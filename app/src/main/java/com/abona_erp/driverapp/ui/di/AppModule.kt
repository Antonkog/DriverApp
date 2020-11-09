/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abona_erp.driverapp.ui.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.abona_erp.driverapp.App
import com.abona_erp.driverapp.BuildConfig
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.local.LocalDataSource
import com.abona_erp.driverapp.data.local.db.AppDatabase
import com.abona_erp.driverapp.data.local.preferences.PrivatePreferences
import com.abona_erp.driverapp.data.model.ConfirmationItem
import com.abona_erp.driverapp.data.remote.*
import com.abona_erp.driverapp.data.remote.rabbitMQ.RabbitService
import com.abona_erp.driverapp.data.remote.utils.MockInterceptor
import com.abona_erp.driverapp.data.remote.utils.NetworkInterceptor
import com.abona_erp.driverapp.data.remote.utils.UnsafeOkHttpClient
import com.abona_erp.driverapp.data.remote.utils.UserAgentInterceptor
import com.abona_erp.driverapp.data.remote.utils.gson.ConfirmationAdapterJson
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * Module to tell Hilt how to provide instances of types that cannot be constructor-injected.
 *
 * As these types are scoped to the application lifecycle using @Singleton, they're installed
 * in Hilt's ApplicationComponent.
 */
@Module
@InstallIn(ApplicationComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideGson(): Gson {
        return getGsonBuilder().create()
    }

    private fun getGsonBuilder(): GsonBuilder {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setDateFormat(Constant.abonaCommunicationDateFormat)
            .setDateFormat(Constant.abonaCommunicationDateVarTwo)
            .setDateFormat(Constant.abonaCommunicationDateVarThree)
        gsonBuilder.registerTypeAdapter(ConfirmationItem::class.java, ConfirmationAdapterJson())
        return gsonBuilder
    }
    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "driver_db"
        ).build()
    }


    @Singleton
    @Provides
    fun provideLocalDataSource(database: AppDatabase): LocalDataSource {
        return LocalDataSource(database)
    }


    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Singleton
    @Provides
    fun provideHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val okHttpBuilder = UnsafeOkHttpClient.getUnsafeOkHttpClient()
        okHttpBuilder.connectTimeout(1, TimeUnit.MINUTES)
        okHttpBuilder.readTimeout(1, TimeUnit.MINUTES)
        okHttpBuilder.writeTimeout(1, TimeUnit.MINUTES)
        okHttpBuilder.addInterceptor(UserAgentInterceptor(context))
        okHttpBuilder.addInterceptor(NetworkInterceptor(context))
        if (BuildConfig.DEBUG) {
            if (App.isTesting()) {
                okHttpBuilder.addInterceptor(MockInterceptor())
            }
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            okHttpBuilder.addInterceptor(logging)
            okHttpBuilder.addInterceptor(OkHttpProfilerInterceptor())
        }
        return okHttpBuilder.build()
    }


    @Singleton
    @Provides
    fun provideRabbitService(okHttpClient: OkHttpClient, gson: Gson): RabbitService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(Constant.baseRabbitUrl).build().create(RabbitService::class.java)
    }


    @Singleton
    @Provides
    fun provideApiServiceWrapper(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
        gson: Gson, localDataSource: LocalDataSource
    ): ApiServiceWrapper {
        val api =  Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(PrivatePreferences.getEndpoint(context)).build().create(ApiService::class.java)
        return ApiServiceWrapper(api, localDataSource)
    }

    @Singleton
    @Provides
    fun provideAuthService(okHttpClient: OkHttpClient, gson: Gson): AuthService { //here is only place where we use base url to get new url.
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(Constant.baseAuthUrl).build().create(AuthService::class.java)
    }

//
//
//    @Provides
//    fun provideIoDispatcher() = Dispatchers.IO
}


/**
 * The binding for ApiRepository is on its own module so that we can replace it easily in tests.
 */
@Module
@InstallIn(ApplicationComponent::class)
abstract class TasksRepositoryModule {
    @Binds
    abstract fun bindApiRepository(
        apiRepositoryImpl: AppRepositoryImpl
    ): AppRepository

}

