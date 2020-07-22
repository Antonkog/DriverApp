package com.redhotapp.driverapp.data.remote

import com.redhotapp.driverapp.data.model.LatestOrder
import com.redhotapp.driverapp.data.model.TokenResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response

interface ApiRepository {
    fun getLatestOrder(id: String): Observable<LatestOrder>
    fun getAuthToken(grantType : String, userName : String, password : String) : Observable<Response<TokenResponse>>
}