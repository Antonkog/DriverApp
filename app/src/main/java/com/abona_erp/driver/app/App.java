package com.abona_erp.driver.app;

import android.app.Application;

import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.repository.NotifyRepository;

import org.greenrobot.eventbus.EventBus;

public class App extends Application {
  
  public static final EventBus eventBus = EventBus.getDefault();
  
  
  @Override
  public void onCreate() {
    super.onCreate();
  }
}
