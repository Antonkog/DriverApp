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
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.local.db.TaskStatus
import com.abona_erp.driverapp.data.local.preferences.PrivatePreferences
import com.abona_erp.driverapp.data.local.preferences.putAny
import com.abona_erp.driverapp.data.model.Activity
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.utils.DeviceUtils
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

    var runningTasks: LinkedList<TaskWithActivities> = LinkedList()
    var pendingTasks: LinkedList<TaskWithActivities> = LinkedList()
    var completedTasks: LinkedList<TaskWithActivities> = LinkedList()

    var currentStatus: Int = 0


    fun filterRunning() {
        currentStatus = TaskStatus.RUNNING.intId
        postTasksToFragmentByStatus()
        Log.d(TAG, "posting Running " + runningTasks.size)
    }

    fun filterPending() {
        currentStatus = TaskStatus.PENDING.intId
        postTasksToFragmentByStatus()
        Log.d(TAG, "posting Pending " + pendingTasks.size)
    }

    fun filterCompleted() {
        currentStatus = TaskStatus.FINISHED.intId
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
        when (currentStatus) {
            TaskStatus.PENDING.intId -> filteredTasks.postValue(pendingTasks)
            TaskStatus.RUNNING.intId -> filteredTasks.postValue(runningTasks)
            TaskStatus.FINISHED.intId -> filteredTasks.postValue(completedTasks)
            TaskStatus.CMR.intId -> Log.e(TAG, "error  TaskStatus.CMR.status - not implemented")
            TaskStatus.BREAK.intId -> Log.e(TAG, "error  TaskStatus.BREAK.status - not implemented"
            )
        }
    }


    private fun divideTasksAndActivityByStatus(tasks: List<TaskWithActivities>) {
        if (!tasks.isNullOrEmpty()) {
            pendingTasks.clear()
            runningTasks.clear()
            completedTasks.clear()
            tasks.forEach {
                when (it.taskEntity.status) {
                    TaskStatus.PENDING -> {
                        pendingTasks.add(it)
                    }
                    TaskStatus.RUNNING -> {
                        runningTasks.add(it)
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
            && PrivatePreferences.getAccessToken(context) != null) true
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

    fun clearVisibleTaskId() {
        prefs.putAny(Constant.currentVisibleTaskid, 0)
        prefs.putAny(  Constant.currentVisibleOrderId,0)
    }

    fun setVisibleTaskIDs(TaskWithActivities: TaskWithActivities) {
        Log.d(TAG, "saving task visible" + TaskWithActivities.taskEntity.taskId)
        prefs.putAny(Constant.currentVisibleTaskid, TaskWithActivities.taskEntity.taskId)
        prefs.putAny(
            Constant.currentVisibleOrderId,
            TaskWithActivities.taskEntity.orderDetails?.orderNo ?: 0
        )
    }

    fun getVisibleTaskId() = prefs.getInt(Constant.currentVisibleTaskid, 0)

    companion object {
        const val TAG = "TasksViewModel"
    }
}