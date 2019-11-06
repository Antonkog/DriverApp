package com.abona_erp.driver.app;

import android.app.Application;
import android.content.Context;
import android.media.VolumeShaper;

import androidx.work.Configuration;
import androidx.work.WorkManager;

import org.greenrobot.eventbus.EventBus;

public class App extends Application {
  
  protected static App sInstance;
  protected static WorkManager sWorkManager;
  
  public static final EventBus eventBus = EventBus.getDefault();
  
  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
    sInstance = this;
  }
  
  public static App getInstance() {
    return sInstance;
  }
  
  public static WorkManager getWorkManager() {
    return sWorkManager;
  }
}
