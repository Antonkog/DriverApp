package com.abona_erp.driver.app.data.remote.rabbitMQ

import com.abona_erp.driver.app.data.model.LatestOrder
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface RabbitService {
    @GET("/api/Yard/GetLatestOrder/{trackid}")
    fun getLastOrder(@Path("trackid") trackID: String?): Observable<LatestOrder>
}