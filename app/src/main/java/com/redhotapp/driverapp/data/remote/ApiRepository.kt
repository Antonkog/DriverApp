package com.redhotapp.driverapp.data.remote

import com.redhotapp.driverapp.data.model.LatestOrder
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response

interface ApiRepository {
    fun getLatestOrder(id: String): Observable<LatestOrder>
    fun getAuthToken(grantType : String, userName : String, password : String) : Observable<Response<String>>
}