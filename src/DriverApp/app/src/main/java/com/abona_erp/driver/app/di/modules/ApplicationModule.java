package com.abona_erp.driver.app.di.modules;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.room.Room;
import androidx.work.WorkManager;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.BuildConfig;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.remote.ApiService;
import com.abona_erp.driver.app.data.remote.client.UnsafeOkHttpClient;
import com.abona_erp.driver.app.data.remote.interceptor.AccessTokenInterceptor;
import com.abona_erp.driver.app.data.remote.interceptor.MockInterceptor;
import com.abona_erp.driver.app.data.remote.interceptor.RequestInterceptor;
import com.abona_erp.driver.app.data.remote.interceptor.UserAgentInterceptor;
import com.abona_erp.driver.app.data.repository.DriverRepository;
import com.abona_erp.driver.app.di.scopes.ApplicationScope;
import com.abona_erp.driver.app.manager.ApiManager;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.gson.BooleanJsonDeserializer;
import com.abona_erp.driver.app.util.gson.DoubleJsonDeserializer;
import com.abona_erp.driver.app.util.gson.FloatJsonDeserializer;
import com.abona_erp.driver.app.util.gson.GmtDateTypeAdapter;
import com.abona_erp.driver.app.util.gson.GmtDateUtcTypeAdapter;
import com.abona_erp.driver.app.util.gson.IntegerJsonDeserializer;
import com.abona_erp.driver.app.util.gson.StringJsonDeserializer;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import org.jetbrains.annotations.NotNull;
import org.whispersystems.signalservice.internal.util.Util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApplicationModule {
    private App application;

    public ApplicationModule(App application) {
        this.application = application;
    }

    @Provides
    @ApplicationScope
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @ApplicationScope
    App provideApplication() {
        return application;
    }

    @Provides
    @ApplicationScope
    static SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @ApplicationScope
    static WorkManager provideWorkManager(Context context) {
        return WorkManager.getInstance(context);
    }

    @Provides
    @ApplicationScope
    NotificationManager provideNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
    }

    @Provides
    @ApplicationScope
    AlarmManager provideAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(android.content.Context.ALARM_SERVICE);
    }

    @Provides
    @ApplicationScope
    static ApiManager provideApiManager () {
        return new ApiManager();
    }

    @Provides
    @ApplicationScope
    static DriverDatabase provideDatabase(Context context) {
        return Room.databaseBuilder(context, DriverDatabase.class, "abona-db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @ApplicationScope
    static DriverRepository provideDriverRepo (App app) {
        return new DriverRepository(app);
    }

    @Provides
    @ApplicationScope
    @Named("GSON")
    static Gson provideGson () {
        GsonBuilder gsonBuilder = getGsonBuilder();
        JsonDeserializer deserializer;

        deserializer = new GmtDateTypeAdapter();
        gsonBuilder.registerTypeAdapter(Date.class, deserializer);

        return gsonBuilder.create();
    }

    @Provides
    @ApplicationScope
    OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder httpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient().newBuilder();
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(30, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
            if(App.isTesting()) httpClient.addInterceptor(new MockInterceptor());
        }
        httpClient.addInterceptor(new UserAgentInterceptor("ABONA DriverApp", BuildConfig.VERSION_NAME));
        httpClient.addInterceptor(new AccessTokenInterceptor());
        httpClient.addInterceptor(new RequestInterceptor());
        httpClient.protocols(Util.immutableList(Protocol.HTTP_1_1));
        httpClient.retryOnConnectionFailure(true);
        return httpClient.build();
    }


    @Provides
    @ApplicationScope
    ApiService provideApiService(OkHttpClient okHttpClient, @Named("GSON_UTC") Gson gsonUtc)
    {
        return new Retrofit.Builder()
                .baseUrl(TextSecurePreferences.getEndpoint())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gsonUtc))
                .build().create(ApiService.class);
    }


    @Provides
    @ApplicationScope
    @Named("GSON_UTC")
    static Gson provideGsonUtc() {
        GsonBuilder gsonBuilder = getGsonBuilder();
        JsonDeserializer deserializer;
        deserializer = new GmtDateUtcTypeAdapter();
        gsonBuilder.registerTypeAdapter(Date.class, deserializer);
        return gsonBuilder.create();
    }

    @NotNull
    private static GsonBuilder getGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        JsonDeserializer deserializer = new IntegerJsonDeserializer();
        gsonBuilder.registerTypeAdapter(int.class, deserializer);
        gsonBuilder.registerTypeAdapter(Integer.class, deserializer);

        deserializer = new FloatJsonDeserializer();
        gsonBuilder.registerTypeAdapter(float.class, deserializer);
        gsonBuilder.registerTypeAdapter(Float.class, deserializer);

        deserializer = new DoubleJsonDeserializer();
        gsonBuilder.registerTypeAdapter(double.class, deserializer);
        gsonBuilder.registerTypeAdapter(Double.class, deserializer);

        deserializer = new StringJsonDeserializer();
        gsonBuilder.registerTypeAdapter(String.class, deserializer);

        deserializer = new BooleanJsonDeserializer();
        gsonBuilder.registerTypeAdapter(boolean.class, deserializer);
        gsonBuilder.registerTypeAdapter(Boolean.class, deserializer);
        return gsonBuilder;
    }

//
//
//  @Provides
//    @ApplicationScope
//    static DriverRepository provideDriverRepo (App app, @Named("GSON") Gson gson) {
//        return new DriverRepository(app, gson);
//    }

}
