package com.abona_erp.driverapp.ui.fprotocol

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.abona_erp.driverapp.data.local.db.RequestEntity
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.ui.base.BaseViewModel

class ProtocolViewModel @ViewModelInject constructor(private val repository: AppRepository) :
    BaseViewModel() {  //taskRepo : ApiRepository
    private val TAG = "SettingsViewModel"

    val requests: LiveData<List<RequestEntity>> = repository.observeRequests()

}