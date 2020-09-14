package com.abona_erp.driver.app.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.abona_erp.driver.app.data.remote.ApiRepository
import com.abona_erp.driver.app.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.abona_erp.driver.app.data.model.Activity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class SettingsViewModel @ViewModelInject constructor(private val api: ApiRepository) :  BaseViewModel() {  //taskRepo : ApiRepository,

    private val TAG = "SettingsViewModel"
}