package com.redhotapp.driverapp.ui.home

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.redhotapp.driverapp.data.remote.ApiRepository
import com.redhotapp.driverapp.data.remote.ApiService
import com.redhotapp.driverapp.data.remote.rabbitMQ.RabbitService

class HomeViewModel @ViewModelInject constructor(api: ApiRepository, gson: Gson, @Assisted private val savedStateHandle: SavedStateHandle) : BaseViewModel() {  //taskRepo : ApiRepository,

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}