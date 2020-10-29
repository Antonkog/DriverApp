package com.abona_erp.driverapp.data.remote.utils

import android.content.Context
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.events.RxBusEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ResponseInterceptor(@ApplicationContext val context: Context) : Interceptor {
    val TAG = "ResponseInterceptor"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
//        try {
        val response: Response = chain.proceed(request)
        if (response.code == Constant.ERROR_REST_AUTH) makeAuth(response.message)
        return response
//        } catch (e: SocketTimeoutException){
//            return Response.Builder()
//                .request(chain.request())
//                .protocol(Protocol.HTTP_1_1)
//                .code(Constant.ERROR_REST_TIMEOUT)
//                .message("$TAG timeout exception")
//                .body("client config invalid".toResponseBody(null))
//                .build()
//        }
    }

    private fun makeAuth(message: String) {
        RxBus.publish(RxBusEvent.AuthError(message))
    }
}