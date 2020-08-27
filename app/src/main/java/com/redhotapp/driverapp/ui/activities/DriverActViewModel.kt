package com.redhotapp.driverapp.ui.activities

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.redhotapp.driverapp.data.Constant
import com.redhotapp.driverapp.data.local.db.ActivityEntity
import com.redhotapp.driverapp.data.local.db.TaskEntity
import com.redhotapp.driverapp.data.remote.ApiRepository
import com.redhotapp.driverapp.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.redhotapp.driverapp.data.model.Activity
import com.redhotapp.driverapp.ui.utils.DeviceUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class DriverActViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val api: ApiRepository, private val  prefs: SharedPreferences) :  BaseViewModel() {  //taskRepo : ApiRepository,

    private val TAG = "DriverActViewModel"

    val mutableTasks  = MutableLiveData<List<ActivityEntity>> ()
    val error  = MutableLiveData<String> ()

    fun getActivities(deviceId: String) {
        getActivities(deviceId, prefs.getInt(Constant.currentVisibleTaskid, 0))
    }

    private fun getActivities(deviceId: String, taskID : Int) {
        mutableTasks.postValue(api.getAllActivity(DeviceUtils.getUniqueID(context)).value?.filter { it.taskpId == taskID })
    }
}