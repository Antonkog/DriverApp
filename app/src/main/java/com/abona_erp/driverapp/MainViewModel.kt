package com.abona_erp.driverapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.local.db.ChangeHistory
import com.abona_erp.driverapp.data.local.db.ConfirmationType
import com.abona_erp.driverapp.data.local.db.HistoryDataType
import com.abona_erp.driverapp.data.local.db.TaskEntity
import com.abona_erp.driverapp.data.local.preferences.putAny
import com.abona_erp.driverapp.data.local.preferences.putLong
import com.abona_erp.driverapp.data.model.*
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.data.remote.connection.base.ConnectivityProvider
import com.abona_erp.driverapp.data.remote.data
import com.abona_erp.driverapp.data.remote.succeeded
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.abona_erp.driverapp.ui.utils.DeviceUtils
import com.abona_erp.driverapp.ui.utils.UtilModel.toActivityEntity
import com.abona_erp.driverapp.ui.utils.UtilModel.toDelayReasonEntity
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class MainViewModel @ViewModelInject constructor(
    private val gson: Gson,
    private val repository: AppRepository,
    private val prefs: SharedPreferences,
    @ApplicationContext private val context: Context,
    private val connectivityProvider: ConnectivityProvider,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel(), ConnectivityProvider.ConnectivityStateListener {
    private val TAG = "MainViewModel"

    val vechicle = MutableLiveData<VehicleItem>()
    val requestStatus = MutableLiveData<Status>()
    val authReset = MutableLiveData<Boolean>()
    val connectionChange = MutableLiveData<Boolean>()

    data class Status(val message: String?, val type: StatusType)
    enum class StatusType {
        COMPLETE,
        LOADING,
        ERROR
    }


    init {
        connectivityProvider.addListener(this)

        RxBus.listen(RxBusEvent.RetryRequest::class.java).subscribe { event ->
            retryRequest(event.changeHistory)
        }
        prefs.getString(Constant.currentVechicle, null)?.let {
            try {
                vechicle.postValue(gson.fromJson<VehicleItem>(it, VehicleItem::class.java))
            } catch (e: Exception) {
                Log.e(TAG, " exception during parse FCM message: vechicleNumber")
            }
        }

        RxBus.listen(RxBusEvent.FirebaseMessage::class.java).subscribe { event ->
            viewModelScope.launch(IO) {
                handleFirebaseMessage(event.message)
            }
        }
        RxBus.listen(RxBusEvent.RequestStatus::class.java).subscribe { event ->
            requestStatus.postValue(event.status)
        }

        RxBus.listen(RxBusEvent.AuthError::class.java).subscribe {
            authReset.postValue(true)
        }
        RxBus.listen(RxBusEvent.LanguageUpdate::class.java).subscribe {
            updateDelayReasons(it.locale)
          //  refreshTasks()
        }
    }

    fun doOnConnectionChange(hasInternet: Boolean) {
        val lastChangeTime = prefs.getLong(Constant.lastConnectionChange, 0L)

        if(System.currentTimeMillis() - lastChangeTime > TimeUnit.SECONDS.toMillis(10))// don't show changes more then in 30sec
        {
            prefs.putAny(Constant.lastConnectionChange, System.currentTimeMillis())
            if(hasInternet) resetOfflineRequests()
            else connectionChange.postValue(hasInternet)
        }
    }

    private fun resetOfflineRequests() =  viewModelScope.launch(IO){
        val offline = LinkedList<ChangeHistory>()
        offline.addAll(repository.getAllOfflineRequests())
        //here is basic implementation of sending offline requests.
        if (offline.isNotEmpty()) {
            val disposable = Observable.interval(
                Constant.PAUSE_SERVER_REQUEST_SEC,
                TimeUnit.SECONDS
            )
                .take(offline.size.toLong())
                .doOnNext {
                    retryRequest(offline.first())
                    Log.d(TAG, "sending ${offline.first.dataType}")
                    offline.pop()
                }
                .subscribe {
                    Log.d(TAG, "all offline items was resend")
                }

            disposables.add(disposable)
        }
    }

    private fun retryRequest(changeHistory: ChangeHistory) {
        Log.e(TAG, "retry \n ${changeHistory.dataType}")
        viewModelScope.launch {
            when (changeHistory.dataType) {
                HistoryDataType.CONFIRM_TASK -> {
                    repository.confirmTask(changeHistory)
                }
                HistoryDataType.POST_ACTIVITY -> {
                    repository.postActivity(changeHistory)
                }
                HistoryDataType.POST_DELAY_REASON -> {
                    repository.postDelayReasons(changeHistory)
                }
                HistoryDataType.GET_TASKS -> {
                    repository.updateTasksFromRemoteDataSource(changeHistory)
                }
                else -> {
                    Log.e(TAG, "$changeHistory.dataType - not implemented")
                }
//                HistoryDataType.GET_DOCUMENTS -> TODO()
//                HistoryDataType.UPLOAD_DOCUMENT -> TODO()
            }
        }
    }


    fun resetAuthTime() {
        prefs.putLong(Constant.token_created, 0)
    }

    suspend fun handleFirebaseMessage(message: String) {
        val messageStruct: CommItem = gson.fromJson(message, CommItem::class.java)
        Log.d(TAG, "handleFirebaseMessage: \n $messageStruct")
        when (messageStruct.header.dataType) {
            DataType.VEHICLE.dataType -> {
                Log.d(TAG, " got fcm VEHICLE")
                vechicle.postValue(messageStruct.vehicleItem)
                prefs.putAny(Constant.currentVechicle, gson.toJson(messageStruct.vehicleItem))
            }
            DataType.DOCUMENT.dataType -> {
                Log.d(TAG, " got fcm DOCUMENT")
            }
            DataType.TASK.dataType -> {
                Log.d(TAG, " got fcm task")
                messageStruct.taskItem?.let { taskItem ->
                    Log.d(TAG, " saving fcm task $taskItem")
                    var status =
                        ConfirmationType.RECEIVED // we dont set confirm type on server and loose it when log-out
                    val activeTasks =
                        taskItem.activities.filter { it.status < ActivityStatus.FINISHED.status }// so we looking for all activitys, and if all finished
                    if (activeTasks.isEmpty()) status =
                        ConfirmationType.TASK_CONFIRMED_BY_USER // we change task as confirmed if all activity FINISHED and checkers will be green.

                    repository.insertOrUpdateTask(
                        TaskEntity(
                            taskItem.taskId,
                            taskItem.actionType,
                            taskItem.status,
                            taskItem.activities.map { it.activityId },
                            taskItem.changeReason,
                            taskItem.address,
                            taskItem.contacts,
                            taskItem.dangerousGoods,
                            taskItem.orderDetails,
                            taskItem.taskDetails,
                            taskItem.palletExchange,
                            taskItem.taskDueDateStart.time,
                            taskItem.taskDueDateFinish.time,
                            taskItem.mandantId,
                            taskItem.kundenName,
                            taskItem.notes,
                            status,
                            false
                        )
                    )
                    taskItem.activities.forEach { fcmActivity ->
                        repository.insertOrUpdateActivity(
                            fcmActivity.toActivityEntity()
                        )
                    }
                }
            }

            DataType.DELAY_REASONS.dataType -> {
                Log.d(TAG, " got DELAY_REASONS")

                messageStruct.delayReasonItems?.map { it.toDelayReasonEntity() }?.let {
                    repository.insertDelayReasons(it)
                }
            }

        }
    }


    fun clearDatabase() {
        viewModelScope.launch(IO) {
            repository.cleanDatabase()
        }
    }

    override fun onCleared() {
        super.onCleared()
        connectivityProvider.removeListener(this)
    }

    override fun onStateChange(state: ConnectivityProvider.NetworkState) {
        doOnConnectionChange(state.hasInternet())
    }

    private fun ConnectivityProvider.NetworkState.hasInternet(): Boolean {
        return (this as? ConnectivityProvider.NetworkState.ConnectedState)?.hasInternet == true
    }

    fun updateDelayReasons(locale: Locale ) = viewModelScope.launch(IO){
        val result =  repository.getDelayReasons(
            prefs.getInt(Constant.mandantId, 3),
            DeviceUtils.getLocaleCode(locale)
        )

        if (result.succeeded ){
            result.data?.delayReasonItems?.map { it.toDelayReasonEntity() }?.let {
                repository.insertDelayReasons(it)
            }
        } else{
            Log.e(TAG, "can't update delay reasons from server $result")
        }
    }

}