package com.abona_erp.driverapp

import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.local.db.ConfirmationType
import com.abona_erp.driverapp.data.local.db.HistoryDataType
import com.abona_erp.driverapp.data.local.db.TaskEntity
import com.abona_erp.driverapp.data.local.preferences.putAny
import com.abona_erp.driverapp.data.local.preferences.putLong
import com.abona_erp.driverapp.data.model.ActivityStatus
import com.abona_erp.driverapp.data.model.CommItem
import com.abona_erp.driverapp.data.model.DataType
import com.abona_erp.driverapp.data.model.VehicleItem
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.abona_erp.driverapp.ui.utils.UtilModel.toActivityEntity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val gson: Gson,
    private val repository: AppRepository,
    private val prefs: SharedPreferences,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    private val TAG = "MainViewModel"

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
        RxBus.listen(RxBusEvent.RetryRequest::class.java).subscribe { event ->
            retryRequest(event)
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


    private fun retryRequest(event: RxBusEvent.RetryRequest) {
        viewModelScope.launch {
            when (event.changeHistory.dataType) {
                HistoryDataType.CONFIRM_TASK -> {
                    reConfirmTask(event.changeHistory.params)
                }
                HistoryDataType.POST_ACTIVITY -> {
                    rePostActivity(event.changeHistory.params)
                }
                HistoryDataType.GET_TASKS -> {
                    repository.refreshTasks(event.changeHistory.params)
                }
                HistoryDataType.SET_DEVICE_PROFILE -> {
                    reSetDeviceProfile(event.changeHistory.params)
                }
                HistoryDataType.FCM_TASK -> TODO()
                HistoryDataType.FCM_DOCUMENT -> TODO()
                HistoryDataType.FCM_VEHICLE -> TODO()
                HistoryDataType.AUTH -> TODO()
                HistoryDataType.GET_DOCUMENTS -> TODO()
                HistoryDataType.UPLOAD_DOCUMENT -> TODO()
            }
        }
    }

    private fun reSetDeviceProfile(params: String) {
        TODO("Not yet implemented")
    }

    private fun reConfirmTask(params: String) {
        TODO("Not yet implemented")
    }

    private fun rePostActivity(changeHistoryParams: String) {
        TODO("Not yet implemented")
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
                            taskItem.taskDueDateStart,
                            taskItem.taskDueDateFinish,
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
}