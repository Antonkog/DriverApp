package com.redhotapp.driverapp.ui.activities

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.redhotapp.driverapp.data.remote.ApiRepository
import com.redhotapp.driverapp.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.redhotapp.driverapp.data.model.Activity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class DriverActViewModel @ViewModelInject constructor(private val api: ApiRepository) :  BaseViewModel() {  //taskRepo : ApiRepository,

    private val TAG = "HomeViewModel"

    val mutableTasks  = MutableLiveData<List<Activity>> ()
    val error  = MutableLiveData<String> ()

    fun populateActivities(deviceId: String, taskID : Int) {
        api.getAllTasks(deviceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.e(TAG, result.toString())
                    if(result.isSuccess){
                        Log.e(TAG, "got tasks")
                        mutableTasks.postValue( result.allTask.filter { it.taskId == taskID }.firstOrNull()?.activities)
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