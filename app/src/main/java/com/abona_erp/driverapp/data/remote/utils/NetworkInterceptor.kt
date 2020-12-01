package com.abona_erp.driverapp.data.remote.utils

import android.content.Context
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.local.preferences.PrivatePreferences
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.events.RxBusEvent
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class NetworkInterceptor(val context: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //saving request body
        var request: Request = chain.request()

        val newHost = PrivatePreferences.getEndpoint(context)?.toHttpUrlOrNull()

        val defaultHostAPI = Constant.defaultApiUrl.toHttpUrl()

        if (newHost!= null && request.url.host == defaultHostAPI.host && (newHost.host != request.url.host || newHost.port != request.url.port)) {

            val new = request.url.newBuilder().host(newHost.host).port(newHost.port).build()

            request = request.newBuilder()
                .url(new)
                .build()

        }

        //getting response
        val response: Response = chain.proceed(request)
        if (response.code == Constant.ERROR_REST_AUTH) makeAuth(response.message)

        return response
    }

    private fun makeAuth(message: String) {
        RxBus.publish(RxBusEvent.AuthError(message))
    }

    companion object {
        const val TAG = "NetworkInterceptor"
    }
}