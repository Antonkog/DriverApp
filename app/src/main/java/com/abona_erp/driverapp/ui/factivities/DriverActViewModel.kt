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
import com.abona_erp.driverapp.data.local.db.ActivityWrapper
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

    val error = MutableLiveData<String>()
    val wrappedActivities = MutableLiveData<List<ActivityWrapper>>()


    fun getActivityObservable(): LiveData<List<ActivityEntity>> {
        val taskId = prefs.getInt(Constant.currentVisibleTaskid, 0)
        return repository.observeActivities(taskId)
    }

    fun postActivityChange(wrapper: ActivityWrapper) {
        val entity = wrapper.activity

       val newAct = incremantActivityStatus(entity)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.postActivity(
                    context,
                    newAct.toActivity(DeviceUtils.getUniqueID(context))
                )
                if (result.isSuccess) {
                    repository.updateActivity(newAct)
                    startNextActivity(newAct)

                } else {
                    error.postValue(result.text)
                }



            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "Auth error")
            }
        }
    }

    private suspend fun startNextActivity(
        entity: ActivityEntity
    ) {
        val nextAct = repository.getNextActivityIfExist(entity)
        if (nextAct != null && nextAct.activityStatus == ActivityStatus.PENDING) {
            val newNextAct = nextAct.copy(activityStatus = ActivityStatus.RUNNING)
           val result =  repository.updateActivity(newNextAct)
            Log.e(TAG, "update next activity $result")

        }else{

            Log.e(TAG, "no next activity")
        }
    }

    private fun incremantActivityStatus(entity: ActivityEntity): ActivityEntity {
        return when (entity.activityStatus) {
            ActivityStatus.PENDING -> entity.copy(activityStatus = ActivityStatus.RUNNING)
            ActivityStatus.RUNNING -> entity.copy(activityStatus = ActivityStatus.FINISHED)
            ActivityStatus.FINISHED -> entity
            ActivityStatus.ENUM_ERROR -> entity
        }
    }

    fun wrapActivities(it: List<ActivityEntity>) {
       val firstVisible =  it.firstOrNull{ // if Running exist show next, else show Start if not finished.
            activityEntity ->   activityEntity.activityStatus == ActivityStatus.RUNNING
       } ?:  it.firstOrNull{
               activityEntity ->  activityEntity.activityStatus == ActivityStatus.PENDING
       }

        val pendingNotExist = it.none{ it.activityStatus == ActivityStatus.PENDING }

        val wrapped = it.map { activityEntity ->
            ActivityWrapper(
                activityEntity,
                activityEntity.activityId == firstVisible?.activityId ?: false,
                pendingNotExist
            )}

        wrappedActivities.postValue(wrapped)
    }
}