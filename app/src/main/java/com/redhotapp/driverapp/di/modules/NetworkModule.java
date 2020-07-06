package com.redhotapp.driverapp.di.modules;

import androidx.annotation.NonNull;

import com.google.android.material.internal.ContextUtils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redhotapp.driverapp.DriverApp;
import com.redhotapp.driverapp.data.source.net.ApiService;
import com.redhotapp.driverapp.data.source.net.NetworkChecker;
import com.redhotapp.driverapp.data.source.net.interceptors.MockRespondInterceptor;
import com.redhotapp.driverapp.data.source.net.interceptors.NetworkCheckInterceptor;
import com.redhotapp.driverapp.data.source.net.interceptors.ServerErrorsInterceptor;
import com.redhotapp.driverapp.di.qualifiers.OkHttpInterceptors;
import com.redhotapp.driverapp.di.qualifiers.OkHttpNetworkInterceptors;
import com.redhotapp.driverapp.util.preferences.Preferences;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(ApplicationComponent.class)
public class NetworkModule {


    @Provides
    @Singleton
    public static ApiService provideApiService(){
        return  new Retrofit.Builder()
                .baseUrl("https://213.144.11.162/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(ApiService.class);
    }



    @Provides
    @NonNull
    @Singleton
    public OkHttpClient provideOkHttpClient(NetworkChecker networkChecker,
                                            @OkHttpInterceptors @NonNull List<Interceptor> interceptors,
                                            @OkHttpNetworkInterceptors @NonNull List<Interceptor> networkInterceptors) {
        final OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        boolean testing = DriverApp.nonReleaseTesting();
        if (testing) okHttpBuilder.addInterceptor(new MockRespondInterceptor());
        if (!testing) okHttpBuilder.addInterceptor(new NetworkCheckInterceptor(networkChecker));
        if (!testing) okHttpBuilder.addInterceptor(new ServerErrorsInterceptor());
        if (!testing) okHttpBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

        for (Interceptor interceptor : interceptors) {
            okHttpBuilder.addInterceptor(interceptor);
        }

        for (Interceptor networkInterceptor : networkInterceptors) {
            okHttpBuilder.addNetworkInterceptor(networkInterceptor);
        }

        okHttpBuilder.connectTimeout(1, TimeUnit.MINUTES);
        okHttpBuilder.readTimeout(1, TimeUnit.MINUTES);
        okHttpBuilder.writeTimeout(1, TimeUnit.MINUTES);

        return okHttpBuilder.build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Preferences.INSTANCE.endpoint.get())
                .build();
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new Gson();
    }

}