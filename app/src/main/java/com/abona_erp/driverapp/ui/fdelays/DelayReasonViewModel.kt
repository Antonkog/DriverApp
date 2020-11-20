package com.abona_erp.driverapp.ui.fdelays

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.abona_erp.driverapp.data.local.db.*
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.utils.UtilModel.toDelayReason
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class DelayReasonViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppRepository,
    private val prefs: SharedPreferences
) : BaseViewModel() {

    fun postDelayReason(delayReasonItem :  DelayReasonEntity ) = viewModelScope.launch(IO){
        repository.postDelayReason(delayReasonItem.toDelayReason() )
    }

    companion object {
        const val TAG = "DriverReasonViewModel"
    }
}