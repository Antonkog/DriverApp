package com.abona_erp.driver.app.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class AppUtils {
  
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
    Date currentDate = Calendar.getInstance().getTime();
    return currentDate;
  }

  public static String parseOrderNo(int orderNo) {
    String _orderNo = String.valueOf(orderNo);
    String tmp = _orderNo.substring(0, 4);
    tmp += "/";
    tmp += _orderNo.substring(4, 6);
    tmp += "/";
    tmp += _orderNo.substring(6);
    return tmp;
  }
}
