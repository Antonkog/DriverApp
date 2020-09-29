package com.abona_erp.driver.app.data.remote

import android.content.Context
import androidx.lifecycle.LiveData
import com.abona_erp.driver.app.data.ResultWithStatus
import com.google.gson.JsonObject
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.data.local.db.DocumentEntity
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.data.model.*
import com.abona_erp.driver.app.ui.utils.UtilModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import java.io.File

interface AppRepository {
    //RabbitMQ
    fun getLatestOrder(id: String): Observable<LatestOrder>
//    fun getDocuments(mandantId: Int,  orderNo: Int, deviceId: String ): LiveData<List<AppFileInterchangeItem>>

    //API set activity change
    suspend fun postActivity(context: Context, activity: Activity): Observable<ResultOfAction>
    suspend fun refreshTasks(deviceId: String) // call api to set db
    suspend fun refreshDocuments(mandantId: Int,  orderNo: Int, deviceId: String) // call api to set db
    suspend fun insertActivity(activityEntity: ActivityEntity) // call api to set db

    //that is LiveData from db
    fun observeTasks(deviceId: String): LiveData<List<TaskEntity>>
    fun observeActivities(taskId : Int): LiveData<List<ActivityEntity>>
    fun observeDocuments(taskId : Int): LiveData<List<DocumentEntity>>

    suspend fun getActivities(): List<ActivityEntity>

    //that responses not in db - safe place to keep credentials
    fun registerDevice(commItem: CommItem): Observable<ResultOfAction>
    fun getClientEndpoint(clientId: String): Observable<JsonObject>
    fun getAuthToken(grantType : String, userName : String, password : String) : Observable<Response<TokenResponse>>
    suspend fun getDocuments(forceUpdate: Boolean , mandantId: Int,  orderNo: Int, deviceId: String ): ResultWithStatus<List<DocumentEntity>>
    suspend fun saveTask(taskEntity: TaskEntity)
    suspend fun getTasks(forceUpdate: Boolean, deviceId: String): ResultWithStatus<List<TaskEntity>>

    fun upladDocument(
        mandantId: Int,
        orderNo: Int,
        taskID: Int,
        driverNo: Int,
        documentType: Int,
        file: File
    ): Single<UploadResult>

}