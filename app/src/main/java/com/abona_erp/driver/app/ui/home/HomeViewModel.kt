package com.abona_erp.driver.app.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.abona_erp.driver.app.data.Constant
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.data.local.db.TaskStatus
import com.abona_erp.driver.app.data.local.preferences.PrivatePreferences
import com.abona_erp.driver.app.data.local.preferences.putAny
import com.abona_erp.driver.app.data.model.Activity
import com.abona_erp.driver.app.data.remote.AppRepository
import com.abona_erp.driver.app.ui.base.BaseViewModel
import com.abona_erp.driver.app.ui.utils.DeviceUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.util.*


class HomeViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppRepository,
    private val prefs: SharedPreferences,
    @Assisted private val savedStateHandle: SavedStateHandle
) :  BaseViewModel() {  //taskRepo : ApiRepository,
    private val TAG = "HomeViewModel"

    val tasks  : LiveData<List<TaskWithActivities>> = repository.observeTaskWithActivities()

    val filteredTasks  : MutableLiveData<List<TaskWithActivities>> = MutableLiveData()

    var runningTasks  : LinkedList<TaskWithActivities> = LinkedList()
    var pendingTasks  : LinkedList<TaskWithActivities> = LinkedList()
    var completedTasks  : LinkedList<TaskWithActivities> = LinkedList()

    var currentStatus : Int = 0


    val error  = MutableLiveData<String> ()

    init {
        refreshTasks()
    }

    fun filterRunning(){
        currentStatus = TaskStatus.RUNNING.status
        postTasksToFragmentByStatus()
        Log.e(TAG, "posting Running ")
    }

    fun filterPending(){
        currentStatus = TaskStatus.PENDING.status
        postTasksToFragmentByStatus()
        Log.e(TAG, "posting Pending ")
    }

    fun filterCompleted(){
        currentStatus = TaskStatus.FINISHED.status
        postTasksToFragmentByStatus()
        Log.e(TAG, "posting Completed ")
    }


    fun setTasks(tasks: List<TaskWithActivities>) {
        if (!tasks.isNullOrEmpty()) {
            divideTasksAndActivityByStatus(tasks)
            postTasksToFragmentByStatus()
        }
    }

    private fun postTasksToFragmentByStatus() {
        when (currentStatus) {
            TaskStatus.PENDING.status -> filteredTasks.postValue(pendingTasks)
            TaskStatus.RUNNING.status -> filteredTasks.postValue(runningTasks)
            TaskStatus.FINISHED.status -> filteredTasks.postValue(completedTasks)
            TaskStatus.CMR.status -> filteredTasks.postValue(runningTasks)
            TaskStatus.BREAK.status -> filteredTasks.postValue(runningTasks)
        }
    }


    private fun divideTasksAndActivityByStatus(tasks: List<TaskWithActivities>) {
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
            }
        }
    }
    fun loggedIn(): Boolean {
        val currentTime = System.currentTimeMillis()
        val difference = currentTime - prefs.getLong(Constant.token_created, 0)
        Log.i(TAG, "token time difference:  $difference")
        return ((difference < Constant.tokenUpdateHours * 3600 * 1000) // hours to seconds to mills
                && PrivatePreferences.getAccessToken(context) != null)
    }

    fun refreshTasks() = viewModelScope.launch {
        repository.refreshTasks(DeviceUtils.getUniqueID(context))
    }

    fun postActivityChange(activity: Activity) = viewModelScope.launch {
        repository.postActivity(context, activity)
    }

    fun setVisibleTaskIDs(TaskWithActivities: TaskWithActivities) {
        Log.e(TAG, "saving task " + TaskWithActivities.taskEntity.taskId)
        prefs.putAny(Constant.currentVisibleTaskid, TaskWithActivities.taskEntity.taskId)
        prefs.putAny(
            Constant.currentVisibleOrderId,
            TaskWithActivities.taskEntity.orderDetails?.orderNo ?: 0
        )
    }

    fun getVisibleTaskId() =prefs.getInt(Constant.currentVisibleTaskid, 0)
}