package com.abona_erp.driverapp.ui.fprotocol

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.abona_erp.driverapp.data.local.db.ChangeHistory
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.ui.base.BaseViewModel

class ProtocolViewModel @ViewModelInject constructor(repository: AppRepository) :
    BaseViewModel() {  //taskRepo : ApiRepository

    val requests: LiveData<List<ChangeHistory>> = repository.observeChangeHistory()

    companion object {
        const val TAG = "ProtocolViewModel"
    }

}