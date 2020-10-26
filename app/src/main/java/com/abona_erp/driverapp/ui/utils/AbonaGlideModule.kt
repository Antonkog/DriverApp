package com.abona_erp.driverapp.ui.utils

import android.content.Context
import com.abona_erp.driverapp.data.remote.utils.UnsafeOkHttpClient
import com.abona_erp.driverapp.ui.di.AppModule
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream

@GlideModule
class AbonaGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

        AppModule.provideHttpClient(context).let {
            registry.replace(
                GlideUrl::class.java,
                InputStream::class.java,
                OkHttpUrlLoader.Factory(it)
            )
        }
        UnsafeOkHttpClient.getUnsafeOkHttpClient().let {
            registry.replace(
                GlideUrl::class.java,
                InputStream::class.java,
                OkHttpUrlLoader.Factory(it.build())
            )
        }
    }
}