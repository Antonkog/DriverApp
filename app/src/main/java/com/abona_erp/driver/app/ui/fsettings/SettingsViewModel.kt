package com.abona_erp.driver.app.ui.fsettings

import androidx.hilt.lifecycle.ViewModelInject
import com.abona_erp.driver.app.data.remote.AppRepository
import com.abona_erp.driver.app.ui.base.BaseViewModel

class SettingsViewModel @ViewModelInject constructor(private val app: AppRepository) :  BaseViewModel() {  //taskRepo : ApiRepository,

    private val TAG = "SettingsViewModel"
}