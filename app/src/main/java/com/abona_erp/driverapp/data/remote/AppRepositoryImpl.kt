package com.abona_erp.driverapp.data.remote

import android.content.Context
import androidx.lifecycle.LiveData
import com.abona_erp.driverapp.MainViewModel
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.LocalDataSource
import com.abona_erp.driverapp.data.local.db.*
import com.abona_erp.driverapp.data.model.*
import com.abona_erp.driverapp.data.remote.rabbitMQ.RabbitService
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.abona_erp.driverapp.ui.ftasks.TaskWithActivities
import com.abona_erp.driverapp.ui.utils.UtilModel
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.InputStream
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    val localDataSource: LocalDataSource,
    val rabbit: RabbitService,
    val api: ApiServiceWrapper,
    val gson: Gson,
    val authService: AuthService
) : AppRepository {
    val TAG = "ApiRepositoryImpl"
    override fun getLatestRabbitOrder(id: String): Observable<LatestOrder> {
        return rabbit.getLastOrder(id)
    }

    override suspend fun getAllOfflineRequests(): List<ChangeHistory> {
        return localDataSource.getAllOfflineRequests()
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

    override fun observeChangeHistory(): LiveData<List<ChangeHistory>> {
        return localDataSource.observeCommunication()
    }

    override fun observeTaskWithActivities(): LiveData<List<TaskWithActivities>> {
        return localDataSource.observeTasksWithActivities()
    }

    override fun observeDelayReasons(): LiveData<List<DelayReasonEntity>> {
        return localDataSource.observeDelayReasons()
    }

    override suspend fun getAuthToken(
        authModel: UtilModel.AuthModel
    ): ResultWrapper<TokenResponse> {
        return api.authentication(authModel)
    }


    override suspend fun registerDevice(
        commItem: CommItem
    ): ResultWrapper<ResultOfAction> {
        return api.setDeviceProfile(commItem)
    }



    /**
     * as we have different auth service, error is handled here
     * calling auth api, that is not showing any credentials,
     * no need to add credentials to database and error handle is here
     */
    override suspend fun getClientEndpoint(clientId: String): ResultWrapper<ServerUrlResponse> {
        postToUi(null, MainViewModel.StatusType.LOADING)
        return try {
            val result = ResultWrapper.Success(authService.getClientEndpoint(clientId))
            postToUi(result.toString(), MainViewModel.StatusType.COMPLETE)
            result
        } catch (ex: Exception) {
            postToUi(ex.message, MainViewModel.StatusType.ERROR)
            ResultWrapper.Error(ex)
        }
    }


    override suspend fun refreshDocuments(mandantId: Int, orderNo: Int, deviceId: String) {
        getDocuments(true, mandantId, orderNo, deviceId)
    }
    /**
     * handle it here before merge with documents branch (A.Kogan)
     */
    override suspend fun getDocuments(
        forceUpdate: Boolean,
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    ): ResultWrapper<List<DocumentEntity>> {
        if (forceUpdate) {
            postToUi(null, MainViewModel.StatusType.LOADING)
            try {
                updateDocumentsFromRemoteDataSource(mandantId, orderNo, deviceId)
                postToUi(context.getString(R.string.doc_update_success), MainViewModel.StatusType.COMPLETE)
            } catch (ex: Exception) {
                postToUi(context.getString(R.string.doc_update_error), MainViewModel.StatusType.ERROR)
                return ResultWrapper.Error(ex)
            }
        }
        return localDataSource.getDocuments()
    }

    private fun postToUi(message: String?, status: MainViewModel.StatusType) {
        RxBus.publish(
            RxBusEvent.RequestStatus(
                MainViewModel.Status(
                    message,
                    status
                )
            )
        )
    }

    override suspend fun postActivity(activity: Activity): ResultWrapper<ResultOfAction> {
        return api.postActivityChange(UtilModel.getCommActivityChangeItem(context, activity))
    }

    override suspend fun postDelayReasons(delayReasonEntity: DelayReasonEntity): ResultWrapper<ResultOfAction> {
        return api.postDelayItems(UtilModel.getCommDelayChangeItem(context, delayReasonEntity))
    }

    override suspend fun postDelayReasons(changeHistory: ChangeHistory): ResultWrapper<ResultOfAction> {
        return api.postDelayItems(changeHistory)
    }

    override suspend fun getDelayReasons(mandantId: Int, langCode : String): ResultWrapper<ResultOfAction> {
        return api.getDelayItems(mandantId, langCode)
    }


    override suspend fun postActivity(changeHistory: ChangeHistory): ResultWrapper<ResultOfAction> {
        return api.postActivityChange(changeHistory)
    }


    override suspend fun confirmTask(
        commItem: CommItem
    ): ResultWrapper<ResultOfAction> {
        return api.confirmTask(commItem)
    }

    override suspend fun confirmTask(
        changeHistory: ChangeHistory
    ): ResultWrapper<ResultOfAction> {
        return api.confirmTask(changeHistory)
    }


    private suspend fun updateDocumentsFromRemoteDataSource(
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    ) {
        api.updateDocumentsFromServer(mandantId, orderNo, deviceId)
    }


    override suspend fun updateTasksFromRemoteDataSource(changeHistory: ChangeHistory?) : ResultWrapper<CommResponseItem>{
        return api.updateTasksFromServer(changeHistory)
    }


    override fun upladDocument(
        mandantId: Int,
        orderNo: Int,
        taskID: Int,
        driverNo: Int,
        documentType: Int,
        inputStream: InputStream
    ): Single<UploadResult> {
        return api.uploadDocument(mandantId, orderNo, taskID, driverNo, documentType, inputStream)
    }



    override suspend fun insertOrUpdateTask(taskEntity: TaskEntity) {
        localDataSource.insertOrUpdateTask(taskEntity)
    }

    override suspend fun insertOrUpdateActivity(activityEntity: ActivityEntity) {
        localDataSource.insertOrUpdateActivity(activityEntity)
    }

    override suspend fun insertDelayReasons(reasons: List<DelayReasonEntity>) {
        localDataSource.insertDelayReasons(reasons = reasons)
    }


    override suspend fun getActivity(actId:  Int, taskId: Int, mandantId: Int): ActivityEntity? {
        return localDataSource.getActivity(actId, taskId, mandantId)
    }


    override suspend fun cleanDatabase() {
        localDataSource.cleanDatabase()
    }

    override suspend fun updateActivity(activityEntity: ActivityEntity): Int {
        return localDataSource.updateActivity(activityEntity)
    }

    override suspend fun insertDocument(documentEntity: DocumentEntity) {
        localDataSource.insertDocument(documentEntity)
    }

    override suspend fun getFirstTaskActivity(taskEntity: TaskEntity): ActivityEntity? {
        return  localDataSource.getFirstTaskActivity(taskEntity)
    }


    override suspend fun updateTask(taskEntity: TaskEntity): Int {
        return localDataSource.updateTask(taskEntity)
    }


    override suspend fun getTask(taskId: Int, mandantId: Int): TaskEntity? {
        return localDataSource.getTask(taskId, mandantId)
    }


    /* if using observables
         Maybe<List<Event>> source1 =
       cacheRepository.getEventsFeed(...);
       Single<List<Event>> source2 =
       networkRepository.getEventsFeed(...);
       Maybe<List<Event>> source =
       Maybe.concat(source1, source2.toMaybe()).firstElement();
        */
}