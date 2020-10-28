package com.abona_erp.driverapp.data.remote

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.abona_erp.driverapp.data.ResultWithStatus
import com.abona_erp.driverapp.data.local.LocalDataSource
import com.abona_erp.driverapp.data.local.db.ActivityEntity
import com.abona_erp.driverapp.data.local.db.DocumentEntity
import com.abona_erp.driverapp.data.local.db.TaskEntity
import com.abona_erp.driverapp.data.model.*
import com.abona_erp.driverapp.data.remote.rabbitMQ.RabbitService
import com.abona_erp.driverapp.ui.fhome.TaskWithActivities
import com.abona_erp.driverapp.ui.utils.UtilModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.commons.io.FileUtils
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject


class AppRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    val localDataSource: LocalDataSource,
    val rabbit: RabbitService,
    val api: ApiService,
    val authService: AuthService
) : AppRepository {
    val TAG = "ApiRepositoryImpl"
    override fun getLatestRabbitOrder(id: String): Observable<LatestOrder> {
        return rabbit.getLastOrder(id)
    }


    override fun observeTasks(deviceId: String): LiveData<List<TaskEntity>> {
        return localDataSource.observeTasks()
    }

    override fun observeActivities(taskId: Int): LiveData<List<ActivityEntity>> {
        return localDataSource.observeActivities(taskId)
    }

    override fun observeAllActivities(): LiveData<List<ActivityEntity>> {
        return localDataSource.observeAllActivities()
    }

    override fun observeDocuments(taskId: Int): LiveData<List<DocumentEntity>> {
        return localDataSource.observeDocuments()
    }

    override fun observeTaskWithActivities(): LiveData<List<TaskWithActivities>> {
        return localDataSource.observeTasksWithActivities()
    }

    override suspend fun registerDevice(commItem: CommItem): ResultOfAction {
        return api.setDeviceProfile(commItem)
    }

    override suspend fun postActivity(context: Context, activity: Activity): ResultOfAction {
        val commItem: CommItem = UtilModel.getCommActivityChangeItem(context, activity)
        return api.postActivityChange(commItem)
    }

    override suspend fun refreshTasks(deviceId: String) {
        getTasks(true, deviceId)
    }

    override suspend fun refreshDocuments(mandantId: Int, orderNo: Int, deviceId: String) {
        getDocuments(true, mandantId, orderNo, deviceId)
    }

    override suspend fun insertOrReplaceTask(taskEntity: TaskEntity) {
        localDataSource.insertOrReplaceTask(taskEntity)
    }

    override suspend fun insertActivity(activityEntity: ActivityEntity) {
        localDataSource.insertActivity(activityEntity)
    }

    override suspend fun insertDocument(documentEntity: DocumentEntity) {
        localDataSource.insertDocument(documentEntity)
    }

    override suspend fun getTasks(
        forceUpdate: Boolean,
        deviceId: String
    ): ResultWithStatus<List<TaskEntity>> {
        /*
          Maybe<List<Event>> source1 =
        cacheRepository.getEventsFeed(...);
        Single<List<Event>> source2 =
        networkRepository.getEventsFeed(...);
        Maybe<List<Event>> source =
        Maybe.concat(source1, source2.toMaybe()).firstElement();
         */
        if (forceUpdate) {
            try {
                updateTasksFromRemoteDataSource(deviceId)
            } catch (ex: Exception) {
                return ResultWithStatus.Error(ex)
            }
        }
        return localDataSource.getTasks()
    }


    override suspend fun getDocuments(
        forceUpdate: Boolean,
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    ): ResultWithStatus<List<DocumentEntity>> {
        if (forceUpdate) {
            try {
                updateDocumentsFromRemoteDataSource(mandantId, orderNo, deviceId)
            } catch (ex: Exception) {
                return ResultWithStatus.Error(ex)
            }
        }
        return localDataSource.getDocuments()
    }

    suspend fun updateDocumentsFromRemoteDataSource(
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    ) {
        val remoteDocuments = api.getDocuments(mandantId, orderNo, deviceId)
        if (remoteDocuments.isNotEmpty()) {
            localDataSource.deleteDocuments()
            localDataSource.insertDocumentResponse(remoteDocuments)
        } else {
            throw java.lang.Exception("no documents for this task ")
        }
    }

    suspend fun updateTasksFromRemoteDataSource(deviceId: String) {
        val remoteTasks = api.getAllTasks(deviceId)

        if (remoteTasks.isSuccess && !remoteTasks.isException) {
            localDataSource.deleteActivities()
            localDataSource.deleteTasks()
            localDataSource.insertFromCommItem(remoteTasks)
        } else {
            throw java.lang.Exception("updateTasks exception:  ${remoteTasks.text} ")
        }
    }

    override suspend fun getClientEndpoint(clientId: String): ServerUrlResponse {
        return authService.getClientEndpoint(clientId)
    }

    override suspend fun getAuthToken(
        grantType: String,
        userName: String,
        password: String
    ): Response<TokenResponse> {
        return api.authentication(grantType, userName, password)
    }

    override fun upladDocument(
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


}