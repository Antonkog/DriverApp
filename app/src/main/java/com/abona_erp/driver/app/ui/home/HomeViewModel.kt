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
import com.abona_erp.driver.app.data.ResultWithStatus
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.data.local.db.TaskEntity
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
    val error  = MutableLiveData<String> ()

    init {
        refreshTasks()
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