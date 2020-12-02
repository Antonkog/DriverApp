package com.abona_erp.driverapp.ui.fdocuments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.abona_erp.driverapp.MainViewModel
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.local.db.DocumentEntity
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.abona_erp.driverapp.ui.utils.DeviceUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.*


class DocumentsViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppRepository,
    private val prefs: SharedPreferences
) : BaseViewModel() {

    val TAG = DocumentsViewModel::class.simpleName


    //    val filteredDocuments : LiveData<List<DocumentEntity>> = repository.observeDocuments(prefs.getInt(Constant.currentVisibleTaskid,0))

    init {
        RxBus.listen(RxBusEvent.DocumentMessage::class.java).subscribe { event ->
            viewModelScope.launch {
                uploadDocuments(event.documents)
            }
        }
    }

    fun observeDocuments(taskId: Int): LiveData<List<DocumentEntity>> {
        return repository.observeDocuments(taskId)
    }


    fun refreshDocuments(mandantId: Int, orderNo: Int, deviceId: String) =
        viewModelScope.launch(IO) {
            repository.refreshDocuments(mandantId, orderNo, deviceId)
        }

    private fun uploadDocuments(documentList: ArrayList<UploadDocumentItem>) {
        var resultCount = 0
        for (document in documentList) {
            val inputStream: InputStream? =
                context.contentResolver.openInputStream(document.uri)
            if (inputStream == null) Log.e(TAG, "can't open document")
            (inputStream)?.use {
                RxBus.publish(
                    RxBusEvent.RequestStatus(
                        MainViewModel.Status(
                            null,
                            MainViewModel.StatusType.LOADING
                        )
                    )
                )
                repository.upladDocument(
                    document.mandantId,
                    document.orderNo,
                    document.taskId,
                    -1,
                    document.documentType.documentType,
                    document.uri
                ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                    resultCount++
                    if (resultCount == documentList.size) {
                        Log.e(TAG, "upload result : $it")
                        RxBus.publish(
                            RxBusEvent.RequestStatus(
                                MainViewModel.Status(
                                    it.toString(),
                                    MainViewModel.StatusType.COMPLETE
                                )
                            )
                        )
                        refreshDocuments(
                            prefs.getInt(Constant.mandantId, 0),
                            prefs.getInt(Constant.currentVisibleOrderId, 0),
                            DeviceUtils.getUniqueID(context)
                        ) //to get vehicle number etc.
                    } else {
                        Log.e(TAG, "Waiting for other document to upload")
                    }
                }, {
                    resultCount++
                    if (resultCount == documentList.size) {
                        RxBus.publish(
                            RxBusEvent.RequestStatus(
                                MainViewModel.Status(
                                    it.message,
                                    MainViewModel.StatusType.ERROR
                                )
                            )
                        )
                    }
                })
            }
        }
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val parcelFileDescriptor: ParcelFileDescriptor? =
            context.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor?.close()
        return image
    }

    @Throws(IOException::class)
    private fun readTextFromUri(uri: Uri): String? {

        val parcelFileDescriptor: ParcelFileDescriptor? =
            context.contentResolver.openFileDescriptor(uri, "r")

        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val reader = BufferedReader(
            InputStreamReader(
                inputStream
            )
        )
        val stringBuilder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
        inputStream?.close()
        parcelFileDescriptor?.close()
        return stringBuilder.toString()
    }
}