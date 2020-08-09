package com.redhotapp.driverapp.data.remote

import com.google.gson.JsonObject
import com.redhotapp.driverapp.data.model.CommResponseItem
import com.redhotapp.driverapp.data.model.LatestOrder
import com.redhotapp.driverapp.data.model.TokenResponse
import com.redhotapp.driverapp.data.model.abona.CommItem
import com.redhotapp.driverapp.data.model.abona.ResultOfAction
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response

interface ApiRepository {
    fun getLatestOrder(id: String): Observable<LatestOrder>
    fun getAllTasks(deviceId: String): Observable<CommResponseItem>
    fun registerDevice(commItem: CommItem): Observable<ResultOfAction>
    fun getClientEndpoint(clientId: String): Observable<JsonObject>
    fun getAuthToken(grantType : String, userName : String, password : String) : Observable<Response<TokenResponse>>
    fun postActivity(deviceId: String): Observable<CommResponseItem>
}