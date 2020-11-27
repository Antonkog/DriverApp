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
import com.abona_erp.driverapp.data.model.ResultOfAction
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.data.remote.ResultWrapper
import com.abona_erp.driverapp.data.remote.data
import com.abona_erp.driverapp.data.remote.succeeded
import com.abona_erp.driverapp.data.remote.utils.NetworkUtil
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.events.RxBusEvent
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
     */
    fun checkTimeAndPostActivity(entity: ActivityEntity){
        val allActConfirmTime = 2 * TimeUnit.MINUTES.toMillis(Constant.PAUSE_SERVER_REQUEST_MIN) //2 1 min for posting, 1 min for next act posting
        val lastTimeUpdate = prefs.getLong(Constant.lastConfirmDate, 0L)
        if(lastTimeUpdate != 0L && (System.currentTimeMillis() - lastTimeUpdate < allActConfirmTime)){
            postConfirmationErrorToUI(String.format(context.resources.getString(R.string.error_act_update_time, Constant.PAUSE_SERVER_REQUEST_MIN)))
        }  else  {
            prefs.putAny(Constant.lastConfirmDate, System.currentTimeMillis())
            postActivityChange(entity, true)
        }
    }


    /**
     * post to server that activity change, then
     * if next activity exist in this task - start next activity
     * if next taxt exist in order - start next task without asking driver.
     * Confirm this task with Abona.
     * Start first activity in task.
     *
     */
    private fun postActivityChange(entity: ActivityEntity, startNext: Boolean) {
            val newAct = setNewActivityStatus(entity)
            viewModelScope.launch(IO) {
                Log.d(TAG, " posting activity ${entity.activityId}, time:  ${System.currentTimeMillis()}")
                val result = repository.postActivity(
                    newAct.toActivity(DeviceUtils.getUniqueID(context))
                )

                if (result.succeeded) {
                    if (result.data?.isSuccess == true) {
                        repository.updateActivity(newAct.copy(confirmationType = ActivityConfirmationType.SYNCED_WITH_ABONA))
                        if(startNext) startNextActivity(newAct)
                    } else {
                        postConfirmationErrorToUI(result.toString())
                    }
                } else {
                    if (!NetworkUtil.isConnectedWithWifi(context)) {
                        repository.updateActivity(newAct.copy(confirmationType = ActivityConfirmationType.CHANGED_BY_USER))
                        if(startNext) startNextActivity(newAct)
                    } else{
                        postConfirmationErrorToUI(context.resources.getString(R.string.error_act_update) + result.toString())
                    }
                }
            }
    }

    private suspend fun startNextActivity(
        newAct: ActivityEntity
    ) {
        delay(TimeUnit.MINUTES.toMillis(Constant.PAUSE_SERVER_REQUEST_MIN))
        val nextActStarted = startNextActivityCurrentTask(newAct)

        val taskUpdated = updateParentTask(newAct, nextActStarted)

        taskUpdated?.let {
            if (!nextActStarted) startActivityInNewTask(it)
        }
    }

    private suspend fun startActivityInNewTask(task: TaskEntity) {
        //check for activity in new task
        val taskEntity = repository.getNextTaskIfExist(task)

        taskEntity?.let { next ->
            val newNextTask: TaskEntity = updateTaskStatus(next, true) // true, next task not started has activity

            val resultWrapper: ResultWrapper<ResultOfAction> = confirmTask(newNextTask)

            if (resultWrapper.succeeded) {
                if (resultWrapper.data?.isSuccess == true) {
                    updateTaskConfirmedInDb(newNextTask)
                    repository.getFirstTaskActivity(next)?.let { firstAct ->
                        postActivityChange(firstAct, false)
                    }
                } else {
                    postConfirmationErrorToUI(
                        resultWrapper.data?.text
                            ?: context.resources.getString(R.string.error_task_update)
                    )
                }
            } else {
                if (!NetworkUtil.isConnectedWithWifi(context)) {
                    updateTaskConfirmedInDb(newNextTask)
                    repository.getFirstTaskActivity(next)?.let { firstAct ->
                        postActivityChange(firstAct, false)
                    }
                } else {
                    postConfirmationErrorToUI(context.resources.getString(R.string.error_task_update) + resultWrapper.toString())
                }
            }
        }

    }


    private suspend fun updateParentTask(newAct: ActivityEntity, nextExist: Boolean): TaskEntity? {
        return repository.getParentTask(newAct)?.let { task ->
            val newTask = updateTaskStatus(task, nextExist) //finish task in no next activity
            val updateResult = repository.updateTask(newTask)
            if (updateResult == 0) {
                Log.e(TAG, context.getString(R.string.error_task_update))
                task
            } else {
                Log.d(TAG, context.getString(R.string.success_task_update))
                task
            }
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

    private suspend fun updateTaskConfirmedInDb(nextTask: TaskEntity) {
        repository.updateTask(
            nextTask.copy(
                confirmationType = ConfirmationType.TASK_CONFIRMED_BY_USER
            )
        )
    }

    private suspend fun confirmTask(nextTask: TaskEntity) =
        repository.confirmTask(
            UtilModel.getTaskConfirmation(
                context,
                nextTask.copy(confirmationType = ConfirmationType.TASK_CONFIRMED_BY_USER)
            )
        )


    private fun updateTaskStatus(
        task: TaskEntity,
        nextExist: Boolean
    ): TaskEntity {
        val newStatus = when (task.status) {
            TaskStatus.PENDING -> TaskStatus.RUNNING
            TaskStatus.RUNNING -> {
                if (nextExist) {
                    TaskStatus.RUNNING
                } else {
                    TaskStatus.FINISHED
                }
            }
            TaskStatus.BREAK -> TaskStatus.FINISHED
            TaskStatus.CMR -> TaskStatus.FINISHED
            TaskStatus.FINISHED -> TaskStatus.FINISHED
        }
        return task.copy(status = newStatus)
    }

    private suspend fun startNextActivityCurrentTask(
        entity: ActivityEntity
    ): Boolean {
        val nextAct = repository.getNextActivityIfExist(entity)
        if (nextAct != null && nextAct.activityStatus == ActivityStatus.PENDING) {
            val newNextAct = nextAct.copy(
                activityStatus = ActivityStatus.RUNNING,
                started = System.currentTimeMillis()
            )
            val result = repository.updateActivity(newNextAct) // we update next activity in task, we do not send it to server when start, only when finish
            Log.e(TAG, "local update next activity to:\n $newNextAct  \n result:  $result")
        } else {
            Log.e(TAG, "no next activity")
        }
        return nextAct != null
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