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
    val error = MutableLiveData<String>()
    val requestStatus = MutableLiveData<Status>()

    data class Status(val message: String?, val type: StatusType)
    enum class StatusType {
        COMPLETE,
        LOADING,
        ERROR
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        exception.message.let {
            Log.e(TasksViewModel.TAG, exception.message ?: " error catch in CoroutineExceptionHandler $exception"  )
            error.postValue(exception.message)
        }
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
            doLogOutActions() //todo: navigate to login check if called from NetworkInterceptor
        }
    }

    fun resetAuthTime() {
        prefs.putLong(Constant.token_created, 0)
    }

    suspend fun handleFirebaseMessage(message: String) {
        val messageStruct: CommItem = gson.fromJson(message, CommItem::class.java)
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
                    repository.insertOrReplaceTask(
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
                            ConfirmationType.RECEIVED,
                            false
                        )
                    )
                    it.activities.forEach {
                            fcmActivity -> repository.insertOrUpdateActivity(fcmActivity.toActivityEntity())
                    }
                }
            }
        }
    }

    fun doLogOutActions() {
        viewModelScope.launch(exceptionHandler){
            resetAuthTime()
            repository.cleanDatabase()
        }
    }
}