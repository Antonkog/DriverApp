package com.abona_erp.driverapp.data.remote

import android.content.Context
import android.util.Log
import com.abona_erp.driverapp.MainViewModel
import com.abona_erp.driverapp.data.local.LocalDataSource
import com.abona_erp.driverapp.data.local.db.ChangeHistory
import com.abona_erp.driverapp.data.local.db.HistoryDataType.*
import com.abona_erp.driverapp.data.local.db.LogType
import com.abona_erp.driverapp.data.local.db.Status
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
        val time = System.currentTimeMillis()
        val change = changeHistory?: ChangeHistory(
            Status.SENT, //as i don't want to recreate request  - set it as sent. (user can't login when offline)
            LogType.APP_TO_SERVER,
            AUTH,
            gson.toJson(authModel),
            null,
            time,
            time
        )
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
    suspend fun updateTasksFromServer(changeHistory: ChangeHistory?) {
        val time = System.currentTimeMillis()
        val connected = NetworkUtil.isConnectedWithWifi(context)

        val deviceId = DeviceUtils.getUniqueID(context)
        val change = changeHistory ?: ChangeHistory(
            if (connected) Status.SENT else Status.SENT_OFFLINE,
            LogType.APP_TO_SERVER,
            GET_TASKS,
            deviceId,
            null,
            time,
            time
        )

        val autoGenId = changeHistory?.id ?: localDataSource.insertHistoryChange(change)

        val remoteTasks = api.getAllTasks(deviceId)
        if (remoteTasks.isSuccess && !remoteTasks.isException) {
            localDataSource.updateFromCommItem(remoteTasks)
            updateHistoryOnSuccess(change, gson.toJson(remoteTasks), autoGenId)
        } else {
            updateHistoryOnError(change, autoGenId)
            throw java.lang.Exception("updateTasks exception:  ${remoteTasks.text} ")
        }
    }

    /**
     * that method exception is handled in AppRepositoryImpl
     * so no need to send exception to UI here and no try/catch block
     */
    suspend fun updateDocumentsFromServer(
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    ) {
        val resp = api.getDocuments(mandantId, orderNo, deviceId)

        resp?.let {
            if (it.isNotEmpty()) {
                localDataSource.deleteDocuments()
                localDataSource.insertDocumentResponse(it)
            }
        }

    }


    suspend fun setDeviceProfile(commItem: CommItem): ResultWrapper<ResultOfAction> {
        return setDeviceProfile(commItem, null)
    }


    private suspend fun setDeviceProfile(
        commItem: CommItem,
        changeHistory: ChangeHistory?
    ): ResultWrapper<ResultOfAction> {
        sentLoadingToUI()
        val change = changeHistory(changeHistory, commItem).copy(status = Status.SENT) //as i don't want to recreate request  - set it as sent. (user can't login when offline)
        val autoGenId = changeHistory?.id ?: localDataSource.insertHistoryChange(change)
        return try {
            val result = ResultWrapper.Success(api.setDeviceProfile(commItem))
            sendSuccessToUI(result.toString())
            updateHistoryOnSuccess(change, gson.toJson(result), autoGenId)
            result
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
        val change = changeHistory(changeHistory, commItem)
        val autoGenId = changeHistory?.id ?: localDataSource.insertHistoryChange(change)
        return try {
            val result = ResultWrapper.Success(api.postActivityChange(commItem))
            updateHistoryOnSuccess(change, gson.toJson(result), autoGenId)
            sendSuccessToUI(result.toString())
            result
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

    private suspend fun confirmTask(
        commItem: CommItem,
        changeHistory: ChangeHistory?
    ): ResultWrapper<ResultOfAction> {
        val change = changeHistory(changeHistory, commItem)
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

    private fun changeHistory(
        changeHistory: ChangeHistory?,
        commItem: CommItem
    ): ChangeHistory {

        val dataType =
            when (commItem.header.dataType) {
                DataType.TASK_CONFIRMATION.dataType -> CONFIRM_TASK
                DataType.DEVICE_PROFILE.dataType -> SET_DEVICE_PROFILE
                DataType.ACTIVITY.dataType -> POST_ACTIVITY
                else -> throw java.lang.Exception("this dataType not supported for offline mode logging")
            }

        val time = System.currentTimeMillis()
        val connected = NetworkUtil.isConnectedWithWifi(context)

        return changeHistory ?: ChangeHistory(
            if (connected) Status.SENT else Status.SENT_OFFLINE,
            LogType.APP_TO_SERVER,
            dataType,
            gson.toJson(commItem),
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