package com.abona_erp.driverapp.data.remote

import com.abona_erp.driverapp.data.model.ServerUrlResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path


interface AuthService {
    @GET("Api/AbonaClients/GetServerURLByClientId/{id}/2")
    suspend fun getClientEndpoint(@Path("id") clientId: String): ServerUrlResponse
}