package com.abona_erp.driver.core.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * BuildInfo is a utility class providing easy access
 * to {@link PackageInfo} information.
 */
public class BuildInfo {
  private static final String TAG = "BuildInfo";
  private static final int MAX_FINGERPRINT_LENGTH = 128;
  
  private static PackageInfo sDriverPackageInfo;
  private static boolean sInitialized;
  
  public final int hostVersionCode;
  public final String packageName;
  public final int versionCode;
  public final String versionName;
  public final String androidBuildFingerprint;
  
  private static class Holder {
    private static BuildInfo sInstance = new BuildInfo();
  }
  
  private static String[] getAll() {
    BuildInfo buildInfo = getInstance();
    String hostPackageName = ContextUtils.getApplicationContext().getPackageName();
    return new String[] {
      Build.BRAND, Build.DEVICE, Build.ID, Build.MANUFACTURER, Build.MODEL,
      String.valueOf(Build.VERSION.SDK_INT), Build.TYPE, Build.BOARD, hostPackageName
    };
  }
  
  private static String nullToEmpty(CharSequence seq) {
    return seq == null ? "" : seq.toString();
  }
  
  public static void setDriverPackageInfo(PackageInfo packageInfo) {
    assert !sInitialized;
    sDriverPackageInfo = packageInfo;
  }
  
  public static BuildInfo getInstance() {
    return Holder.sInstance;
  }
  
  private BuildInfo() {
    sInitialized = true;
    try {
      Context appContext = ContextUtils.getApplicationContext();
      String hostPackageName = appContext.getPackageName();
      PackageManager pm = appContext.getPackageManager();
      PackageInfo pi = pm.getPackageInfo(hostPackageName, 0);
      hostVersionCode = pi.versionCode;
      if (sDriverPackageInfo != null) {
        packageName = sDriverPackageInfo.packageName;
        versionCode = sDriverPackageInfo.versionCode;
        versionName = nullToEmpty(sDriverPackageInfo.versionName);
        sDriverPackageInfo = null;
      } else {
        packageName = hostPackageName;
        versionCode = hostVersionCode;
        versionName = nullToEmpty(pi.versionName);
      }
      
      // The value is truncated, as this is used for crash.
      androidBuildFingerprint = Build.FINGERPRINT.substring(0,
        Math.min(Build.FINGERPRINT.length(), MAX_FINGERPRINT_LENGTH));
    } catch (PackageManager.NameNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Check if this is a debuggable build of Android. Use this to enable developer-only features.
   * This is a rough approximation of the hidden API {@code Build.IS_DEBUGGABLE}.
   */
  public static boolean isDebugAndroid() {
    return "eng".equals(Build.TYPE) || "userdebug".equals(Build.TYPE);
  }
}
