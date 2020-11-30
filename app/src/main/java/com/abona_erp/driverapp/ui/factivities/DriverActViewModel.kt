package com.abona_erp.driverapp.ui.factivities

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abona_erp.driverapp.MainViewModel
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.local.db.*
import com.abona_erp.driverapp.data.local.preferences.putAny
import com.abona_erp.driverapp.data.model.ActivityStatus
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.data.remote.data
import com.abona_erp.driverapp.data.remote.succeeded
import com.abona_erp.driverapp.data.remote.utils.NetworkUtil
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.abona_erp.driverapp.ui.ftasks.TasksViewModel
import com.abona_erp.driverapp.ui.utils.DeviceUtils
import com.abona_erp.driverapp.ui.utils.UtilModel
import com.abona_erp.driverapp.ui.utils.UtilModel.toActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class DriverActViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppRepository,
    private val prefs: SharedPreferences
) : BaseViewModel() {

    val wrappedActivities = MutableLiveData<List<ActivityWrapper>>()

    fun getActivityObservable(taskId: Int): LiveData<List<ActivityEntity>> {
        return repository.observeActivities(taskId)
    }


    /**
     * this method is to fix condition, when server sends 200 ok and it is not changed database.
     * so when change next activity server say "preveous was not changed if we are not waiting,
     * this time will be managed on settings screen as "SERVER_DELAY"
     *
     * post to server that activity change, then
     * if next activity exist in this task - start next activity
     * if next taxt exist in order - start next task without asking driver.
     * Confirm this task with Abona.
     * Start first activity in task.
     */

    fun checkTimePostActChange(entity: ActivityWrapper){
        val allActConfirmTime = TimeUnit.SECONDS.toMillis(Constant.PAUSE_SERVER_REQUEST_SEC) //2 1 min for posting, 1 min for next act posting
        val lastTimeUpdate = prefs.getLong(Constant.lastConfirmDate, 0L)
        if(lastTimeUpdate != 0L && (System.currentTimeMillis() - lastTimeUpdate < allActConfirmTime)){
            postConfirmationErrorToUI(String.format(context.resources.getString(R.string.error_act_update_time, Constant.PAUSE_SERVER_REQUEST_SEC)))
        }  else  {
            viewModelScope.launch(IO) {
                prefs.putAny(Constant.lastConfirmDate, System.currentTimeMillis())
                postActivityChange(entity.activity)
                //now do only local update, and send next time, or wait 1 min and then request.
              if(NetworkUtil.isConnectedWithWifi(context))  delay(allActConfirmTime) //that delay is for server next activity or task confirmation
                if(entity.isLastActivity){
                    val nextTask = repository.getTask(entity.activity.taskpId +1, entity.activity.mandantId)
                    nextTask?.let {
                        confirmTask(nextTask)
                        if(NetworkUtil.isConnectedWithWifi(context))  delay(allActConfirmTime) //that delay is for server next activity confirmation
                       val firstActNextTask =  repository.getFirstTaskActivity(nextTask)
                        firstActNextTask?.let {
                            postActivityChange(it)
                        }
                    }
                } else {
                    //getting next activity assume that nextID is current +1
                  val nextAct =   repository.getActivity(entity.activity.activityId +1 , entity.activity.taskpId, entity.activity.mandantId )
                    nextAct?.let { postActivityChange(it) }
                }
            }
        }
    }


    private suspend fun postActivityChange(entity: ActivityEntity) : Boolean {
        val newAct = setNewActivityStatus(entity)
        return if (NetworkUtil.isConnectedWithWifi(context)) {
            val result = repository.postActivity(newAct.toActivity(DeviceUtils.getUniqueID(context)))
            if (result.succeeded && result.data?.isSuccess == true) {
                repository.updateActivity(newAct.copy(confirmationType = ActivityConfirmationType.SYNCED_WITH_ABONA))
                true
            } else {
                postConfirmationErrorToUI(context.resources.getString(R.string.error_act_update) + result.toString())
                false
            }
        } else {
            repository.saveActivityPost(newAct.toActivity(DeviceUtils.getUniqueID(context)))
            repository.updateActivity(newAct.copy(confirmationType = ActivityConfirmationType.CHANGED_BY_USER))
            true
        }
    }


    private fun postConfirmationErrorToUI(text: String) {
        RxBus.publish(
            RxBusEvent.RequestStatus(
                MainViewModel.Status(
                    text,
                    MainViewModel.StatusType.ERROR
                )
            )
        )
    }



    fun confirmTask(taskEntity: TaskEntity) = viewModelScope.launch {
        val newTask =  UtilModel.getTaskConfirmation(
            context,
            taskEntity.copy(confirmationType = ConfirmationType.TASK_CONFIRMED_BY_USER)
        )

        if(NetworkUtil.isConnectedWithWifi(context)){ //app is not connected so we are opening this task, but we dont confirm it
            val result = repository.confirmTask(newTask)
            if (result.succeeded && result.data?.isSuccess == true) {//common errors like no networks
               repository.updateTask(taskEntity.copy(confirmationType = ConfirmationType.TASK_CONFIRMED_BY_USER, openCondition = !taskEntity.openCondition))
            } else {
                postConfirmationErrorToUI(result.toString())
            }
        } else {
            repository.saveConfirmTask(newTask)
            val updateResult =  repository.updateTask(taskEntity.copy(
                openCondition = !taskEntity.openCondition
            ))
            Log.e(TasksViewModel.TAG, "task update result: $updateResult")
        }
    }

    private fun setNewActivityStatus(entity: ActivityEntity): ActivityEntity {
        return when (entity.activityStatus) {
            ActivityStatus.PENDING -> {
                entity.copy(
                    activityStatus = ActivityStatus.RUNNING,
                    started = System.currentTimeMillis()
                )
            }
            ActivityStatus.RUNNING -> {
                if (entity.started <= 0L) {
                    entity.copy(
                        activityStatus = ActivityStatus.FINISHED,
                        started = System.currentTimeMillis(),
                        finished = System.currentTimeMillis()
                    )
                } else
                    entity.copy(
                        activityStatus = ActivityStatus.FINISHED,
                        finished = System.currentTimeMillis()
                    )
            }
            ActivityStatus.FINISHED -> entity
        }
    }

    fun wrapActivities(it: List<ActivityEntity>) {
        val firstVisible =
            it.firstOrNull { // if Running exist show next, else show Start if not finished.
                    activityEntity ->
                activityEntity.activityStatus == ActivityStatus.RUNNING
            } ?: it.firstOrNull { activityEntity ->
                activityEntity.activityStatus == ActivityStatus.PENDING
            }

        val lastActivity = it.none { it.activityStatus == ActivityStatus.PENDING } && it.filter {it.activityStatus == ActivityStatus.RUNNING}.size == 1

        val wrapped = it.map { activityEntity ->
            ActivityWrapper(
                activityEntity,
                activityEntity.activityId == firstVisible?.activityId ?: false,
                lastActivity
            )
        }

        wrappedActivities.postValue(wrapped)
    }

    companion object {
        const val TAG = "DriverActViewModel"
    }
}