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
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.abona_erp.driverapp.ui.utils.UtilModel.toActivityEntity
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
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

    private var connectionHistory: Pair<Boolean, Long>? = null

    val vechicle = MutableLiveData<VehicleItem>()
    val requestStatus = MutableLiveData<Status>()
    val authReset = MutableLiveData<Boolean>()

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
    }


    fun doOnConnectionChange(hasInternet: Boolean) {
        val justChanged =
            if (connectionHistory == null) {
                connectionHistory = Pair(hasInternet, System.currentTimeMillis())
                false
            } else {
                System.currentTimeMillis() - connectionHistory!!.second < TimeUnit.SECONDS.toMillis(Constant.CONNECTION_VELOCITY_SEC)
            }

        if (hasInternet && !justChanged)
            viewModelScope.launch {
                val offline = LinkedList<ChangeHistory>()
                offline.addAll(repository.getAllOfflineRequests())
                //here is basic implementation of sending offline requests.
                if (offline.isNotEmpty()) {
                    val disposable = io.reactivex.Observable.interval(
                        Constant.PAUSE_SERVER_REQUEST_MIN,
                        TimeUnit.MINUTES
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
                HistoryDataType.GET_TASKS -> {
                    repository.getTasks(true, changeHistory)
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
}