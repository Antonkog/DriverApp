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
import com.abona_erp.driverapp.ui.ftasks.TasksViewModel
import com.abona_erp.driverapp.ui.utils.UtilModel.toActivityEntity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.lang.Exception

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

        prefs.getString(Constant.currentVechicle, null)?.let {
            try {
                vechicle.postValue(gson.fromJson<VehicleItem>(it, VehicleItem::class.java))
            }catch (e : Exception){
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
                prefs.putAny(Constant.currentVechicle,   gson.toJson(messageStruct.vehicleItem))
            }
            DataType.DOCUMENT.dataType -> {
                Log.d(TAG, " got fcm DOCUMENT")
            }
            DataType.TASK.dataType -> {
                Log.d(TAG, " got fcm task")
                messageStruct.taskItem?.let {
                    Log.d(TAG, " saving fcm task $it")
                    var status = ConfirmationType.RECEIVED
                    if(status == ConfirmationType.RECEIVED){// we dont set confirm type on server and loose it when log-out
                        val activeTasks = it.activities.filter{it.status < ActivityStatus.FINISHED.status}
                        if(activeTasks.isEmpty())
                            status = ConfirmationType.TASK_CONFIRMED_BY_USER // we change task as confirmed if all activity set
                    }

                    repository.insertOrUpdateTask(
                        TaskEntity(
                            it.taskId,
                            it.actionType,
                            it.status,
                            it.activities.map { it.activityId },
                            it.changeReason,
                            it.address,
                            it.contacts,
                            it.dangerousGoods,
                            it.orderDetails,
                            it.taskDetails,
                            it.palletExchange,
                            it.taskDueDateStart,
                            it.taskDueDateFinish,
                            it.mandantId,
                            it.kundenName,
                            it.notes,
                            status,
                            false
                        )
                    )
                    it.activities.forEach { fcmActivity -> repository.insertOrUpdateActivity(fcmActivity.toActivityEntity())
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