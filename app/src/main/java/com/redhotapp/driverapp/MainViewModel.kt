package com.redhotapp.driverapp

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import com.redhotapp.driverapp.data.Constant
import com.redhotapp.driverapp.data.remote.ApiRepository
import com.redhotapp.driverapp.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.redhotapp.driverapp.data.local.preferences.putAny
import com.redhotapp.driverapp.data.local.preferences.putLong

class MainViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val api: ApiRepository, private val  prefs: SharedPreferences, @Assisted private val savedStateHandle: SavedStateHandle) :  BaseViewModel() {
    //taskRepo : ApiRepository,
    private val TAG = "MainViewModel"

    fun resetAuthTime(){
        prefs.putLong(Constant.token_created,0)
    }

    fun setShowAll(showAll: Boolean) {
        prefs.putAny(Constant.prefShowAll, showAll)
    }

    fun getShowAll (): Boolean  {
      return  prefs.getBoolean(Constant.prefShowAll, false)
    }
}