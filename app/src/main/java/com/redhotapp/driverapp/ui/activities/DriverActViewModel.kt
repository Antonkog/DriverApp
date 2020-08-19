package com.redhotapp.driverapp.ui.activities

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.remote.ApiRepository
import com.redhotapp.driverapp.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.redhotapp.driverapp.data.model.Activity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class DriverActViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val api: ApiRepository, private val  prefs: SharedPreferences) :  BaseViewModel() {  //taskRepo : ApiRepository,

    private val TAG = "HomeViewModel"

    val mutableTasks  = MutableLiveData<List<Activity>> ()
    val error  = MutableLiveData<String> ()

    fun getActivities(deviceId: String) {
        getActivities(deviceId, prefs.getInt(context.resources.getString(R.string.current_visible_taskId), 0))
    }

    private fun getActivities(deviceId: String, taskID : Int) {
        api.getAllTasks(deviceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.e(TAG, result.toString())
                    if(result.isSuccess){
                        Log.e(TAG, "got tasks")
                        mutableTasks.postValue(result.allTask.firstOrNull { it.taskId == taskID }?.activities)
                    }else{
                        error.postValue(result.text)
                    }
                    if(result.isException) {
                        Log.e(TAG, "got tasks is Exception: $result")
                        error.postValue(result.text)
                    }
                },
                { e ->
                    Log.e(TAG, "error while get tasks " +  e.localizedMessage)
                    error.postValue(e.localizedMessage)
                }
            )
    }

}