package com.redhotapp.driverapp.data.remote

import com.google.gson.JsonObject
import com.redhotapp.driverapp.data.model.LatestOrder
import com.redhotapp.driverapp.data.model.TokenResponse
import com.redhotapp.driverapp.data.model.abona.CommItem
import com.redhotapp.driverapp.data.model.abona.ResultOfAction
import com.redhotapp.driverapp.data.remote.rabbitMQ.RabbitService
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response

class ApiRepositoryImpl(val rabbit : RabbitService, val api : ApiService, val authService: AuthService) : ApiRepository {
    override fun getLatestOrder(id: String): Observable<LatestOrder> {
       return  rabbit.getLastOrder(id)
    }

    override fun getAllTasks(deviceId: String): Observable<ResultOfAction> {
       return api.getAllTasks(deviceId)
    }

    override fun registerDevice(commItem: CommItem): Observable<ResultOfAction> {
       return api.deviceProfile(commItem)
    }

    override fun getClientEndpoint(clientId: String): Observable<JsonObject> {
        return  authService.getClientEndpoint(clientId)
    }

    override fun getAuthToken(grantType : String, userName : String, password : String): Observable<Response<TokenResponse>> {
        return  api.authentication(grantType, userName, password)
    }
}