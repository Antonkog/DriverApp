package com.abona_erp.driver.app.ui.delayReason

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.abona_erp.driver.app.data.remote.ApiRepository
import com.abona_erp.driver.app.ui.base.BaseViewModel

class DelayReasonViewModel @ViewModelInject constructor(private val api: ApiRepository) :  BaseViewModel(){}