package com.abona_erp.driverapp.data.remote

import androidx.lifecycle.LiveData
import com.abona_erp.driverapp.data.local.db.*
import com.abona_erp.driverapp.data.model.*
import com.abona_erp.driverapp.ui.ftasks.TaskWithActivities
import com.abona_erp.driverapp.ui.utils.UtilModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.InputStream

interface AppRepository {

    suspend fun getClientEndpoint(clientId: String): ResultWrapper<ServerUrlResponse>

    //RabbitMQ
    fun getLatestRabbitOrder(id: String): Observable<LatestOrder>

    suspend fun getAuthToken(authModel: UtilModel.AuthModel): ResultWrapper<TokenResponse>

    suspend fun registerDevice(commItem: CommItem): ResultWrapper<ResultOfAction>

    fun upladDocument(
        mandantId: Int,
        orderNo: Int,
        taskID: Int,
        driverNo: Int,
        documentType: Int,
        inputStream: InputStream
    ): Single<UploadResult>

    fun observeTasks(deviceId: String): LiveData<List<TaskEntity>>
    fun observeActivities(taskId: Int): LiveData<List<ActivityEntity>>
    fun observeAllActivities(): LiveData<List<ActivityEntity>>
    fun observeDocuments(taskId: Int): LiveData<List<DocumentEntity>>
    fun observeChangeHistory(): LiveData<List<ChangeHistory>>
    fun observeTaskWithActivities(): LiveData<List<TaskWithActivities>>
    fun observeDelayReasons(): LiveData<List<DelayReasonEntity>>

    suspend fun getDocuments(
        forceUpdate: Boolean,
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    ): ResultWrapper<List<DocumentEntity>>
    //rest API
    suspend fun getTasks(forceUpdate: Boolean, changeHistory: ChangeHistory?): ResultWrapper<List<TaskEntity>>

    suspend fun postActivity(
        activity: Activity
    ): ResultWrapper<ResultOfAction>

    suspend fun confirmTask(
        commItem: CommItem
    ): ResultWrapper<ResultOfAction>

    suspend fun postDelayReason(activityWithDelay: ActivityEntity): ResultWrapper<ResultOfAction>

    /**
     * used to refresh delay reasons when task come from Firebase.
     */
    suspend fun getDelayReasons(mandantId: Int, langCode: String): ResultWrapper<ResultOfAction>

    /**
     * offline mode
     * @param changeHistory - object to recreate request
     */
    suspend fun postActivity(
        changeHistory: ChangeHistory
    ): ResultWrapper<ResultOfAction>

    /**
     * offline mode
     */
    suspend fun confirmTask(
        changeHistory: ChangeHistory
    ): ResultWrapper<ResultOfAction>

    /**
     * offline mode
     */
    suspend fun postDelayReasons(
        changeHistory: ChangeHistory
    ): ResultWrapper<ResultOfAction>

    suspend fun refreshTasks()
    suspend fun refreshDocuments(
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    )

    //local from db
    suspend fun updateTask(taskEntity: TaskEntity): Int //from fcm
    suspend fun insertOrUpdateTask(taskEntity: TaskEntity)
    suspend fun insertOrUpdateActivity(activityEntity: ActivityEntity)
    suspend fun updateActivity(activityEntity: ActivityEntity): Int // call api to set db

    //new document
    suspend fun insertDocument(documentEntity: DocumentEntity)

    suspend fun getNextActivityIfExist(activityEntity: ActivityEntity): ActivityEntity?
    suspend fun getFirstTaskActivity(taskEntity: TaskEntity): ActivityEntity?
    suspend fun getNextTaskIfExist(taskEntity: TaskEntity): TaskEntity?
    suspend fun getParentTask(activityEntity: ActivityEntity): TaskEntity?

    suspend fun cleanDatabase()
    suspend fun getAllOfflineRequests(): List<ChangeHistory>


    suspend fun cleanDelayReasons()
    suspend fun insertDelayReasons(reasons : List<DelayReasonEntity>)
}