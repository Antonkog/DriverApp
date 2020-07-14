package com.redhotapp.driverapp.data.remote

import com.redhotapp.driverapp.data.model.LatestOrder
import com.redhotapp.driverapp.data.remote.rabbitMQ.RabbitService
import io.reactivex.rxjava3.core.Observable

class ApiRepositoryImpl(val rabbit : RabbitService, val api : ApiService) : ApiRepository {
    override fun getLatestOrder(id: String): Observable<LatestOrder> {
       return  rabbit.getLastOrder(id)
    }
}