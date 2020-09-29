package com.abona_erp.driver.app.ui.documents

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abona_erp.driver.app.data.Constant
import com.abona_erp.driver.app.data.model.DMSDocumentType
import com.abona_erp.driver.app.data.model.DocumentResponse
import com.abona_erp.driver.app.data.remote.AppRepository
import com.abona_erp.driver.app.ui.RxBus
import com.abona_erp.driver.app.ui.base.BaseViewModel
import com.abona_erp.driver.app.ui.events.RxBusEvent
import com.abona_erp.driver.app.ui.utils.DeviceUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.w3c.dom.DocumentType
import java.io.File


class DocumentsViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val repository: AppRepository, private val  prefs: SharedPreferences) :  BaseViewModel() {

    val TAG = DocumentsViewModel::class.simpleName
    val documents = MutableLiveData<List<DocumentResponse>>()
    val error  = MutableLiveData<Throwable> ()

    init {
        RxBus.listen(RxBusEvent.DocumentMessage::class.java).subscribe { event->
            viewModelScope.launch {
                Log.e(TAG, " got uri: ${event.uri} ")
                uploadDocuments(DMSDocumentType.POD_CMR, File(event.uri.path))
            }
        }
    }

    fun getDocuments() {
        getDocuments(prefs.getInt(Constant.mandantId, 0), prefs.getInt(Constant.currentVisibleOrderId,0),prefs.getInt(Constant.currentVisibleTaskid,0),  DeviceUtils.getUniqueID(context))
    }

    fun uploadDocuments(documentType: DMSDocumentType, file : File) {

        repository.upladDocument(prefs.getInt(Constant.mandantId, 0), prefs.getInt(Constant.currentVisibleOrderId,0),prefs.getInt(Constant.currentVisibleTaskid,0), -1, documentType.documentType, file)
    }

    private fun getDocuments(mandantId: Int,  orderNo: Int, taskId: Int, deviceId: String) {
        repository.getDocuments(mandantId, orderNo, deviceId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {result ->
                    if(taskId!=0) result.filter { it.taskId == taskId }.let {
                        documents.postValue(it)
                        Log.d(TAG, " got documents filtered by TaskId size" + it.size)
                    }
                    else {
                        Log.e(TAG, " no active taskId to filter")
                        documents.postValue(result)
                    }
                },
                {throwable -> error.postValue(throwable)}
            )
    }
}