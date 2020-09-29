package com.abona_erp.driver.app.ui.documents

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abona_erp.driver.app.data.Constant
import com.abona_erp.driver.app.data.local.db.DocumentEntity
import com.abona_erp.driver.app.data.model.DMSDocumentType
import com.abona_erp.driver.app.data.remote.AppRepository
import com.abona_erp.driver.app.ui.RxBus
import com.abona_erp.driver.app.ui.base.BaseViewModel
import com.abona_erp.driver.app.ui.events.RxBusEvent
import com.abona_erp.driver.app.ui.utils.DeviceUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.io.File


class DocumentsViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val repository: AppRepository, private val  prefs: SharedPreferences) :  BaseViewModel() {

    val TAG = DocumentsViewModel::class.simpleName
    val documents : LiveData<List<DocumentEntity>> = repository.observeDocuments(prefs.getInt(Constant.currentVisibleTaskid,0))
//    val filteredDocuments : LiveData<List<DocumentEntity>> = repository.observeDocuments(prefs.getInt(Constant.currentVisibleTaskid,0))
    val error  = MutableLiveData<Throwable> ()

    init {
        refreshDocuments(prefs.getInt(Constant.mandantId, 0), prefs.getInt(Constant.currentVisibleOrderId,0),  DeviceUtils.getUniqueID(context))
        RxBus.listen(RxBusEvent.DocumentMessage::class.java).subscribe { event->
            viewModelScope.launch {
                Log.e(TAG, " got uri: ${event.uri} ")
                context.contentResolver.takePersistableUriPermission(
                    event.uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                uploadDocuments(DMSDocumentType.POD_CMR, event.uri)
            }
        }
    }

    fun refreshDocuments(mandantId: Int,  orderNo: Int, deviceId: String) = viewModelScope.launch {
        viewModelScope.launch {
            try {
                repository.refreshDocuments(mandantId,orderNo,deviceId)
            }catch (e : Throwable){
                error.postValue(e)
            }
        }
    }

    fun uploadDocuments(documentType: DMSDocumentType, uri: Uri) {
        repository.upladDocument(prefs.getInt(Constant.mandantId, 0), prefs.getInt(Constant.currentVisibleOrderId,0),prefs.getInt(Constant.currentVisibleTaskid,0), -1, documentType.documentType, File(uri.path))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "upload result : $it")
            }, {
                error.postValue(it)
            })
    }
}