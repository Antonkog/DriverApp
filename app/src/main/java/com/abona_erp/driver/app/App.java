package com.abona_erp.driver.app;

import android.app.Application;
import android.content.Context;

import com.abona_erp.driver.app.manager.ApiManager;
import com.abona_erp.driver.app.manager.SharedPrefManager;
import com.abona_erp.driver.core.base.ContextUtils;

import org.greenrobot.eventbus.EventBus;

public class App extends Application {
  
  protected static App sInstance;
  
  public static final EventBus eventBus = EventBus.getDefault();
  public static final ApiManager apiManager = new ApiManager();
  public static final SharedPrefManager spManager = new SharedPrefManager();
  
  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    ContextUtils.initApplicationContext(this);
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
    sInstance = this;
    
    spManager.init(this);
    apiManager.init(this);
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
