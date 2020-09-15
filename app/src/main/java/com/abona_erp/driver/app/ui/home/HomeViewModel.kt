package com.abona_erp.driver.app.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.abona_erp.driver.app.data.Constant
import com.abona_erp.driver.app.data.ResultWithStatus
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.data.remote.AppRepository
import com.abona_erp.driver.app.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.abona_erp.driver.app.data.local.preferences.PrivatePreferences
import com.abona_erp.driver.app.data.local.preferences.putAny
import com.abona_erp.driver.app.data.succeeded
import com.abona_erp.driver.app.ui.utils.DeviceUtils
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val app: AppRepository, private val  prefs: SharedPreferences, @Assisted private val savedStateHandle: SavedStateHandle) :  BaseViewModel() {  //taskRepo : ApiRepository,
    private val TAG = "HomeViewModel"

    val mutableTasks  = MutableLiveData<List<TaskEntity>> ()
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

    fun getTasks() {
       mutableTasks.postValue(app.observeTasks(DeviceUtils.getUniqueID(context)).value)
    }

    fun refreshTasks() = viewModelScope.launch {
        //ResultWithStatus<List<TaskEntity>>
      val result =   app.getTasks(true, DeviceUtils.getUniqueID(context))

        if(result is ResultWithStatus.Success){
            if(result.data.isNullOrEmpty()){
                error.postValue("no tasks from server")
            }
            mutableTasks.postValue(result.data)
        }else if(result is ResultWithStatus.Error){
            error.postValue(result.exception.message)
        }
    }

    fun setVisibleTaskID(taskEntity: TaskEntity) {
        Log.e(TAG, "saving task " + taskEntity.taskId)
        prefs.putAny(Constant.currentVisibleTaskid, taskEntity.taskId)
    }

}