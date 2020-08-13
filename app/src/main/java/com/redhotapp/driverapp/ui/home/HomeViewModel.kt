package com.redhotapp.driverapp.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.Constant
import com.redhotapp.driverapp.data.remote.ApiRepository
import com.redhotapp.driverapp.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.redhotapp.driverapp.data.local.preferences.PrivatePreferences
import com.redhotapp.driverapp.data.local.preferences.putAny
import com.redhotapp.driverapp.data.model.AllTask
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class HomeViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val api: ApiRepository, private val  prefs: SharedPreferences, @Assisted private val savedStateHandle: SavedStateHandle) :  BaseViewModel() {  //taskRepo : ApiRepository,
    private val TAG = "HomeViewModel"

    val mutableTasks  = MutableLiveData<List<AllTask>> ()
    val error  = MutableLiveData<String> ()

    fun loggedIn(): Boolean {
        val currentTime = System.currentTimeMillis()
        val difference = currentTime - prefs.getLong(context.getString(R.string.token_created),0)
        Log.i(TAG, "token time difference:  $difference")
        return ((difference < Constant.tokenUpdateHours * 3600 * 1000) // hours to seconds to mills
                && PrivatePreferences.getAccessToken(context) != null)
    }

    fun populateTasks(deviceId: String) {
        api.getAllTasks(deviceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.e(TAG, result.toString())
                    if(result.isSuccess){
                        Log.e(TAG, "got tasks")
                        mutableTasks.postValue(result.allTask)
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

    fun setVisibleTaskID(allTask: AllTask) {
        prefs.putAny(context.resources.getString(R.string.current_visible_taskId), allTask.taskId)
    }

}