package com.abona_erp.driverapp.ui.ftasks

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.abona_erp.driverapp.MainViewModel
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.local.db.ConfirmationType
import com.abona_erp.driverapp.data.local.db.TaskEntity
import com.abona_erp.driverapp.data.local.db.TaskStatus
import com.abona_erp.driverapp.data.local.preferences.PrivatePreferences
import com.abona_erp.driverapp.data.local.preferences.putAny
import com.abona_erp.driverapp.data.model.Activity
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.data.remote.data
import com.abona_erp.driverapp.data.remote.succeeded
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.abona_erp.driverapp.ui.utils.DeviceUtils
import com.abona_erp.driverapp.ui.utils.UtilModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.util.*


class TasksViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppRepository,
    private val prefs: SharedPreferences,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val tasks: LiveData<List<TaskWithActivities>> = repository.observeTaskWithActivities()

    val filteredTasks: MutableLiveData<List<TaskWithActivities>> = MutableLiveData()

    var todoTasks: LinkedList<TaskWithActivities> = LinkedList()
    var completedTasks: LinkedList<TaskWithActivities> = LinkedList()

    var tabStatus: TabStatus = TabStatus.TO_DO

    enum class TabStatus {
        TO_DO,
        COMPLETED
    }

    fun filterTodo() {
        tabStatus = TabStatus.TO_DO
        postTasksToFragmentByStatus()
        Log.d(TAG, "posting TO do " + todoTasks.size)
    }

    fun filterCompleted() {
        tabStatus = TabStatus.COMPLETED
        postTasksToFragmentByStatus()
        Log.d(TAG, "posting Completed " + completedTasks.size)
    }


    fun setTasks(tasks: List<TaskWithActivities>) {
        if (!tasks.isNullOrEmpty()) {
            divideTasksAndActivityByStatus(tasks)
            postTasksToFragmentByStatus()
        }
    }

    private fun postTasksToFragmentByStatus() {
        when (tabStatus) {
            TabStatus.TO_DO -> filteredTasks.postValue(todoTasks)
            TabStatus.COMPLETED -> filteredTasks.postValue(completedTasks)
        }
    }


    private fun divideTasksAndActivityByStatus(tasks: List<TaskWithActivities>) {
        if (!tasks.isNullOrEmpty()) {
            todoTasks.clear()
            completedTasks.clear()
            tasks.forEach {
                when (it.taskEntity.status) {
                    TaskStatus.PENDING -> {
                        todoTasks.add(it)
                    }
                    TaskStatus.RUNNING -> {
                        todoTasks.add(it)
                    }
                    TaskStatus.FINISHED -> {
                        completedTasks.add(it)
                    }
                    TaskStatus.BREAK, TaskStatus.CMR -> {
                        Log.e(TAG, " TaskStatus.BREAK , TaskStatus.CMR not implemented")
                    }
                }
            }
        }

    }

    fun loggedIn(): Boolean {
        val currentTime = System.currentTimeMillis()
        val difference = currentTime - prefs.getLong(Constant.token_created, 0)

        return if ((difference < Constant.tokenUpdateHours * 3600 * 1000) // hours to seconds to mills
            && PrivatePreferences.getAccessToken(context) != null
        ) true
        else {
            Log.d(TAG, "auth expired, token time difference:  $difference")
            false
        }
    }

    fun refreshTasks() = viewModelScope.launch {
        repository.refreshTasks(DeviceUtils.getUniqueID(context))
    }

    //to post from task fragment use
    fun postActivityChange(activity: Activity) = viewModelScope.launch {
        repository.postActivity(context, activity)
    }


    fun setVisibleTaskIDs(TaskWithActivities: TaskWithActivities) {
        Log.d(TAG, "saving task visible" + TaskWithActivities.taskEntity.taskId)
        prefs.putAny(Constant.currentVisibleTaskid, TaskWithActivities.taskEntity.taskId)
        TaskWithActivities.taskEntity.orderDetails?.orderNo?.let {
            prefs.putAny(Constant.currentVisibleOrderId, it)
        }
    }

    fun getVisibleTaskId() = prefs.getInt(Constant.currentVisibleTaskid, 0)

    fun updateTask(data: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(data)
        }
    }

    fun confirmTask(taskEntity: TaskEntity) = viewModelScope.launch {
        val result =
            repository.confirmTask(
                context,
                UtilModel.getTaskConfirmation(
                    context,
                    taskEntity.copy(confirmationType = ConfirmationType.TASK_CONFIRMED_BY_USER)
                )
            )
        if (result.succeeded) {
            if (result.data?.isSuccess == true) {
                updateTask(
                    taskEntity.copy(
                        confirmationType = ConfirmationType.TASK_CONFIRMED_BY_USER,
                        openCondition = !taskEntity.openCondition
                    )
                )//green checkers
            } else {
                RxBus.publish(
                    RxBusEvent.RequestStatus(
                        MainViewModel.Status(
                            result.data?.text,
                            MainViewModel.StatusType.ERROR
                        )
                    )
                )
            }
        } else {
            Log.e(TAG, "can't update task status on server $result")

            //todo: put in offline confirmations table here
            // or when requesting and delete on error
        }
    }

    companion object {
        const val TAG = "TasksViewModel"
    }
}