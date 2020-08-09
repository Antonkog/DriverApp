package com.redhotapp.driverapp.ui.activities

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.redhotapp.driverapp.data.model.AllTask
import com.redhotapp.driverapp.data.model.abona.ActivityItem

class DriverActViewModel : ViewModel() {
    val mutableTasks  = MutableLiveData<List<ActivityItem>> ()



}