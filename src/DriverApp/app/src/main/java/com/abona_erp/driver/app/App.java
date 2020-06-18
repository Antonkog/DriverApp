package com.abona_erp.driver.app;

import android.content.Context;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.abona_erp.driver.app.di.components.ApplicationComponent;
import com.abona_erp.driver.app.di.components.DaggerApplicationComponent;
import com.abona_erp.driver.app.manager.ApiManager;
import com.abona_erp.driver.app.manager.SharedPrefManager;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.dynamiclanguage.DynamicLanguageContextWrapper;
import com.abona_erp.driver.core.base.ContextUtils;
import com.abona_erp.driver.core.util.MiscUtil;
import com.devexpress.logify.alert.android.LogifyAlert;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Named;

public class App extends BaseApp {
  
  private static final String TAG = MiscUtil.getTag(App.class);

  private ApplicationComponent appComponent;
  protected static App sInstance;
  
  public static boolean isAppInForeground = false;

  @Inject
  @Named("GSON") //that is NOT how di should be implemented, instances should be injected right where we need them, but for now just cleaning the
  public Gson gson;

  @Inject
  @Named("GSON_UTC")
  public Gson gsonUtc;

  @Inject
  public ApiManager apiManager;
  
  public static final EventBus eventBus = EventBus.getDefault();
  public static final SharedPrefManager spManager = new SharedPrefManager();

  public ApplicationComponent getApplicationComponent() {
    return appComponent;
  }
  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(DynamicLanguageContextWrapper.updateContext(base, TextSecurePreferences.getLanguage(base)));
    ContextUtils.initApplicationContext(this);
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
    sInstance = this;

    appComponent = DaggerApplicationComponent
            .builder()
            .build();

    appComponent.inject(this);
    AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver();
    ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    
    spManager.init(this);
    apiManager.init(this);
  
    LogifyAlert client = LogifyAlert.getInstance();
    client.setApiKey("5B357B2806714B8598C6127F537CD389");
    client.setContext(this.getApplicationContext());
    client.startExceptionsHandling();
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
