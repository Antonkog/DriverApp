package com.abona_erp.driver.app.ui.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.abona_erp.driver.app.data.Constant
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.data.remote.AppRepository
import com.abona_erp.driver.app.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class DriverActViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val repository: AppRepository, private val  prefs: SharedPreferences) :  BaseViewModel() {  //taskRepo : ApiRepository,

    private val TAG = "DriverActViewModel"

    val mutableTasks  = MutableLiveData<List<ActivityEntity>> ()
    val error  = MutableLiveData<String> ()

    fun getActivities(deviceId: String) {
        getActivities(deviceId, prefs.getInt(Constant.currentVisibleTaskid, 0))
    }

    private fun getActivities(deviceId: String, taskID : Int) {
        mutableTasks.postValue(repository.observeActivities(deviceId).value?.filter { it.taskpId == taskID })
    }
}