package com.abona_erp.driver.app;

import android.content.Context;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.abona_erp.driver.app.manager.ApiManager;
import com.abona_erp.driver.app.manager.SharedPrefManager;
import com.abona_erp.driver.app.util.gson.DoubleJsonDeserializer;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.dynamiclanguage.DynamicLanguageContextWrapper;
import com.abona_erp.driver.app.util.gson.FloatJsonDeserializer;
import com.abona_erp.driver.app.util.gson.IntegerJsonDeserializer;
import com.abona_erp.driver.app.util.gson.StringJsonDeserializer;
import com.abona_erp.driver.core.base.ContextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import org.greenrobot.eventbus.EventBus;

public class App extends BaseApp {
  
  protected static App sInstance;
  
  public static boolean isAppInForeground = false;
  
  private static Gson GSON_INSTANCE;
  
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
  }
  
  public static Gson createGson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
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
    
    return gsonBuilder.create();
  }
  
  public synchronized static Gson getGson() {
    if (GSON_INSTANCE == null) {
      GSON_INSTANCE = createGson();
    }
    return GSON_INSTANCE;
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
