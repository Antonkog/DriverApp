package com.abona_erp.driver.app.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.abona_erp.driver.app.data.Constant
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.data.local.db.TaskStatus
import com.abona_erp.driver.app.data.remote.AppRepository
import com.abona_erp.driver.app.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.abona_erp.driver.app.data.local.preferences.PrivatePreferences
import com.abona_erp.driver.app.data.local.preferences.putAny
import com.abona_erp.driver.app.data.model.Activity
import com.abona_erp.driver.app.ui.utils.DeviceUtils
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val repository: AppRepository, private val  prefs: SharedPreferences, @Assisted private val savedStateHandle: SavedStateHandle) :  BaseViewModel() {  //taskRepo : ApiRepository,
    private val TAG = "HomeViewModel"

    val tasks  : LiveData<List<TaskEntity>> = repository.observeTasks(DeviceUtils.getUniqueID(context))
    val filteredTasks  : MutableLiveData<List<TaskEntity>> = MutableLiveData()

    var runningTasks  : List<TaskEntity> = listOf()
    var pendingTasks  : List<TaskEntity> = listOf()
    var completedTasks  : List<TaskEntity> = listOf()
    var currentStatus : Int = 0


    val error  = MutableLiveData<String> ()

    init {
        refreshTasks()
    }

    fun filterRunning(){
        currentStatus = TaskStatus.PENDING.status
        filteredTasks.postValue(runningTasks)
    }

    fun filterPending(){
        currentStatus = TaskStatus.RUNNING.status
        filteredTasks.postValue(runningTasks)
    }

    fun filterCompleted(){
        currentStatus = TaskStatus.FINISHED.status
        filteredTasks.postValue(runningTasks)
    }

    fun setTasks(tasks :List<TaskEntity>) {
        pendingTasks  = tasks.filter { it.status == TaskStatus.PENDING }
        runningTasks  = tasks.filter { it.status == TaskStatus.RUNNING }
        completedTasks  = tasks.filter { it.status == TaskStatus.FINISHED }
        when(currentStatus){
            TaskStatus.PENDING.status -> filteredTasks.postValue(pendingTasks)
            TaskStatus.RUNNING.status -> filteredTasks.postValue(runningTasks)
            TaskStatus.FINISHED.status -> filteredTasks.postValue(completedTasks)
        }
    }


    fun loggedIn(): Boolean {
        val currentTime = System.currentTimeMillis()
        val difference = currentTime - prefs.getLong(Constant. token_created,0)
        Log.i(TAG, "token time difference:  $difference")
        return ((difference < Constant.tokenUpdateHours * 3600 * 1000) // hours to seconds to mills
                && PrivatePreferences.getAccessToken(context) != null)
    }

    fun refreshTasks() = viewModelScope.launch {
        repository.refreshTasks(DeviceUtils.getUniqueID(context))
    }

    fun postActivityChange(activity: Activity ) = viewModelScope.launch {
        repository.postActivity(context, activity)
    }

    fun setVisibleTaskID(taskEntity: TaskEntity) {
        Log.e(TAG, "saving task " + taskEntity.taskId)
        prefs.putAny(Constant.currentVisibleTaskid, taskEntity.taskId)
        prefs.putAny(Constant.currentVisibleOrderId, taskEntity.orderDetails?.orderNo ?: 0)
    }

    fun getVisibleTaskId() =prefs.getInt(Constant.currentVisibleTaskid,0)
}