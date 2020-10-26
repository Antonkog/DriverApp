package com.abona_erp.driverapp.ui.fsettings

import androidx.hilt.lifecycle.ViewModelInject
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.ui.base.BaseViewModel

class SettingsViewModel @ViewModelInject constructor(private val app: AppRepository) :
    BaseViewModel() {  //taskRepo : ApiRepository,

    private val TAG = "SettingsViewModel"
}