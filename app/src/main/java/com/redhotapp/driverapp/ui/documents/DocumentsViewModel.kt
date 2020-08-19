package com.redhotapp.driverapp.ui.documents

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.local.preferences.putAny
import com.redhotapp.driverapp.data.model.abona.AppFileInterchangeItem
import com.redhotapp.driverapp.data.remote.ApiRepository
import com.redhotapp.driverapp.ui.base.BaseViewModel
import com.redhotapp.driverapp.ui.login.LoginViewModel
import com.redhotapp.driverapp.ui.utils.DeviceUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class DocumentsViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val api: ApiRepository, private val  prefs: SharedPreferences) :  BaseViewModel() {

    val documents = MutableLiveData<List<AppFileInterchangeItem>>()
    val error  = MutableLiveData<String> ()

    fun getDocuments() {
        getDocuments(prefs.getInt(context.resources.getString(R.string.mandantId), 0), prefs.getInt(context.resources.getString(R.string.current_visible_taskId),0), DeviceUtils.getUniqueID(context))
    }

    private fun getDocuments(mandantId: Int,  orderNo: Int, deviceId: String) {
        api.getDocuments(mandantId, orderNo, deviceId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {result ->
                    documents.postValue(result)},
                {throwable -> error.postValue(throwable.message)}
            )
    }
}