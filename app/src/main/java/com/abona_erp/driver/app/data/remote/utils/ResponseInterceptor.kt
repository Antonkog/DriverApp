package com.abona_erp.driver.app.data.remote.utils

import android.content.Context
import com.abona_erp.driver.app.data.Constant
import com.abona_erp.driver.app.ui.RxBus
import com.abona_erp.driver.app.ui.events.RxBusEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ResponseInterceptor (@ApplicationContext val context: Context) : Interceptor {
    val TAG = "ResponseInterceptor"
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val response: Response = chain.proceed(request)
        if(response.code == 401) makeAuth(response.message)
           when( response.peekBody(2048)?.string()){
               null, "null" -> //return response.newBuilder().message("null body").code(Constant.ERROR_NULL_CODE).build()
               return chain.proceed(chain.request())
                   .newBuilder()
                   .code(Constant.ERROR_NULL_CODE)
                   .protocol(Protocol.HTTP_2)
                   .message("null body")
                   .addHeader("content-type", "application/json")
                 //  .body("null body".toResponseBody("application/json".toMediaTypeOrNull()))
                   .build()
           }
        return response
    }

    private fun makeAuth(message: String) {
       RxBus.publish(RxBusEvent.AuthError(message))
    }
}