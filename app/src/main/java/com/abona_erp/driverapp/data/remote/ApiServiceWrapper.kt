package com.abona_erp.driverapp.data.remote

import android.content.Context
import android.util.Log
import com.abona_erp.driverapp.MainViewModel
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.LocalDataSource
import com.abona_erp.driverapp.data.local.db.*
import com.abona_erp.driverapp.data.local.db.HistoryDataType.*
import com.abona_erp.driverapp.data.model.*
import com.abona_erp.driverapp.data.remote.utils.NetworkUtil
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.abona_erp.driverapp.ui.utils.DeviceUtils
import com.abona_erp.driverapp.ui.utils.UtilModel
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Created by Anton Kogan email: Akogan777@gmail.com on 11/9/2020
 */
class ApiServiceWrapper(
    private val api: ApiService,
    private val localDataSource: LocalDataSource,
    private val context: Context,
    val gson: Gson
) {

    suspend fun authentication(
        authModel: UtilModel.AuthModel
    ): ResultWrapper<TokenResponse> {
        return authentication(authModel, null)
    }

    private suspend fun authentication(
        authModel: UtilModel.AuthModel, changeHistory: ChangeHistory?
    ): ResultWrapper<TokenResponse> {
        val change = prerapeChangeChistory( gson.toJson(authModel), changeHistory, AUTH)
        val autoGenId = changeHistory?.id ?: localDataSource.insertHistoryChange(change)
        sentLoadingToUI()
        return try {
            val result = ResultWrapper.Success(
                api.authentication(
                    authModel.grantType,
                    authModel.userName,
                    authModel.password
                )
            )
            sendSuccessToUI(result.toString())
            updateHistoryOnSuccess(change, gson.toJson(result), autoGenId)
            result
        } catch (ex: Exception) {
            sendErrorToUI(ex)
            updateHistoryOnError(change, autoGenId)
            ResultWrapper.Error(ex)
        }
    }

    /**
     * that method exception is handled in AppRepositoryImpl
     * so no need to send exception to UI here and no try/catch block
     */
    suspend fun updateTasksFromServer(changeHistory: ChangeHistory?): ResultWrapper<CommResponseItem> {
        val id = DeviceUtils.getUniqueID(context)
        val change = prerapeChangeChistory(id, changeHistory, GET_TASKS)
        val autoGenId = changeHistory?.id ?: localDataSource.insertHistoryChange(change)
       return try {
            val result = ResultWrapper.Success(api.getAllTasks(id))
           if(result.data.isSuccess){
               localDataSource.updateFromCommItem(result.data)
               updateHistoryOnSuccess(change, gson.toJson(result.data), autoGenId)
               result
           } else {
               val ex  = java.lang.Exception(result.data.text ?: context.getString(R.string.error_get_tasks))
               sendErrorToUI(ex)
               ResultWrapper.Error(ex)
           }
        } catch (ex: Exception){
           if(NetworkUtil.isConnectedWithWifi(context)){
               sendErrorToUI(ex)
           }
           updateHistoryOnError(change, autoGenId)
           ResultWrapper.Error(ex)
        }
    }


    /**
     * that method exception is handled in AppRepositoryImpl - need to rewrite after merge
     * so no need to send exception to UI here and no try/catch block now
     */
    suspend fun updateDocumentsFromServer(
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    ) {
        val resp = api.getDocuments(mandantId, orderNo, deviceId)

        resp.body()?.let {
            if (it.isNotEmpty()) {
                localDataSource.deleteDocuments()
                localDataSource.insertDocumentResponse(it)
            }
        }
    }


    suspend fun setDeviceProfile(commItem: CommItem): ResultWrapper<ResultOfAction> {
        return setDeviceProfile(commItem, null)
    }



    suspend fun postDelayItems(commItem: CommItem): ResultWrapper<ResultOfAction> {
        return postDelayItems(commItem, null)
    }

    suspend fun postDelayItems(changeHistory: ChangeHistory): ResultWrapper<ResultOfAction> {
        val item = gson.fromJson(changeHistory.params, CommItem::class.java)
        return postDelayItems(item, changeHistory)
    }

    suspend fun postDelayItems(commItem: CommItem, changeHistory: ChangeHistory?): ResultWrapper<ResultOfAction> {
        sentLoadingToUI()
        val change = prerapeChangeChistory(gson.toJson(commItem),changeHistory, POST_DELAY_REASON)
        val autoGenId = changeHistory?.id ?: localDataSource.insertHistoryChange(change)
        return try {
            val result = api.postDelayItems(commItem)
            updateHistoryOnSuccess(change, gson.toJson(result), autoGenId)
            sendSuccessToUI(result.toString())
            ResultWrapper.Success(result)
        } catch (ex: Exception) {
            updateHistoryOnError(change, autoGenId)
            sendErrorToUI(ex)
            ResultWrapper.Error(ex)
        }
    }


    suspend fun getDelayItems(mandantId: Int, langCode : String): ResultWrapper<ResultOfAction> {
        sentLoadingToUI()
        return try {
            val result = api.getDelayReasons(mandantId, langCode)
            sendSuccessToUI(result.toString())
            ResultWrapper.Success(result)
        } catch (e :java.lang.Exception){
            ResultWrapper.Error(e)
        }
    }

    private suspend fun setDeviceProfile(
        commItem: CommItem,
        changeHistory: ChangeHistory?
    ): ResultWrapper<ResultOfAction> {
        sentLoadingToUI()
        val change = prerapeChangeChistory( gson.toJson(commItem),changeHistory, SET_DEVICE_PROFILE)
        val autoGenId = changeHistory?.id ?: localDataSource.insertHistoryChange(change)
        return try {
            val result = api.setDeviceProfile(commItem)
            sendSuccessToUI(result.toString())
            updateHistoryOnSuccess(change, gson.toJson(result), autoGenId)
            ResultWrapper.Success(result)
        } catch (ex: Exception) {
            sendErrorToUI(ex)
            updateHistoryOnError(change, autoGenId)
            return ResultWrapper.Error(ex)
        }
    }


    suspend fun postActivityChange(commItem: CommItem): ResultWrapper<ResultOfAction> {
        return postActivityChange(commItem, null)
    }

    suspend fun postActivityChange(changeHistory: ChangeHistory): ResultWrapper<ResultOfAction> {
        val item = gson.fromJson(changeHistory.params, CommItem::class.java)
        return postActivityChange(item, changeHistory)
    }

    suspend fun saveActivityPost(commItem: CommItem) :Boolean{
        val change = prerapeChangeChistory(gson.toJson(commItem), null, POST_ACTIVITY)
        val result =  localDataSource.insertHistoryChange(change.copy(status =  Status.SENT_OFFLINE))
        return result == 1L
    }

    /**
     * Post activity and rewrite database logs
     * @param commItem - request body based on this param
     * @param changeHistory - if you set this id  -  that makes db to rewrite old object, so use it for rewrite errors only.
     */
    private suspend fun postActivityChange(
        commItem: CommItem,
        changeHistory: ChangeHistory?
    ): ResultWrapper<ResultOfAction> {
        sentLoadingToUI()
        val change = prerapeChangeChistory(gson.toJson(commItem),changeHistory, POST_ACTIVITY)
        val autoGenId = changeHistory?.id ?: localDataSource.insertHistoryChange(change)
        return try {
            val result = api.postActivityChange(commItem)
            updateHistoryOnSuccess(change, gson.toJson(result), autoGenId)
            sendSuccessToUI(result.toString())
            ResultWrapper.Success(result)
        } catch (ex: Exception) {
            updateHistoryOnError(change, autoGenId)
            sendErrorToUI(ex)
            ResultWrapper.Error(ex)
        }
    }


    suspend fun confirmTask(changeHistory: ChangeHistory): ResultWrapper<ResultOfAction> {
        val item = gson.fromJson(changeHistory.params, CommItem::class.java)
        return confirmTask(item, changeHistory)
    }

    suspend fun confirmTask(commItem: CommItem): ResultWrapper<ResultOfAction> {
        return confirmTask(commItem, null)
    }

    suspend fun saveConfirmTask(commItem: CommItem): Boolean {
        val change = prerapeChangeChistory(gson.toJson(commItem), null, CONFIRM_TASK)
        val result =  localDataSource.insertHistoryChange(change.copy(status =  Status.SENT_OFFLINE))
        return result == 1L
    }

    private suspend fun confirmTask(
        commItem: CommItem,
        changeHistory: ChangeHistory?
    ): ResultWrapper<ResultOfAction> {
        val change = prerapeChangeChistory(gson.toJson(commItem), changeHistory, CONFIRM_TASK)
        val autoGenId = changeHistory?.id ?: localDataSource.insertHistoryChange(change)
        return try {
            val result = api.confirmTask(commItem)
            updateHistoryOnSuccess(change, gson.toJson(result), autoGenId)
            sendSuccessToUI(result.toString())
            ResultWrapper.Success(result)
        } catch (ex: java.lang.Exception) {
            updateHistoryOnError(change, autoGenId)
            sendErrorToUI(ex)
            ResultWrapper.Error(ex)
        }
    }

    fun uploadDocument(
        mandantId: Int,
        orderNo: Int,
        taskID: Int,
        driverNo: Int,
        documentType: Int,
        inputStream: InputStream
    ): Single<UploadResult> {
        val mandantBody = mandantId.toMultipartBody()
        val orderBody = orderNo.toMultipartBody()
        val taskBody = taskID.toMultipartBody()
        val driverBody = driverNo.toMultipartBody()
        val docTypeBody = documentType.toMultipartBody()
        // val ims : InputStream =  file.inputStream()

        val newFIle = File.createTempFile(
            "abona",
            ".pdf",
            context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
        )

        try {
            FileUtils.copyToFile(inputStream, newFIle)
        } catch (e: IOException) {
            Log.e(TAG, "can't send document")
        }


        val fileBody = newFIle.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//        val out =  IOUtils.toByteArray(ims).toRequestBody()
//        val name = ""+ System.currentTimeMillis() + ".dpf"

        val multiparFileBody = MultipartBody.Part.createFormData(
            name = "files[]",
            filename = newFIle.name,
            body = fileBody
        )

        return api.uploadDocument(
            mandantBody,
            orderBody,
            taskBody,
            driverBody,
            docTypeBody,
            multiparFileBody
        )
    }


    private suspend fun updateHistoryOnError(
        change: ChangeHistory,
        autoGenId: Long
    ) {
        localDataSource.updateHistoryChange(
            change.copy(
                status = if (NetworkUtil.isConnectedWithWifi(context)) Status.ERROR else Status.SENT_OFFLINE,
                id = autoGenId
            )
        )
    }


    private suspend fun updateHistoryOnSuccess(
        change: ChangeHistory,
        result: String,
        autoGenId: Long
    ) {
        localDataSource.updateHistoryChange(
            change.copy(
                status = Status.SUCCESS,
                response = result,
                id = autoGenId
            )
        )
    }

    private fun sentLoadingToUI() {
        RxBus.publish(
            RxBusEvent.RequestStatus(
                MainViewModel.Status(
                    null,
                    MainViewModel.StatusType.LOADING
                )
            )
        )
    }

    private fun sendErrorToUI(ex: Exception) {
        RxBus.publish(
            RxBusEvent.RequestStatus(
                MainViewModel.Status(
                    ex.message,
                    MainViewModel.StatusType.ERROR
                )
            )
        )
    }

    private fun sendSuccessToUI(result: String) {
        RxBus.publish(
            RxBusEvent.RequestStatus(
                MainViewModel.Status(
                    result,
                    MainViewModel.StatusType.COMPLETE
                )
            )
        )
    }


    fun prerapeChangeChistory(request: String , changeHistory: ChangeHistory?, historyDataType: HistoryDataType) : ChangeHistory{
        val time = System.currentTimeMillis()
        var connected = NetworkUtil.isConnectedWithWifi(context)
        if(historyDataType == AUTH) connected = true // we cant login when offline, so we don't save offline request in this case
        return changeHistory ?: ChangeHistory(
            if (connected) Status.SENT else Status.SENT_OFFLINE,
            LogType.APP_TO_SERVER,
            historyDataType,
            request,
            null,
            time,
            time
        )
    }

    private fun String.toPlainTextBody() = toRequestBody("text/plain".toMediaType())
    private fun String.toMultipartBody() = toRequestBody("multipart/form-data".toMediaType())

    private fun Int.toMultipartBody() =
        this.toString().toRequestBody("multipart/form-data".toMediaType())

    companion object {
        const val TAG = "ApiServiceWrapper"
    }


}