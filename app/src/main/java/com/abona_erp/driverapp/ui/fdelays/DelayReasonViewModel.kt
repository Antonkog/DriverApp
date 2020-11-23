package com.abona_erp.driverapp.ui.fdelays

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abona_erp.driverapp.data.local.db.*
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.data.remote.succeeded
import com.abona_erp.driverapp.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class DelayReasonViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppRepository,
    private val prefs: SharedPreferences
) : BaseViewModel() {

     val goBack = MutableLiveData<Boolean>()
     val error = MutableLiveData<String>()

     val delayReasons = repository.observeDelayReasons()

    fun postDelayReason(delayReasonItem: ActivityEntity) = viewModelScope.launch(IO){
      val result  = repository.postDelayReason(delayReasonItem)
       if(result.succeeded){
           goBack.postValue(true)
       } else{
           error.postValue(result.toString())
       }
    }

    companion object {
        const val TAG = "DriverReasonViewModel"
    }
}