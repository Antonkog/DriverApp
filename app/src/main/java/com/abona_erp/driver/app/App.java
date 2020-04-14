package com.abona_erp.driver.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.manager.ApiManager;
import com.abona_erp.driver.app.manager.SharedPrefManager;
import com.abona_erp.driver.app.service.BackgroundServiceWorker;
import com.abona_erp.driver.app.util.gson.BooleanJsonDeserializer;
import com.abona_erp.driver.app.util.gson.DoubleJsonDeserializer;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.dynamiclanguage.DynamicLanguageContextWrapper;
import com.abona_erp.driver.app.util.gson.FloatJsonDeserializer;
import com.abona_erp.driver.app.util.gson.GmtDateTypeAdapter;
import com.abona_erp.driver.app.util.gson.GmtDateUtcTypeAdapter;
import com.abona_erp.driver.app.util.gson.IntegerJsonDeserializer;
import com.abona_erp.driver.app.util.gson.StringJsonDeserializer;
import com.abona_erp.driver.core.base.ContextUtils;
import com.abona_erp.driver.core.util.MiscUtil;
import com.devexpress.logify.alert.android.LogifyAlert;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class App extends BaseApp {
  
  private static final String TAG = MiscUtil.getTag(App.class);
  
  protected static App sInstance;
  
  public static boolean isAppInForeground = false;
  
  private static Gson GSON_INSTANCE;
  private static Gson GSON_UTC_INSTANCE;
  private static SimpleDateFormat SDF_UTC;
  
  public static final EventBus eventBus = EventBus.getDefault();
  public static final ApiManager apiManager = new ApiManager();
  public static final SharedPrefManager spManager = new SharedPrefManager();
  
  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(DynamicLanguageContextWrapper.updateContext(base, TextSecurePreferences.getLanguage(base)));
    ContextUtils.initApplicationContext(this);
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
    sInstance = this;
    
    AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver();
    ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    
    spManager.init(this);
    apiManager.init(this);
  
    LogifyAlert client = LogifyAlert.getInstance();
    client.setApiKey("5B357B2806714B8598C6127F537CD389");
    client.setContext(this.getApplicationContext());
    client.startExceptionsHandling();
  }
  
  public static Gson createGson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    /*gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);*/
    /*gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");*/
    /*gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");*/
    
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
  
    deserializer = new GmtDateTypeAdapter();
    gsonBuilder.registerTypeAdapter(Date.class, deserializer);
    
    return gsonBuilder.create();
  }
  
  public static Gson createGsonUtc() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    /*gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);*/
    /*gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");*/
    /*gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");*/
  
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
  
    deserializer = new GmtDateUtcTypeAdapter();
    gsonBuilder.registerTypeAdapter(Date.class, deserializer);
  
    return gsonBuilder.create();
  }
  
  public synchronized static Gson getGson() {
    if (GSON_INSTANCE == null) {
      GSON_INSTANCE = createGson();
    }
    return GSON_INSTANCE;
  }
  
  public synchronized static Gson getGsonUtc() {
    if (GSON_UTC_INSTANCE == null) {
      GSON_UTC_INSTANCE = createGsonUtc();
    }
    return GSON_UTC_INSTANCE;
  }
  
  public synchronized static SimpleDateFormat getSdfUtc() {
    if (SDF_UTC == null) {
      SDF_UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.getDefault());
      SDF_UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    return SDF_UTC;
  }
  
  public static App getInstance() {
    return sInstance;
  }
  
  public void clear() {
    apiManager.clear();
    spManager.clear();
  }
  
  @Override
  public void onTerminate() {
    clear();
    super.onTerminate();
  }
}
