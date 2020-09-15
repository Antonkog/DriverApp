package com.abona_erp.driver.app.ui.documents

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.abona_erp.driver.app.data.Constant
import com.abona_erp.driver.app.data.model.AppFileInterchangeItem
import com.abona_erp.driver.app.data.remote.AppRepository
import com.abona_erp.driver.app.ui.base.BaseViewModel
import com.abona_erp.driver.app.ui.utils.DeviceUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class DocumentsViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val repository: AppRepository, private val  prefs: SharedPreferences) :  BaseViewModel() {

    val documents = MutableLiveData<List<AppFileInterchangeItem>>()
    val error  = MutableLiveData<String> ()

    fun getDocuments() {
        getDocuments(prefs.getInt(Constant.mandantId, 0), prefs.getInt(Constant.currentVisibleTaskid,0), DeviceUtils.getUniqueID(context))
    }

    private fun getDocuments(mandantId: Int,  orderNo: Int, deviceId: String) {
        repository.getDocuments(mandantId, orderNo, deviceId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {result ->
                    documents.postValue(result)},
                {throwable -> error.postValue(throwable.message)}
            )
    }
}