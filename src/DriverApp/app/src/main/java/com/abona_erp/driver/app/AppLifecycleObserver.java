package com.abona_erp.driver.app;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.abona_erp.driver.app.logging.Log;

public class AppLifecycleObserver implements LifecycleObserver {
  
  public static final String TAG = AppLifecycleObserver.class.getName();
  
  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  public void onEnteredForeground() {
    Log.d(TAG, "***** APP IN FOREGROUND *****");
    App.isAppInForeground = true;
  }
  
  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  public void onEnterBackground() {
    Log.d(TAG, "***** APP IN BACKGROUND *****");
    App.isAppInForeground = false;
  }
}
