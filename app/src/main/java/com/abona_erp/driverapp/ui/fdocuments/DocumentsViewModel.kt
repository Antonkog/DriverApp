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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.local.db.DocumentEntity
import com.abona_erp.driverapp.data.model.DMSDocumentType
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.abona_erp.driverapp.ui.utils.DeviceUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.io.*


class DocumentsViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppRepository,
    private val prefs: SharedPreferences
) : BaseViewModel() {

    val TAG = DocumentsViewModel::class.simpleName
    val documents: LiveData<List<DocumentEntity>> =
        repository.observeDocuments(prefs.getInt(Constant.currentVisibleTaskid, 0))

    //    val filteredDocuments : LiveData<List<DocumentEntity>> = repository.observeDocuments(prefs.getInt(Constant.currentVisibleTaskid,0))
    val error = MutableLiveData<Throwable>()


    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        //  users.postValue(Resource.error("Something Went Wrong", null))
        Log.e(TAG, exception.message ?: " error catch in CoroutineExceptionHandler $exception"  )
        error.postValue(exception)
    }


    init {
        refreshDocuments(
            prefs.getInt(Constant.mandantId, 0),
            prefs.getInt(Constant.currentVisibleOrderId, 0),
            DeviceUtils.getUniqueID(context)
        )
        RxBus.listen(RxBusEvent.DocumentMessage::class.java).subscribe { event ->
            viewModelScope.launch(exceptionHandler) {
                Log.e(TAG, " 123 got uri: ${event.uri} ")
                uploadDocuments(DMSDocumentType.POD_CMR, event.uri)
            }
        }
    }


    fun refreshDocuments(mandantId: Int, orderNo: Int, deviceId: String) =
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.refreshDocuments(mandantId, orderNo, deviceId)
            } catch (e: Throwable) {
                error.postValue(e)
            }
        }

    fun uploadDocuments(documentType: DMSDocumentType, uri: Uri) {
//        val fileUri: Uri? = try {
//            FileProvider.getUriForFile(
//                context,
//                "com.abona_erp.driverapp.fileprovider",
//                File(uri.path))
//        } catch (e: IllegalArgumentException) {
//            Log.e("File Selector",
//                "The selected file can't be shared: $uri")
//            null
//        }

        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        if (inputStream != null)
            repository.upladDocument(
                prefs.getInt(Constant.mandantId, 0),
                prefs.getInt(Constant.currentVisibleOrderId, 0),
                prefs.getInt(Constant.currentVisibleTaskid, 0),
                -1,
                documentType.documentType,
                inputStream
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "upload result : $it")
                    refreshDocuments(
                        prefs.getInt(Constant.mandantId, 0),
                        prefs.getInt(Constant.currentVisibleOrderId, 0),
                        DeviceUtils.getUniqueID(context)
                    ) //to get vehicle number etc.
                }, {
                    error.postValue(it)
                })
        else Log.e(TAG, "can't open document")
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