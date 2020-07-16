package com.redhotapp.driverapp.data.remote.utils

import android.content.Context
import android.os.Build
import com.google.android.material.internal.ContextUtils
import com.redhotapp.driverapp.BuildConfig
import com.redhotapp.driverapp.data.Constant
import com.redhotapp.driverapp.data.local.Preferences
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

/**
 * Adds a custom `User-Agent` header to OkHttp requests.
 */
class UserAgentInterceptor : Interceptor {

      var context : Context
    constructor(context: Context) {
        this.context = context
    }

    private val userAgent: String = String.format(
        Locale.US,
        "%s/%s (Android %s; %s; %s %s; %s)",
        Constant.userAgentAppName,
        BuildConfig.VERSION_NAME,
        Build.VERSION.RELEASE,
        Build.MODEL,
        Build.BRAND,
        Build.DEVICE,
        Locale.getDefault().language
    )


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val userAgentRequest = chain.request().newBuilder()
            .header("User-Agent", userAgent).let {
                if(Preferences.getAccessToken(context) != null){
                    it.header("Authorization", "Bearer " + Preferences.getAccessToken(context)).build()
                } else{   it.build() }
            }
        return chain.proceed(userAgentRequest)
    }
}