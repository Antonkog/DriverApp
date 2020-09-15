package com.abona_erp.driver.app.ui.delayReason

import androidx.hilt.lifecycle.ViewModelInject
import com.abona_erp.driver.app.data.remote.AppRepository
import com.abona_erp.driver.app.ui.base.BaseViewModel

class DelayReasonViewModel @ViewModelInject constructor(private val repository: AppRepository) :  BaseViewModel(){}