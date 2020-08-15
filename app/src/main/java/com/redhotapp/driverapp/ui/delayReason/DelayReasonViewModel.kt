package com.redhotapp.driverapp.ui.delayReason

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.redhotapp.driverapp.data.remote.ApiRepository
import com.redhotapp.driverapp.ui.base.BaseViewModel

class DelayReasonViewModel @ViewModelInject constructor(private val api: ApiRepository) :  BaseViewModel(){}