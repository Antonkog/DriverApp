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
import android.os.Environment;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.ChangeHistory;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.core.base.ContextUtils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TimeZone;

import co.nedim.maildroidx.MaildroidX;
import co.nedim.maildroidx.MaildroidXType;
import io.reactivex.Completable;

public class AppUtils {
  
  /**
   * Like {@link android.os.Build.VERSION#SDK_INT}, but in a place where
   * it can be conveniently overriden for local testing.
   */
  public static final int SDK_INT = Build.VERSION.SDK_INT;
  private static final String TAG = "AppUtils";

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
        return "-";
      }
    }
    return "-";
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


  /**method to send email based on our csv file
   * created by Anton Kogan
   * @param context
   * @param message
   * @throws Exception
   */
  private static void sendIntentLogFile(Context context, String message) {
    Intent email = new Intent(Intent.ACTION_SEND);
    email.setType("plain/text");
    email.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.email_support)});
    email.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.history_email_theme));

    File logFile = getLogFile(context);

    if (logFile.exists()) {
      email.putExtra(Intent.EXTRA_TEXT, message+ "\n"
              + context.getResources().getString(R.string.csv_attached) +"\n"
              + logFile.getPath());
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getMimeTypeFromExtension(getExtension(logFile.getPath()));
        Uri uri = FileProvider.getUriForFile(context, Constants.FILE_PROVIDER_AUTHORITY, logFile);
        email.setDataAndType(uri, type);
        email.setType(type);
        email.putExtra(Intent.EXTRA_STREAM, uri);
      }else {
        email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(getLogFile(context)));
      }
    }
    context.startActivity(email);
  }
  public static String getExtension(String name) {
    String ext;

    if (name.lastIndexOf(".") == -1) {
      ext = "";

    } else {
      int index = name.lastIndexOf(".");
      ext = name.substring(index + 1, name.length());
    }
    return ext;
  }

  /**method to get our csv file
   * created by Anton Kogan
   * @param context
   * @return
   */
  public static File getLogFile(Context context) {
    return new  File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + Constants.LOG_FILE_PREFIX  + Constants.LOG_FILE_EXTENSION);
  }

  public static boolean removeLogFile(Context context) {
    return getLogFile(context).delete();
  }

  /**method to add logs based on action history
   * created by Anton Kogan
   * @param context
   * @param deviceProfileString
   * @param logsByOrderNum
   * @throws Exception
   */
  public static void sendEmailIntent(Context context, String deviceProfileString, List<ChangeHistory> logsByOrderNum) throws Exception {
      if(logsByOrderNum.isEmpty()) throw new NoSuchElementException("empty ChangeHistory");
      removeLogFile(context);
      appendLogsInFile(context, logsByOrderNum);
      sendIntentLogFile(context, deviceProfileString);
  }


  public static void appendLogsInFile(Context context, List<ChangeHistory> logsByOrderNum) throws IOException {
    FileWriter myWriter = new FileWriter(getLogFile(context));
    BufferedWriter bufferedWriter = new BufferedWriter(myWriter);
    bufferedWriter.append(logsByOrderNum.get(0).getCsvHeader());
    for (int i = 0; i < logsByOrderNum.size(); i++) {
      bufferedWriter.append(logsByOrderNum.get(i).getAsCsv());
    }
    bufferedWriter.close();
  }
}
