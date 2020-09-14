package com.abona_erp.driver.app.data.remote

import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface AuthService {
    @GET("Api/AbonaClients/GetServerURLByClientId/{id}/2")
    fun getClientEndpoint(@Path("id") clientId: String): Observable<JsonObject>
}