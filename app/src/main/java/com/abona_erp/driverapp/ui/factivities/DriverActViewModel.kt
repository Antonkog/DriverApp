package com.abona_erp.driverapp.ui.factivities

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.local.db.ActivityEntity
import com.abona_erp.driverapp.data.model.ActivityStatus
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.utils.DeviceUtils
import com.abona_erp.driverapp.ui.utils.UtilModel.toActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DriverActViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppRepository,
    private val prefs: SharedPreferences
) : BaseViewModel() {  //taskRepo : ApiRepository,

    private val TAG = "DriverActViewModel"

    //    val mutableActitities  = MutableLiveData<List<ActivityEntity>> ()
    val error = MutableLiveData<String>()

//    fun getActivities(deviceId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//
//            mutableActitities.postValue(repository.observeActivities(deviceId).value?.filter { it.taskpId == taskId })
//        }
//    }

    fun getActivityObservable(): LiveData<List<ActivityEntity>> {
        val taskId = prefs.getInt(Constant.currentVisibleTaskid, 0)
        return repository.observeActivities(taskId)
    }

    fun postActivityChange(entity: ActivityEntity) {
       val newAct =  when(entity.activityStatus){
           ActivityStatus.PENDING -> entity.copy(activityStatus = ActivityStatus.RUNNING)
           ActivityStatus.RUNNING -> entity.copy(activityStatus = ActivityStatus.FINISHED)
           ActivityStatus.FINISHED -> entity
           ActivityStatus.ENUM_ERROR -> entity
       }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.postActivity(
                    context,
                    newAct.toActivity(DeviceUtils.getUniqueID(context))
                )
                if(result.isSuccess){
                    repository.updateActivity(newAct)
                }else{
                    error.postValue(result.text)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "Auth error")
            }
        }
    }
}