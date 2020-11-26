package com.abona_erp.driverapp.ui.fdelays

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abona_erp.driverapp.data.local.db.*
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.data.remote.succeeded
import com.abona_erp.driverapp.data.remote.utils.NetworkUtil
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

     val delayReasons = repository.observeDelayReasons()

    fun postDelayReason(delayReasonEntity: DelayReasonEntity) = viewModelScope.launch(IO){
            val result  = repository.postDelayReasons(delayReasonEntity)//here activity to wrap delay when doing rest sync
            if(result.succeeded){
                repository.updateTasksFromRemoteDataSource(null)
                goBack.postValue(true)
            } else {
                if(!NetworkUtil.isConnectedWithWifi(context)){
                    localUpdate(delayReasonEntity)
                } //no else - we assume that errors handled in mainViewModel common Courutine exception handler
            }
    }


    /**
     * used to show user in UI that delay is set and waiting for sync with abona: total time shown inclement. When go online -  we sync app by sending changeHistory.
     */
    suspend fun localUpdate(delayReasonEntity: DelayReasonEntity) {
        repository.getActivity(delayReasonEntity.activityId, delayReasonEntity.taskId, delayReasonEntity.mandantId)?.let { entity ->
            val delays = arrayListOf<DelayReasonEntity>()
           entity.delayReasons?.let {
               delays.addAll(it)
           }
            delays.add(delayReasonEntity)
            val result = repository.updateActivity(entity.copy(delayReasons =  delays))
            if(result == 1)  {
                goBack.postValue(true)
            }
        }
    }

    companion object {
        const val TAG = "DriverReasonViewModel"
    }
}