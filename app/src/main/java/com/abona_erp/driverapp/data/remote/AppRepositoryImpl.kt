package com.abona_erp.driverapp.data.remote

import android.content.Context
import androidx.lifecycle.LiveData
import com.abona_erp.driverapp.MainViewModel
import com.abona_erp.driverapp.data.local.LocalDataSource
import com.abona_erp.driverapp.data.local.db.ActivityEntity
import com.abona_erp.driverapp.data.local.db.ChangeHistory
import com.abona_erp.driverapp.data.local.db.DocumentEntity
import com.abona_erp.driverapp.data.local.db.TaskEntity
import com.abona_erp.driverapp.data.model.*
import com.abona_erp.driverapp.data.remote.rabbitMQ.RabbitService
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.abona_erp.driverapp.ui.ftasks.TaskWithActivities
import com.abona_erp.driverapp.ui.utils.UtilModel
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

    override fun observeChangeHistory(): LiveData<List<ChangeHistory>> {
      return  localDataSource.observeCommunication()
    }

    override fun observeTaskWithActivities(): LiveData<List<TaskWithActivities>> {
        return localDataSource.observeTasksWithActivities()
    }
    override suspend fun getClientEndpoint(clientId: String): ResultWrapper<ServerUrlResponse> {
        RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(null, MainViewModel.StatusType.LOADING)))
        return try {
            val result =  ResultWrapper.Success(authService.getClientEndpoint(clientId))
            RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(result.toString(), MainViewModel.StatusType.COMPLETE)))
            result
        } catch (ex: Exception){
            RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(ex.message, MainViewModel.StatusType.ERROR)))
            ResultWrapper.Error(ex)
        }
    }

    override suspend fun getAuthToken(
        grantType: String,
        userName: String,
        password: String
    ): ResultWrapper<TokenResponse> {
       return api.authentication(grantType,userName,password)
    }

    override suspend fun registerDevice(commItem: CommItem): ResultWrapper<ResultOfAction>  {
       return api.setDeviceProfile(commItem)
    }

    override suspend fun getTasks(
        forceUpdate: Boolean,
        deviceId: String
    ): ResultWrapper<List<TaskEntity>> {
        if (forceUpdate) {
            try {
                updateTasksFromRemoteDataSource(deviceId)
            } catch (ex: Exception) {
                return ResultWrapper.Error(ex)
            }
        }
        return localDataSource.getTasks()
    }

    override suspend fun postActivity(context: Context, activity: Activity): ResultWrapper<ResultOfAction>{
        val commItem: CommItem = UtilModel.getCommActivityChangeItem(context, activity)
       return api.postActivityChange(commItem)
    }

    override suspend fun confirmTask(
        context: Context,
        commItem: CommItem
    ): ResultWrapper<ResultOfAction> {
       return api.confirmTask(commItem)
    }


    private suspend fun updateDocumentsFromRemoteDataSource(
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    ) {
        api.updateDocumentsFromServer(mandantId, orderNo, deviceId)
    }

    private suspend fun updateTasksFromRemoteDataSource(deviceId: String) {
        api.updateTasksFromServer(deviceId)
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

    override suspend fun getDocuments(
        forceUpdate: Boolean,
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    ): ResultWrapper<List<DocumentEntity>> {
        if (forceUpdate) {
            try {
                updateDocumentsFromRemoteDataSource(mandantId, orderNo, deviceId)
            } catch (ex: Exception) {
                return ResultWrapper.Error(ex)
            }
        }
        return localDataSource.getDocuments()
    }

    override suspend fun refreshTasks(deviceId: String) {
        getTasks(true, deviceId)
    }

    override suspend fun refreshDocuments(mandantId: Int, orderNo: Int, deviceId: String) {
        getDocuments(true, mandantId, orderNo, deviceId)
    }

    override suspend fun insertOrUpdateTask(taskEntity: TaskEntity) {
        localDataSource.insertOrUpdateTask(taskEntity)
    }

    override suspend fun insertOrUpdateActivity(activityEntity: ActivityEntity) {
        localDataSource.insertOrUpdateActivity(activityEntity)
    }

    override suspend fun getNextActivityIfExist(activityEntity: ActivityEntity): ActivityEntity? {
        return localDataSource.getNextActivityIfExist(activityEntity)
    }

    override suspend fun getFirstTaskActivity(taskEntity: TaskEntity): ActivityEntity? {
        return localDataSource.getFirstTaskActivity(taskEntity)
    }

    override suspend fun getNextTaskIfExist(taskEntity: TaskEntity): TaskEntity? {
        return localDataSource.getNextTaskIfExist(taskEntity)
    }

    override suspend fun cleanDatabase() {
        localDataSource.cleanDatabase()
    }

    override suspend fun updateActivity(activityEntity: ActivityEntity) :Int {
       return localDataSource.updateActivity(activityEntity)
    }

    override suspend fun insertDocument(documentEntity: DocumentEntity) {
        localDataSource.insertDocument(documentEntity)
    }


    override suspend fun updateTask(taskEntity: TaskEntity) : Int{
        return localDataSource.updateTask(taskEntity)
    }


    override suspend fun getParentTask(activityEntity: ActivityEntity): TaskEntity? {
       return localDataSource.getParentTask(activityEntity)
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