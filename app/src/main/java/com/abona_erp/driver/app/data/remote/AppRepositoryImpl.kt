package com.abona_erp.driver.app.data.remote

import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.data.model.*
import com.abona_erp.driver.app.data.remote.rabbitMQ.RabbitService
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import javax.inject.Inject
import com.abona_erp.driver.app.data.ResultWithStatus
import com.abona_erp.driver.app.data.local.LocalDataSource

class AppRepositoryImpl @Inject constructor (val localDataSource: LocalDataSource, val rabbit : RabbitService,
                                             val api : ApiService, val authService: AuthService) : AppRepository {
    val TAG = "ApiRepositoryImpl"
    override fun getLatestOrder(id: String): Observable<LatestOrder> {
       return  rabbit.getLastOrder(id)
    }


    override fun observeTasks(deviceId: String): LiveData<List<TaskEntity>> {
        return localDataSource.observeTasks()
    }

    override fun observeActivities(deviceId: String): LiveData<List<ActivityEntity>> {
        return localDataSource.observeActivities()
    }

    override suspend fun getActivities(): List<ActivityEntity> {
       return  localDataSource.getActivities()
    }

    override fun registerDevice(commItem: CommItem): Observable<ResultOfAction> {
        return api.deviceProfile(commItem)
    }

    override fun postActivity(deviceId: String): Observable<CommResponseItem> {
        return api.postActivity(deviceId)
    }

    override suspend fun refreshTasks(deviceId: String) {
        getTasks(true, deviceId)
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
    ): Single<List<AppFileInterchangeItem>> {
        return api.getDocuments(mandantId,orderNo,deviceId)
    }

    override suspend fun saveTask(taskEntity: TaskEntity) {
        localDataSource.saveTask(taskEntity)
    }
}