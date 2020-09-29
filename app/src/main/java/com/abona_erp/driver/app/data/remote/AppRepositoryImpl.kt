package com.abona_erp.driver.app.data.remote

import android.content.Context
import androidx.lifecycle.LiveData
import com.abona_erp.driver.app.data.ResultWithStatus
import com.abona_erp.driver.app.data.local.LocalDataSource
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.data.model.*
import com.abona_erp.driver.app.data.remote.rabbitMQ.RabbitService
import com.abona_erp.driver.app.ui.utils.UtilModel
import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject


class AppRepositoryImpl @Inject constructor (val localDataSource: LocalDataSource, val rabbit : RabbitService,
                                             val api : ApiService, val authService: AuthService) : AppRepository {
    val TAG = "ApiRepositoryImpl"
    override fun getLatestOrder(id: String): Observable<LatestOrder> {
       return  rabbit.getLastOrder(id)
    }


    override fun observeTasks(deviceId: String): LiveData<List<TaskEntity>> {
        return localDataSource.observeTasks()
    }

    override fun observeActivities(taskID : Int): LiveData<List<ActivityEntity>> {
        return localDataSource.observeActivities(taskID)
    }

    override suspend fun getActivities(): List<ActivityEntity> {
       return  localDataSource.getActivities()
    }

    override fun registerDevice(commItem: CommItem): Observable<ResultOfAction> {
        return api.setDeviceProfile(commItem)
    }

    override suspend fun postActivity(context: Context, activity: Activity): Observable<ResultOfAction> {
            val commItem: CommItem = UtilModel.getCommActivityChangeItem(context, activity)
            return api.postActivityChange(commItem)
    }

    override suspend fun refreshTasks(deviceId: String) {
        getTasks(true, deviceId)
    }

    override suspend fun insertActivity(activityEntity: ActivityEntity) {
        localDataSource.insertActivity(activityEntity)
    }

    override suspend fun getTasks(forceUpdate: Boolean, deviceId: String):  ResultWithStatus<List<TaskEntity>> {
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

     suspend fun updateTasksFromRemoteDataSource(deviceId: String) {
        val remoteTasks = api.getAllTasks(deviceId)

        if(remoteTasks.isSuccess && !remoteTasks.isException){
            localDataSource.deleteActivities()
            localDataSource.deleteTasks()
            localDataSource.insertFromCommItem(remoteTasks)
        } else{
            throw java.lang.Exception("updateTasks exception:  ${remoteTasks.text} " )
        }
    }

    override fun getClientEndpoint(clientId: String): Observable<JsonObject> {
        return  authService.getClientEndpoint(clientId)
    }

    override fun getAuthToken(grantType : String, userName : String, password : String): Observable<Response<TokenResponse>> {
        return  api.authentication(grantType, userName, password)
    }

    override fun getDocuments(
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    ): Single<List<DocumentResponse>> {
        return api.getDocuments(mandantId,orderNo,deviceId)
    }

    override fun upladDocument(
        mandantId: Int,
        orderNo: Int,
        taskID: Int,
        driverNo: Int,
        documentType: Int,
        file: File
    ): Single<UploadResult> {
        val mandantBody = mandantId.toMultipartBody()
        val orderBody = orderNo.toMultipartBody()
        val taskBody = taskID.toMultipartBody()
        val driverBody = driverNo.toMultipartBody()
        val docTypeBody = documentType.toMultipartBody()
        val fileBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val multiparFileBody =  MultipartBody.Part.createFormData(
            name = "files[]",
            filename = file.name,
            body = fileBody
        )
        return api.uploadDocument(mandantBody, orderBody, taskBody, driverBody, docTypeBody, multiparFileBody)
    }

    private fun String.toPlainTextBody() = toRequestBody("text/plain".toMediaType())
    private fun String.toMultipartBody() = toRequestBody("multipart/form-data".toMediaType())
    private fun Int.toMultipartBody() = this.toString().toRequestBody("multipart/form-data".toMediaType())



    override suspend fun saveTask(taskEntity: TaskEntity) {
        localDataSource.saveTask(taskEntity)
    }
}