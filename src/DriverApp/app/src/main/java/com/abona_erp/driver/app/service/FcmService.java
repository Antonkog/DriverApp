package com.abona_erp.driver.app.service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.BuildConfig;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.converters.LogLevel;
import com.abona_erp.driver.app.data.converters.LogType;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.dao.LogDAO;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LogItem;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.DataType;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.app.ui.feature.main.MainActivity;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.util.ServiceUtil;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.worker.FCMParserWorker;
import com.abona_erp.driver.app.worker.NotifyWorker;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.microsoft.appcenter.analytics.Analytics;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@SuppressLint("NewApi")
public class FcmService extends FirebaseMessagingService {
  
  private static final String TAG = FcmService.class.getSimpleName();
  
  private static final int NOTIFICATION_ID = 1453;
  private static Timer mTimer = new Timer();
  private static LinkedList<RemoteMessage> mNotifyData = new LinkedList<>();
  
  private DriverDatabase mDB = DriverDatabase.getDatabase();
  private LogDAO         mLogDAO = mDB.logDAO();
  
  public void onCreate() {
    super.onCreate();
    mTimer.scheduleAtFixedRate(new workerTask(), 0, 1000);
  }
  
  @Override
  public void onMessageReceived(RemoteMessage message) {
    
    Log.d(TAG, "++ FCM Message... latency (" + (System.currentTimeMillis() - message.getSentTime()) + " ms)");
    mNotifyData.add(message);
    addLog(LogLevel.INFO, LogType.FCM,  getBaseContext().getString(R.string.log_title_fcm), message.getData().toString());
    if (!BuildConfig.DEBUG) {
      Analytics.trackEvent("FCM-Task");
    }
  }
  
  @Override
  public void onNewToken(String token) {
    Log.i(TAG, "onNewToken()");
    
    if (TextUtils.isEmpty(token) || token.length() <= 0)
      return;
    
    TextSecurePreferences.setFcmToken(getApplicationContext(), token);
    //DriverDatabase db = DriverDatabase.getDatabase();
    DeviceProfileDAO dao = mDB.deviceProfileDAO();
    List<DeviceProfile> deviceProfiles = dao.getDeviceProfiles();
    if (deviceProfiles.size() > 0) {
      TextSecurePreferences.setFcmTokenUpdate(getApplicationContext(), true);
      DateFormat dfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.getDefault());
      dfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
      Date currentTimeStamp = new Date();
      TextSecurePreferences.setFcmTokenLastSetTime(getBaseContext(), dfUtc.format(currentTimeStamp));
      
      deviceProfiles.get(0).setInstanceId(token);
    }
  }
  
  private void scheduleJob(Map<String, String> data) {
    
    Bundle bundle = new Bundle();
    bundle.putString("data", data.toString());

    CommItem commItem = App.getInstance().gsonUtc.fromJson(data.toString(), CommItem.class);

    if (commItem!= null && commItem.getHeader().getDataType().equals(DataType.DOCUMENT) && commItem.getDocumentItem() != null) {
      EventBus.getDefault().post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_NEW_DOCUMENTS), null).addDocumentOrderNo(commItem.getDocumentItem().getOrderNo()));
      //EventBus.getDefault().post(new LogEvent(getBaseContext().getString(R.string.log_document_fcm), LogType.FCM, LogLevel.INFO, getBaseContext().getString(R.string.log_title_fcm),exist? commItem.getTaskItem().getTaskId() : 0));
    }
//    else if (commItem.getHeader().getDataType().equals(DataType.VEHICLE)) {
//      //EventBus.getDefault().post(new LogEvent(getBaseContext().getString(R.string.log_vehicle_fcm), LogType.FCM, LogLevel.INFO, getBaseContext().getString(R.string.log_title_fcm),  exist? commItem.getTaskItem().getTaskId() : 0));
//    } else if (commItem.getTaskItem().getMandantId() != null && commItem.getTaskItem().getTaskId() != null) {
//      //EventBus.getDefault().post(new LogEvent(getBaseContext().getString(R.string.log_task_updated_fcm), LogType.FCM, LogLevel.INFO, getBaseContext().getString(R.string.log_title_fcm),  exist? commItem.getTaskItem().getTaskId() : 0));
//    }


    setOneTimeWork(data.toString());
  }

  public static void setOneTimeWork(String rawMessageExtras) {
    OneTimeWorkRequest taskAlarmRequest =
            new OneTimeWorkRequest.Builder(FCMParserWorker.class)
                    .setInputData(createInputDataForUri(rawMessageExtras))
                    .addTag(Constants.PARSE_FCM_TAG_SUFFIX)
                    .build();
    WorkManager.getInstance(App.getInstance()).enqueue(taskAlarmRequest);
  }

  /**
   * Creates the input data bundle which includes the Uri to operate on
   * @return Data which contains the Image Uri as a String
   */
  public static Data createInputDataForUri(String  extras) {
    Data.Builder builder = new Data.Builder();
    builder.putString(Constants.EXTRAS_FCM_MESSAGE, extras);
    return builder.build();
  }


  /**
   * Create and show a simple notification containing the received
   * FCM message.
   */
  public void createNotification(Context context) {
    NotificationManager mNotificationManager = ServiceUtil
      .getNotificationManager(getApplicationContext());
    
    String appName = getAppName(this);
    
    Intent notificationIntent = new Intent(this, MainActivity.class);
    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    
    String channelId = "channel-Task";
    String channelName = "task";
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel mChannel = new NotificationChannel(channelId,
        channelName, NotificationManager.IMPORTANCE_HIGH);
      mNotificationManager.createNotificationChannel(mChannel);
    }
  
    NotificationCompat.Builder mBuilder =
      new NotificationCompat.Builder(context, channelId)
        .setWhen(System.currentTimeMillis())
        .setSmallIcon(R.drawable.ic_notifications)
        .setContentTitle(fromHtml("ABONA Driver App"))
        .setContentText(fromHtml("You have got a new task"))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true);
  
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    stackBuilder.addNextIntent(notificationIntent);
    
    int requestCode = new Random().nextInt();
    PendingIntent contentIntent = stackBuilder
      .getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
    mBuilder.setContentIntent(contentIntent);
    
    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
  }
  
  private void showNotificationIfPossible(Context context) {
  
    createNotification(context);
  }
  
  public static String getAppName(Context context) {
    CharSequence appName = context.getPackageManager()
      .getApplicationLabel(context.getApplicationInfo());
    return (String)appName;
  }
  
  private Spanned fromHtml(String source) {
    if (source != null) {
      return Html.fromHtml(source);
    } else {
      return null;
    }
  }
  
  private boolean isAvailableSender(String from) {
    String savedSenderID = TextSecurePreferences
      .getFCMSenderID(getApplicationContext());
    return from.equals(savedSenderID);
  }
  
  private void addLog(LogLevel level, LogType type, String title, String message) {
    LogItem item = new LogItem();
    item.setLevel(level);
    item.setType(type);
    item.setTitle(title);
    item.setMessage(message);
    item.setCreatedAt(new Date());
    if (mLogDAO != null) {
      mLogDAO.insert(item);
    }
  }
  
  private static int counter = 0;
  private class workerTask extends TimerTask {
    public void run() {
      if (!mNotifyData.isEmpty()) {
        Log.i(TAG, "Insert Notification to Database " + (++counter));
        RemoteMessage message = mNotifyData.removeFirst();
        if (message == null)
          return;
        
        if (message.getFrom() != null && isAvailableSender(message.getFrom())) {
          if (message.getData().size() > 0) {
            Log.d(TAG, "Data " + message.getData());
            scheduleJob(message.getData());
          }
  
          if (App.isAppInForeground) {
            Log.d(TAG, "foreground");
          } else {
            Log.d(TAG, "background");
            showNotificationIfPossible(getApplicationContext());
          }
        }
      } else {
        counter = 0;
      }
    }
  }
}
