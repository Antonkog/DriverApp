package com.abona_erp.driver.app.ui.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abona_erp.driver.app.data.Constant
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.data.remote.AppRepository
import com.abona_erp.driver.app.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DriverActViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val repository: AppRepository, private val  prefs: SharedPreferences) :  BaseViewModel() {  //taskRepo : ApiRepository,

    private val TAG = "DriverActViewModel"

//    val mutableActitities  = MutableLiveData<List<ActivityEntity>> ()
    val error  = MutableLiveData<String> ()

//    fun getActivities(deviceId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//
//            mutableActitities.postValue(repository.observeActivities(deviceId).value?.filter { it.taskpId == taskId })
//        }
//    }

    fun getActivityObservable() : LiveData<List<ActivityEntity>>{
        val taskId= prefs.getInt(Constant.currentVisibleTaskid, 0)
        return repository.observeActivities(taskId)
    }
}