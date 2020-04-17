package com.abona_erp.driver.core.base.multidex;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.multidex.MultiDex;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Performs multidex installation for non-isolated processes.
 */
public class BaseMultiDexInstaller {
  private static final String TAG = BaseMultiDexInstaller.class.getSimpleName();
  
  /**
   * Suffix for the meta-data tag in the AndroidManifext.xml that determines
   * whether loading secondary dexes should be skipped for a given
   * process name.
   */
  private static final String IGNORE_MULTIDEX_KEY = ".ignore_multidex";
  
  /**
   *  Installs secondary dexes if possible/necessary.
   *
   *  Isolated processes (e.g. renderer processes) can't load secondary dex
   *  files on K and below, so we don't even try in that case.
   *
   *  In release builds of app apks (as opposed to test apks), this is a
   *  no-op because:
   *    - multidex isn't necessary in release builds because we run proguard
   *      there and thus aren't threatening to hit the dex limit; and
   *    - calling MultiDex.install, even in the absence of secondary dexes,
   *      causes a significant regression in start-up time (crbug.com/525695).
   *
   *  @param context The application context.
   */
  public static void install(Context context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
      && !shouldInstallMultiDex(context)) {
      Log.i(TAG, "Skipping multidex installation: not needed for process.");
    } else {
      MultiDex.install(context);
      Log.i(TAG, "Completed multidex installation.");
    }
  }
  
  private static String getProcessName(Context context) {
    try {
      String currentProcessName = null;
      int pid = android.os.Process.myPid();
      
      ActivityManager manager =
        (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
      for (ActivityManager.RunningAppProcessInfo processInfo
        : manager.getRunningAppProcesses()) {
        if (processInfo.pid == pid) {
          currentProcessName = processInfo.processName;
          break;
        }
      }
      
      return currentProcessName;
    } catch (SecurityException ex) {
      return null;
    }
  }
  
  // Determines whether MultiDex should be installed for the current process.
  // Isolated Processes should skip MultiDex as they can not actually access
  // the files on disk. Privileged processes need ot have all of their
  // dependencies in the MainDex for performance reasons.
  private static boolean shouldInstallMultiDex(Context context) {
    try {
      Method isIsolatedMethod =
        android.os.Process.class.getMethod("isIsolated");
      Object retVal = isIsolatedMethod.invoke(null);
      if (retVal != null && retVal instanceof Boolean && ((Boolean) retVal)) {
        return false;
      }
    } catch (IllegalAccessException | IllegalArgumentException
      | InvocationTargetException | NoSuchMethodException e) {
      // Ignore and fall back to checking the app processes.
    }
    
    String currentProcessName = getProcessName(context);
    if (currentProcessName == null) return true;
    
    PackageManager packageManager = context.getPackageManager();
    try {
      ApplicationInfo appInfo = packageManager.getApplicationInfo(
        context.getPackageName(), PackageManager.GET_META_DATA);
      if (appInfo == null || appInfo.metaData == null) return true;
      return !appInfo.metaData.getBoolean(currentProcessName
        + IGNORE_MULTIDEX_KEY, false);
    } catch (PackageManager.NameNotFoundException e) {
      return true;
    }
  }
}
