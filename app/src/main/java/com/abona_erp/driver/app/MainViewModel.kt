package com.abona_erp.driver.app

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.abona_erp.driver.app.data.Constant
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.data.remote.AppRepository
import com.abona_erp.driver.app.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.abona_erp.driver.app.data.local.preferences.putAny
import com.abona_erp.driver.app.data.local.preferences.putLong
import com.abona_erp.driver.app.data.model.CommItem
import com.abona_erp.driver.app.data.model.CommResponseItem
import com.abona_erp.driver.app.data.model.DataType
import com.abona_erp.driver.app.ui.RxBus
import com.abona_erp.driver.app.ui.events.RxBusEvent
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val gson: Gson, private val repository: AppRepository, private val  prefs: SharedPreferences, @Assisted private val savedStateHandle: SavedStateHandle) :  BaseViewModel() {
    //taskRepo : ApiRepository,
    private val TAG = "MainViewModel"
    init {
        RxBus.listen(RxBusEvent.FirebaseMessage::class.java).subscribe { event->
            viewModelScope.launch {
                handleFirebaseMessage(event.message)
            }
        }
    }

    fun resetAuthTime(){
        prefs.putLong(Constant.token_created,0)
    }

    fun setShowAll(showAll: Boolean) {
        prefs.putAny(Constant.prefShowAll, showAll)
    }

    fun getShowAll (): Boolean  {
      return  prefs.getBoolean(Constant.prefShowAll, false)
    }

     suspend fun handleFirebaseMessage(message : String){
         val messageStruct  : CommItem = gson.fromJson(message, CommItem::class.java)
         when(messageStruct.header.dataType){
             DataType.VEHICLE.dataType -> {
                 Log.d(TAG, " got fcm VEHICLE")
             }
             DataType.DOCUMENT.dataType -> {
                 Log.d(TAG, " got fcm DOCUMENT")
             }
             DataType.TASK.dataType -> {
                 Log.d(TAG, " got fcm task")
                 messageStruct.taskItem?.let {
                     Log.d(TAG, " saving fcm task")
                     repository.saveTask( TaskEntity(
                         it.taskId, it.status, it.activities.map { it.activityId },
                         it.changeReason, it.address, it.orderDetails, it.palletExchange,
                         false,  it.taskDueDateStart, it.taskDueDateFinish, it.mandantId, it.kundenName
                     ))
                 }
             }
         }

    }
}