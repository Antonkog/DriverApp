package com.abona_erp.driver.app.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.abona_erp.driver.app.BuildConfig;
import com.abona_erp.driver.app.data.remote.ActivityService;
import com.abona_erp.driver.app.data.remote.ConfirmService;
import com.abona_erp.driver.app.data.remote.TokenService;
import com.abona_erp.driver.app.data.remote.interceptor.NetworkConnectionInterceptor;
import com.abona_erp.driver.app.data.remote.interceptor.RequestInterceptor;
import com.abona_erp.driver.app.data.remote.interceptor.UserAgentInterceptor;
import com.abona_erp.driver.app.util.gson.DoubleJsonDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager implements Manager {
  private static final String TAG = ApiManager.class.getSimpleName();
  
  private static final int DISK_CACHE_SIZE = 10 * 1024 * 1024;  // 10 MB
  
  private Context mContext;
  private TokenService mTokenService;
  private ConfirmService mConfirmService;
  private ActivityService mActivityService;
  
  public TokenService getTokenApi() {
    if (mTokenService == null) {
      mTokenService = provideRetrofit("http://213.144.11.162:5000/api/device/")
        .create(TokenService.class);
    }
    return mTokenService;
  }
  
  public ConfirmService getConfirmApi() {
    if (mConfirmService == null) {
      mConfirmService = provideRetrofit("http://213.144.11.162:5000/api/confirmation/")
        .create(ConfirmService.class);
    }
    return mConfirmService;
  }
  
  public ActivityService getActivityApi() {
    if (mActivityService == null) {
      mActivityService = provideRetrofit("http://213.144.11.162:5000/api/activity/")
        .create(ActivityService.class);
    }
    return mActivityService;
  }
  
  private Retrofit provideRetrofit(String url) {
    JsonDeserializer deserializer = new DoubleJsonDeserializer();
    
    Gson mGson = new GsonBuilder()
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      .registerTypeAdapter(double.class, deserializer)
      .registerTypeAdapter(Double.class, deserializer)
      .create();
    
    return new Retrofit.Builder()
      .baseUrl(url)
      .client(provideOkHttpClient())
      .addConverterFactory(GsonConverterFactory.create(mGson))
      .build();
  }
  
  private OkHttpClient provideOkHttpClient() {
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    httpClient.connectTimeout(30, TimeUnit.SECONDS);
    httpClient.readTimeout(30, TimeUnit.SECONDS);
    httpClient.writeTimeout(30, TimeUnit.SECONDS);
    
    String versionName = "1.0";
    try {
      PackageInfo pi = mContext.getApplicationContext()
        .getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
      versionName = pi.versionName;
    } catch (PackageManager.NameNotFoundException ignore) {
    }
    
    // Get Android User Agent:
    //String UA = System.getProperty("http.agent");
    httpClient.addInterceptor(new UserAgentInterceptor("ABONA DriverApp", versionName));
    httpClient.addInterceptor(new RequestInterceptor());
    httpClient.addInterceptor(new NetworkConnectionInterceptor() {
      @Override
      public boolean isInternetAvailable() {
        return true;
      }
  
      @Override
      public void onInternetUnavailable() {
    
      }
  
      @Override
      public void onCacheUnavailable() {
    
      }
    });
  
    if (BuildConfig.DEBUG) {
      HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
      logging.setLevel(HttpLoggingInterceptor.Level.BODY);
      httpClient.addInterceptor(logging);
    }
  
    httpClient.cache(getCache());
  
    return httpClient.build();
  }
  
  @Override
  public void init(Context context) {
    mContext = context;
  }
  
  @Override
  public void clear() {
  
  }
  
  public Cache getCache() {
    File cacheDir = new File(mContext.getCacheDir(), "cache");
    Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
    return cache;
  }
}
