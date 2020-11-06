package com.abona_erp.driverapp.ui.factivities

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.*
import com.abona_erp.driverapp.data.model.ActivityStatus
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.data.remote.data
import com.abona_erp.driverapp.data.remote.succeeded
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.utils.DeviceUtils
import com.abona_erp.driverapp.ui.utils.UtilModel.getCurrentDateServerFormat
import com.abona_erp.driverapp.ui.utils.UtilModel.toActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class DriverActViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppRepository,
    private val prefs: SharedPreferences
) : BaseViewModel() {

    val wrappedActivities = MutableLiveData<List<ActivityWrapper>>()

    fun getActivityObservable(taskId: Int): LiveData<List<ActivityEntity>> {
        return repository.observeActivities(taskId)
    }

    fun postActivityChange(wrapper: ActivityWrapper) {
        val entity = wrapper.activity
        val newAct = setNewActivityStatus(entity)
        viewModelScope.launch(IO) {
            val result = repository.postActivity(
                context,
                newAct.toActivity(DeviceUtils.getUniqueID(context))
            )
            if (result.succeeded && result.data?.isSuccess == true) { //todo: implement offline mode.
                repository.updateActivity(newAct)
                val nextStarted = (startNextActivityCurrentTask(newAct) != null)
                updateParentTask(newAct, nextStarted)
            } else {
                Log.e(TAG, result.toString())
            }
        }
    }

    private suspend fun updateParentTask(newAct: ActivityEntity, nextExist: Boolean) {
        repository.getParentTask(newAct)?.let { task ->
            incrementTaskStatusAndUpdate(task, nextExist) //finish task in no next activity
            if(!nextExist){//check for activity in new task
                val  nextTask  = repository.getNextTaskIfExist(task)

                nextTask?.let {next->
                    repository.getFirstTaskActivity(next)?.let {firstAct->
                        repository.updateActivity(setNewActivityStatus(firstAct))
                        incrementTaskStatusAndUpdate(next, true) // true, as we found first activity
                    }
                }
            }
        }
    }

    private suspend fun incrementTaskStatusAndUpdate(
        task: TaskEntity,
        nextExist: Boolean
    ) {
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
        if (task.status != newStatus) {
            val result = repository.updateTask(task.copy(status = newStatus))
            if (result == 0) {
                Log.e(TAG, context.getString(R.string.error_task_update))
            } else {
                Log.d(TAG, context.getString(R.string.success_task_update) + newStatus)
            }
        }
    }

    private suspend fun startNextActivityCurrentTask(
        entity: ActivityEntity
    ) : ActivityEntity? {
        val nextAct = repository.getNextActivityIfExist(entity)
        if (nextAct != null && nextAct.activityStatus == ActivityStatus.PENDING) {
            val newNextAct = nextAct.copy(
                activityStatus = ActivityStatus.RUNNING,
                started = getCurrentDateServerFormat()
            )
            val result = repository.updateActivity(newNextAct)
            Log.e(TAG, "update next activity to:\n $newNextAct  \n result:  $result")
        } else {
            Log.e(TAG, "no next activity")
        }
        return nextAct
    }

    private fun setNewActivityStatus(entity: ActivityEntity): ActivityEntity {
        return when (entity.activityStatus) {
            ActivityStatus.PENDING -> {
                entity.copy(
                    activityStatus = ActivityStatus.RUNNING,
                    started = getCurrentDateServerFormat()
                )
            }
            ActivityStatus.RUNNING -> {
                entity.copy(
                    activityStatus = ActivityStatus.FINISHED,
                    started = getCurrentDateServerFormat(),
                    finished = getCurrentDateServerFormat()
                )
            }
            ActivityStatus.FINISHED -> entity
            ActivityStatus.ENUM_ERROR -> entity
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

        val pendingNotExist = it.none { it.activityStatus == ActivityStatus.PENDING }

        val wrapped = it.map { activityEntity ->
            ActivityWrapper(
                activityEntity,
                activityEntity.activityId == firstVisible?.activityId ?: false,
                pendingNotExist
            )
        }

        wrappedActivities.postValue(wrapped)
    }

    companion object {
        const val TAG = "DriverActViewModel"
    }
}