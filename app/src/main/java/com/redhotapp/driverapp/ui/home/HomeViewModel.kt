package com.redhotapp.driverapp.ui.home

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.redhotapp.driverapp.data.Constant
import com.redhotapp.driverapp.data.remote.ApiRepository
import com.redhotapp.driverapp.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.redhotapp.driverapp.data.local.preferences.Preferences
import com.redhotapp.driverapp.ui.login.LoginViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class HomeViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val api: ApiRepository, private val  gson: Gson, @Assisted private val savedStateHandle: SavedStateHandle) :  BaseViewModel() {  //taskRepo : ApiRepository,

    private val TAG = "HomeViewModel"

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun loggedIn(): Boolean {
        return Preferences.getAccessToken(context) != null
    }

    fun getAllTasks(deviceId: String) {
        api.getAllTasks(deviceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.e(TAG, result.toString())
                    Log.e(TAG, "got tasks")
                    _text.value = result.toString()
                },
                { error ->
                    Log.e(TAG, error.localizedMessage)
                }

            )

    }


}