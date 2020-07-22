package com.redhotapp.driverapp.data.remote

import com.redhotapp.driverapp.data.model.LatestOrder
import com.redhotapp.driverapp.data.model.TokenResponse
import com.redhotapp.driverapp.data.remote.rabbitMQ.RabbitService
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response

class ApiRepositoryImpl(val rabbit : RabbitService, val api : ApiService) : ApiRepository {
    override fun getLatestOrder(id: String): Observable<LatestOrder> {
       return  rabbit.getLastOrder(id)
    }

    override fun getAuthToken(grantType : String, userName : String, password : String): Observable<Response<TokenResponse>> {
        return  api.authentication(grantType, userName, password)
    }

}