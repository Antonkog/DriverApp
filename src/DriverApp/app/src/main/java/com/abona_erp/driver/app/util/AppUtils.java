package com.abona_erp.driver.app.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.core.base.ContextUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AppUtils {
  
  /**
   * Like {@link android.os.Build.VERSION#SDK_INT}, but in a place where
   * it can be conveniently overriden for local testing.
   */
  public static final int SDK_INT = Build.VERSION.SDK_INT;
  
  public static void playNotificationTone() {
    try {
      Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      Ringtone r = RingtoneManager.getRingtone(ContextUtils.getApplicationContext(), notification);
      r.play();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static long getAppFirstInstallTime(Context context) {
    PackageInfo packageInfo;
    try {
      if(Build.VERSION.SDK_INT>8/*Build.VERSION_CODES.FROYO*/ ){
        packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return packageInfo.firstInstallTime;
      }else{
        //firstinstalltime unsupported return last update time not first install time
        ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
        String sAppFile = appInfo.sourceDir;
        return new File(sAppFile).lastModified();
      }
    } catch (PackageManager.NameNotFoundException e) {
      //should never happen
      return 0;
    }
  }
  
  public static Date getCurrentDateTime() {
    return Calendar.getInstance().getTime();
  }
  
  public synchronized static Date getCurrentDateTimeUtc() {
    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    String currentDate = isoFormat.format(new Date());
    try {
      return isoFormat.parse(currentDate);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String parseOrderNo(int orderNo) {
    if (orderNo > 0) {
      try {
        char [] numString = String.valueOf(orderNo).toCharArray();
        StringBuilder parsedNum = new StringBuilder();
        for(int counter = 0; counter < numString.length; counter++ ){
          if (counter == 4 || counter == 6) {
            parsedNum.append("/");
          }
          parsedNum.append(numString[counter]);
        }
        return parsedNum.toString();
      } catch (RuntimeException e) {
        Log.e(AppUtils.class.getCanonicalName(), " parsing exception : string from server based on :" + orderNo);
        return Constants.empty_str;
      }
    }
    return Constants.empty_str;
  }
  public static boolean isNetworkConnected(Context context) {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    if (activeNetwork != null) {
      // connected to the internet
//      if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
//        // connected to wifi
//      } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
//        // connected to mobile data
//      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * Calls {@link Context#startForegroundService(Intent)} if {@link #SDK_INT}
   * is 26 or higher, or {@link Context#startService(Intent)} otherwise.
   *
   * @param context The context to call.
   * @param intent The intent to pass to the called method.
   * @return The result of the called method.
   */
  public static ComponentName startForegroundService(Context context, Intent intent) {
    if (AppUtils.SDK_INT >= 26) {
      return context.startForegroundService(intent);
    } else {
      return context.startService(intent);
    }
  }
}
