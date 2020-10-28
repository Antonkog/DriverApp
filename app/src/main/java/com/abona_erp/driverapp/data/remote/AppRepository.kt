package com.abona_erp.driverapp.data.remote

import android.content.Context
import androidx.lifecycle.LiveData
import com.abona_erp.driverapp.data.ResultWithStatus
import com.abona_erp.driverapp.data.local.db.ActivityEntity
import com.abona_erp.driverapp.data.local.db.DocumentEntity
import com.abona_erp.driverapp.data.local.db.TaskEntity
import com.abona_erp.driverapp.data.model.*
import com.abona_erp.driverapp.ui.fhome.TaskWithActivities
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import java.io.InputStream

interface AppRepository {

    suspend fun registerDevice(commItem: CommItem): ResultOfAction
    suspend fun getClientEndpoint(clientId: String): ServerUrlResponse
    suspend fun getAuthToken(
        grantType: String,
        userName: String,
        password: String
    ): Response<TokenResponse>

    //RabbitMQ
    fun getLatestRabbitOrder(id: String): Observable<LatestOrder>
//    fun getDocuments(mandantId: Int,  orderNo: Int, deviceId: String ): LiveData<List<AppFileInterchangeItem>>


    fun upladDocument(
        mandantId: Int,
        orderNo: Int,
        taskID: Int,
        driverNo: Int,
        documentType: Int,
        inputStream: InputStream
    ): Single<UploadResult>

    //API set activity change
    suspend fun postActivity(context: Context, activity: Activity): ResultOfAction

    suspend fun refreshTasks(deviceId: String) // call api to set db
    suspend fun refreshDocuments(
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    ) // call api to set db

    //that is LiveData from db
    fun observeTasks(deviceId: String): LiveData<List<TaskEntity>>
    fun observeActivities(taskId: Int): LiveData<List<ActivityEntity>>
    fun observeAllActivities(): LiveData<List<ActivityEntity>>
    fun observeDocuments(taskId: Int): LiveData<List<DocumentEntity>>
    fun observeTaskWithActivities(): LiveData<List<TaskWithActivities>>

    suspend fun getDocuments(
        forceUpdate: Boolean,
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    ): ResultWithStatus<List<DocumentEntity>>

    suspend fun getTasks(forceUpdate: Boolean, deviceId: String): ResultWithStatus<List<TaskEntity>>
    //that responses not in db - safe place to keep credentials

    suspend fun insertOrReplaceTask(taskEntity: TaskEntity)
    suspend fun insertActivity(activityEntity: ActivityEntity) // call api to set db
    suspend fun insertDocument(documentEntity: DocumentEntity)


}