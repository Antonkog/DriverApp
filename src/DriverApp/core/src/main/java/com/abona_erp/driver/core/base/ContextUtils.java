package com.abona_erp.driver.core.base;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Process;
import android.preference.PreferenceManager;

import com.abona_erp.driver.core.base.annotations.MainDex;

/**
 * This class provides Android application context related utility methods.
 */
public class ContextUtils {
  private static final String TAG = "ContextUtils";
  
  private static Context sApplicationContext;
  private static String sProcessName;
  
  /**
   * Initialization-on-demand holder. This exists for thread-safe
   * lazy initialization.
   */
  private static class Holder {
    // Not final for tests.
    private static SharedPreferences sSharedPreferences = fetchAppSharedPreferences();
  }
  
  /**
   * Get the Android application context.
   */
  public static Context getApplicationContext() {
    return sApplicationContext;
  }
  
  /**
   * Initializes the java application context.
   *
   * @param appContext The application context.
   */
  @MainDex
  public static void initApplicationContext(Context appContext) {
    if (sApplicationContext != null && sApplicationContext != appContext) {
      throw new RuntimeException("Attempting to set multiple global application contexts.");
    }
    initJavaSideApplicationContext(appContext);
  }
  
  /**
   * Only called by the static holder class and tests.
   *
   * @return The application-wide shared preferences.
   */
  private static SharedPreferences fetchAppSharedPreferences() {
    return PreferenceManager.getDefaultSharedPreferences(sApplicationContext);
  }
  
  public static SharedPreferences getAppSharedPreferences() {
    return Holder.sSharedPreferences;
  }
  
  private static void initJavaSideApplicationContext(Context appContext) {
    if (appContext == null) {
      throw new RuntimeException("Global application context cannot be set to null.");
    }
    sApplicationContext = appContext;
  }
  
  public static AssetManager getApplicationAssets() {
    Context context = getApplicationContext();
    while (context instanceof ContextWrapper) {
      context = ((ContextWrapper) context).getBaseContext();
    }
    return context.getAssets();
  }
  
  /**
   * @return Whether the process is isolated.
   */
  public static boolean isIsolatedProcess() {
    try {
      return (Boolean) Process.class.getMethod("isIsolated").invoke(null);
    } catch (Exception e) { // No multi-catch below API level 19 for reflection exceptions.
      throw new RuntimeException(e);
    }
  }
  
  public static String getProcessName() {
    if (sProcessName != null) {
      return sProcessName;
    }
    try {
      Class<?> activityThreadClazz = Class.forName("android.app.ActivityThread");
      Object activityThread =
        activityThreadClazz.getMethod("currentActivityThread").invoke(null);
      sProcessName =
        (String) activityThreadClazz.getMethod("getProcessName").invoke(activityThread);
      return sProcessName;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
