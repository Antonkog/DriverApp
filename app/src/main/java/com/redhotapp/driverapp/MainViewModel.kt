package com.redhotapp.driverapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.redhotapp.driverapp.App
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.Constant
import com.redhotapp.driverapp.data.remote.ApiRepository
import com.redhotapp.driverapp.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.redhotapp.driverapp.data.local.preferences.PrivatePreferences
import com.redhotapp.driverapp.data.local.preferences.putAny
import com.redhotapp.driverapp.data.local.preferences.putLong
import com.redhotapp.driverapp.data.model.AllTask
import com.redhotapp.driverapp.ui.RxBus
import com.redhotapp.driverapp.ui.events.RxBusEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class MainViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val api: ApiRepository, private val  prefs: SharedPreferences, @Assisted private val savedStateHandle: SavedStateHandle) :  BaseViewModel() {
    //taskRepo : ApiRepository,
    private val TAG = "MainViewModel"

    fun resetAuthTime(){
        prefs.putLong(context.getString(R.string.token_created),0)
    }

    fun setShowAll(showAll: Boolean) {
        prefs.putAny(context.getString(R.string.pref_show_all), showAll)
    }

    fun getShowAll (): Boolean  {
      return  prefs.getBoolean(context.getString(R.string.pref_show_all), false)
    }
}