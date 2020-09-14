package com.abona_erp.driver.app.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.gson.JsonObject
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.data.model.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
interface ApiRepository {
    //RabbitMQ
    fun getLatestOrder(id: String): Observable<LatestOrder>
//    fun getDocuments(mandantId: Int,  orderNo: Int, deviceId: String ): LiveData<List<AppFileInterchangeItem>>

    //API set activity change
    fun postActivity(deviceId: String): Observable<CommResponseItem>

    suspend fun refreshTasks(deviceId: String) // call api to set db
    //that is LiveData from db
    fun getAllTasks(deviceId: String): LiveData<List<TaskEntity>>
    fun getAllActivity(deviceId: String): LiveData<List<ActivityEntity>>

    //that responses not in db - safe place to keep credentials
    fun registerDevice(commItem: CommItem): Observable<ResultOfAction>
    fun getClientEndpoint(clientId: String): Observable<JsonObject>
    fun getAuthToken(grantType : String, userName : String, password : String) : Observable<Response<TokenResponse>>
    fun getDocuments(mandantId: Int,  orderNo: Int, deviceId: String ): Single<List<AppFileInterchangeItem>>
}