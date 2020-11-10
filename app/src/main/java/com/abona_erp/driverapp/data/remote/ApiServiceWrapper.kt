package com.abona_erp.driverapp.data.remote

import android.content.Context
import android.util.Log
import com.abona_erp.driverapp.MainViewModel
import com.abona_erp.driverapp.data.local.LocalDataSource
import com.abona_erp.driverapp.data.local.db.ChangeHistory
import com.abona_erp.driverapp.data.local.db.HistoryDataType.*
import com.abona_erp.driverapp.data.local.db.LogType
import com.abona_erp.driverapp.data.local.db.Status
import com.abona_erp.driverapp.data.model.CommItem
import com.abona_erp.driverapp.data.model.ResultOfAction
import com.abona_erp.driverapp.data.model.TokenResponse
import com.abona_erp.driverapp.data.model.UploadResult
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.events.RxBusEvent
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
        grantType: String,
        userName: String,
        password: String
    ): ResultWrapper<TokenResponse> {
        RxBus.publish(
            RxBusEvent.RequestStatus(
                MainViewModel.Status(
                    null,
                    MainViewModel.StatusType.LOADING
                )
            )
        )
        return try {
            val result = ResultWrapper.Success(api.authentication(grantType, userName, password))
            RxBus.publish(
                RxBusEvent.RequestStatus(
                    MainViewModel.Status(
                        result.toString(),
                        MainViewModel.StatusType.COMPLETE
                    )
                )
            )
            result
        } catch (ex: Exception) {
            RxBus.publish(
                RxBusEvent.RequestStatus(
                    MainViewModel.Status(
                        ex.message,
                        MainViewModel.StatusType.ERROR
                    )
                )
            )
            ResultWrapper.Error(ex)
        }
    }

    suspend fun updateTasksFromServer(deviceId: String) {
        val time = System.currentTimeMillis()
        val change =
            ChangeHistory(Status.SENT, LogType.APP_TO_SERVER, GET_TASKS, deviceId, null, time, time)
        val autoGenId = localDataSource.insertHistoryChange(change)
        RxBus.publish(
            RxBusEvent.RequestStatus(
                MainViewModel.Status(
                    null,
                    MainViewModel.StatusType.LOADING
                )
            )
        )
        val remoteTasks = api.getAllTasks(deviceId)
        if (remoteTasks.isSuccess && !remoteTasks.isException) {
            localDataSource.updateFromCommItem(remoteTasks)
            localDataSource.updateHistoryChange(
                change.copy(
                    status = Status.SUCCESS,
                    id = autoGenId,
                    response = gson.toJson(remoteTasks)
                )
            )
            RxBus.publish(
                RxBusEvent.RequestStatus(
                    MainViewModel.Status(
                        null,
                        MainViewModel.StatusType.COMPLETE
                    )
                )
            )
        } else {
            RxBus.publish(
                RxBusEvent.RequestStatus(
                    MainViewModel.Status(
                        "updateTasks exception:  ${remoteTasks.text}",
                        MainViewModel.StatusType.ERROR
                    )
                )
            )
            localDataSource.updateHistoryChange(change.copy(status = Status.ERROR, id = autoGenId))
            throw java.lang.Exception("updateTasks exception:  ${remoteTasks.text} ")
        }
    }

    suspend fun updateDocumentsFromServer(
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    ) {
        try {
            RxBus.publish(
                RxBusEvent.RequestStatus(
                    MainViewModel.Status(
                        null,
                        MainViewModel.StatusType.LOADING
                    )
                )
            )

            val resp = api.getDocuments(mandantId, orderNo, deviceId)

            RxBus.publish(
                RxBusEvent.RequestStatus(
                    MainViewModel.Status(
                        null,
                        MainViewModel.StatusType.COMPLETE
                    )
                )
            )

            resp?.let {
                if (it.isNotEmpty()) {
                    localDataSource.deleteDocuments()
                    localDataSource.insertDocumentResponse(it)
                }
            }

        } catch (ex: java.lang.Exception) {
            RxBus.publish(
                RxBusEvent.RequestStatus(
                    MainViewModel.Status(
                        ex.message,
                        MainViewModel.StatusType.ERROR
                    )
                )
            )
            throw java.lang.Exception("update Documents exception:  ${ex.message} ")
        }
    }


    suspend fun setDeviceProfile(commItem: CommItem?): ResultWrapper<ResultOfAction> {
        RxBus.publish(
            RxBusEvent.RequestStatus(
                MainViewModel.Status(
                    null,
                    MainViewModel.StatusType.LOADING
                )
            )
        )
        val time = System.currentTimeMillis()
        val change = ChangeHistory(
            Status.SENT,
            LogType.APP_TO_SERVER,
            SET_DEVICE_PROFILE,
            gson.toJson(commItem),
            null,
            time,
            time
        )
        val autoGenId = localDataSource.insertHistoryChange(change)
        return try {
            val result =
                ResultWrapper.Success(api.setDeviceProfile(commItem))
            RxBus.publish(
                RxBusEvent.RequestStatus(
                    MainViewModel.Status(
                        result.toString(),
                        MainViewModel.StatusType.COMPLETE
                    )
                )
            )
            localDataSource.updateHistoryChange(
                change.copy(
                    status = Status.SUCCESS,
                    response = gson.toJson(result),
                    id = autoGenId
                )
            )
            result
        } catch (ex: Exception) {
            RxBus.publish(
                RxBusEvent.RequestStatus(
                    MainViewModel.Status(
                        ex.message,
                        MainViewModel.StatusType.ERROR
                    )
                )
            )
            localDataSource.updateHistoryChange(change.copy(status = Status.ERROR, id = autoGenId))
            return ResultWrapper.Error(ex)
        }
    }

    suspend fun postActivityChange(commItem: CommItem): ResultWrapper<ResultOfAction> {
        RxBus.publish(
            RxBusEvent.RequestStatus(
                MainViewModel.Status(
                    null,
                    MainViewModel.StatusType.LOADING
                )
            )
        )
        val time = System.currentTimeMillis()
        val change = ChangeHistory(
            Status.SENT,
            LogType.APP_TO_SERVER,
            POST_ACTIVITY,
            gson.toJson(commItem),
            null,
            time,
            time
        )
        val autoGenId = localDataSource.insertHistoryChange(change)
        return try {
            val result = ResultWrapper.Success(api.postActivityChange(commItem))
            localDataSource.updateHistoryChange(
                change.copy(
                    status = Status.SUCCESS,
                    response = gson.toJson(result),
                    id = autoGenId
                )
            )
            RxBus.publish(
                RxBusEvent.RequestStatus(
                    MainViewModel.Status(
                        result.toString(),
                        MainViewModel.StatusType.COMPLETE
                    )
                )
            )
            result
        } catch (ex: Exception) {
            localDataSource.updateHistoryChange(change.copy(status = Status.ERROR, id = autoGenId))
            RxBus.publish(
                RxBusEvent.RequestStatus(
                    MainViewModel.Status(
                        ex.message,
                        MainViewModel.StatusType.ERROR
                    )
                )
            )
            ResultWrapper.Error(ex)
        }
    }


    suspend fun confirmTask(commItem: CommItem): ResultWrapper<ResultOfAction> {
        val time = System.currentTimeMillis()
        val change = ChangeHistory(
            Status.SENT,
            LogType.APP_TO_SERVER,
            CONFIRM_TASK,
            gson.toJson(commItem),
            null,
            time,
            time
        )
        val autoGenId = localDataSource.insertHistoryChange(change)
        return try {
            val result = api.confirmTask(commItem)
            localDataSource.updateHistoryChange(
                change.copy(
                    status = Status.SUCCESS,
                    response = gson.toJson(result),
                    id = autoGenId
                )
            )
            ResultWrapper.Success(result)
        } catch (ex: java.lang.Exception) {
            localDataSource.updateHistoryChange(change.copy(status = Status.ERROR, id = autoGenId))
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


    private fun String.toPlainTextBody() = toRequestBody("text/plain".toMediaType())
    private fun String.toMultipartBody() = toRequestBody("multipart/form-data".toMediaType())

    private fun Int.toMultipartBody() =
        this.toString().toRequestBody("multipart/form-data".toMediaType())

    companion object {
        const val TAG = "ApiServiceWrapper"
    }


}