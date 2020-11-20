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
import com.abona_erp.driverapp.data.local.db.*
import com.abona_erp.driverapp.data.model.ActivityStatus
import com.abona_erp.driverapp.data.model.ResultOfAction
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.data.remote.ResultWrapper
import com.abona_erp.driverapp.data.remote.data
import com.abona_erp.driverapp.data.remote.succeeded
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.abona_erp.driverapp.ui.ftasks.TasksViewModel
import com.abona_erp.driverapp.ui.utils.DeviceUtils
import com.abona_erp.driverapp.ui.utils.UtilModel
import com.abona_erp.driverapp.ui.utils.UtilModel.toActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*

class DriverActViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppRepository,
    private val prefs: SharedPreferences
) : BaseViewModel() {

    val wrappedActivities = MutableLiveData<List<ActivityWrapper>>()

    val currentActivityList = LinkedList<ActivityWrapper>()
    val currentDelayList = LinkedList<DelayReasonEntity>()

    fun getActivityObservable(taskId: Int): LiveData<List<ActivityEntity>> {
        return repository.observeActivities(taskId)
    }


    fun getDelayReasonObservable(): LiveData<List<DelayReasonEntity>> {
        return repository.observeDelayReasons()
    }

    /**
     * post to server that activity change, then
     * if next activity exist in this task - start next activity
     * if next taxt exist in order - start next task without asking driver.
     * Confirm this task with Abona.
     * Start first activity in task.
     *
     */
    fun postActivityChange(wrapper: ActivityWrapper) {
        val entity = wrapper.activity
        val newAct = setNewActivityStatus(entity)
        viewModelScope.launch(IO) {
            val result = repository.postActivity(
                newAct.toActivity(DeviceUtils.getUniqueID(context))
            )

            if (result.succeeded && result.data?.isSuccess == true) { //todo: implement offline mode.
                repository.updateActivity(newAct.copy(confirmationType = ActivityConfirmationType.SYNCED_WITH_ABONA))

                val nextActStarted = startNextActivityCurrentTask(newAct)

                val taskUpdated = updateParentTask(newAct, nextActStarted)

                taskUpdated?.let {
                    if (!nextActStarted) startActivityInNewTask(it)
                }


            } else {
                postConfirmationErrorToUI(result.toString())
                Log.e(TAG, result.toString())
            }
        }
    }

    private suspend fun startActivityInNewTask(task: TaskEntity) {

        //check for activity in new task
        val taskEntity = repository.getNextTaskIfExist(task)

        taskEntity?.let { next ->
            val newNextTask: TaskEntity =
                incrementTaskStatus(next, true) // true, next task not started has activity

            val resultWrapper: ResultWrapper<ResultOfAction> = confirmTask(newNextTask)

            if (resultWrapper.succeeded) {
                if (resultWrapper.data?.isSuccess == true) {
                    updateTaskConfirmedInDb(newNextTask)
                    postNextTaskActivity(next)
                } else {
                    postConfirmationErrorToUI(
                        resultWrapper.data?.text ?: "cant update next task status"
                    )
                }
            } else {
                postConfirmationErrorToUI("cant update next task status $resultWrapper")
                Log.e(TasksViewModel.TAG, "can't update task status on server $resultWrapper")
            }
        }

    }

    private suspend fun updateParentTask(newAct: ActivityEntity, nextExist: Boolean): TaskEntity? {
        return repository.getParentTask(newAct)?.let { task ->
            val newTask = incrementTaskStatus(task, nextExist) //finish task in no next activity
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

    private suspend fun postNextTaskActivity(next: TaskEntity) {
        repository.getFirstTaskActivity(next)?.let { firstAct ->
            val result = repository.postActivity(
                firstAct.toActivity(DeviceUtils.getUniqueID(context))
            )
            if (result.succeeded && result.data?.isSuccess == true) { //todo: implement offline mode.
                Log.e(TAG, "started next task activity (first) : $firstAct")
                repository.updateActivity(firstAct.copy(confirmationType = ActivityConfirmationType.SYNCED_WITH_ABONA))
            } else {
                Log.e(TAG, result.toString())
            }
        }
    }

    private suspend fun confirmTask(nextTask: TaskEntity) =
        repository.confirmTask(
            UtilModel.getTaskConfirmation(
                context,
                nextTask.copy(confirmationType = ConfirmationType.TASK_CONFIRMED_BY_USER)
            )
        )


    private fun incrementTaskStatus(
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
            val result = repository.updateActivity(newNextAct)
            Log.e(TAG, "update next activity to:\n $newNextAct  \n result:  $result")
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
                    started =   System.currentTimeMillis()
                )
            }
            ActivityStatus.RUNNING -> {
                if(entity.started <= 0L){
                    entity.copy(
                        activityStatus = ActivityStatus.FINISHED,
                        started =  System.currentTimeMillis(),
                        finished =  System.currentTimeMillis()
                    )
                }else
                entity.copy(
                    activityStatus = ActivityStatus.FINISHED,
                    finished =   System.currentTimeMillis()
                )
            }
            ActivityStatus.FINISHED -> entity
            ActivityStatus.ENUM_ERROR -> entity
        }
    }

    fun wrapActivities(it: List<ActivityEntity>) {
        currentActivityList.clear()
        val firstVisible =
            it.firstOrNull { // if Running exist show next, else show Start if not finished.
                    activityEntity ->
                activityEntity.activityStatus == ActivityStatus.RUNNING
            } ?: it.firstOrNull { activityEntity ->
                activityEntity.activityStatus == ActivityStatus.PENDING
            }

        val pendingNotExist = it.none { it.activityStatus == ActivityStatus.PENDING }

        val wrapped = it.map { activityEntity ->
            ActivityWrapper(
                activityEntity.copy(delayReasons = currentDelayList.filter {it.activityId == activityEntity.activityId }),
                activityEntity.activityId == firstVisible?.activityId ?: false,
                pendingNotExist
            )
        }

        currentActivityList.addAll(wrapped)
        wrappedActivities.postValue(currentActivityList)
    }

    fun addDelaysToActivities(delays: List<DelayReasonEntity>) {
        if(delays.isNotEmpty()){
            currentDelayList.clear()
            currentDelayList.addAll(delays)

            val newList = LinkedList<ActivityWrapper>()

            currentActivityList.forEach { activityWrapper ->
                newList.add(
                    activityWrapper.copy(activity =
                    activityWrapper.activity.copy(delayReasons =
                    delays.filter { it.activityId == activityWrapper.activity.activityId })))}

            currentActivityList.clear()
            currentActivityList.addAll(newList)
            wrappedActivities.postValue(currentActivityList)
        }
    }

    companion object {
        const val TAG = "DriverActViewModel"
    }
}