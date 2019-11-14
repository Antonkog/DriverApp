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

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.feature.main.MainActivity;
import com.abona_erp.driver.app.util.ServiceUtil;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

@SuppressLint("NewApi")
public class FcmService extends FirebaseMessagingService {
  
  private static final String TAG = FcmService.class.getSimpleName();
  
  private static final int NOTIFICATION_ID = 1453;
  
  @Override
  public void onMessageReceived(RemoteMessage message) {
    
    String from = message.getFrom();
    Log.d(TAG, "onMessage - from: " + from);
    Log.i(TAG, "FCM message... Delay: "
      + (System.currentTimeMillis() - message.getSentTime()));
    
    Bundle extras = new Bundle();
    
    if (message.getNotification() != null) {
      extras.putString(PushConstants.TITLE, message.getNotification().getTitle());
      extras.putString(PushConstants.MESSAGE, message.getNotification().getBody());
    }
    //for (Map.Entry<String, String> entry : message.getData().entrySet()) {
    //  extras.putString(entry.getKey(), entry.getValue());
    //}
    
    if (extras != null && isAvailableSender(from)) {
      
      if (message.getData().size() > 0) {
        Log.d(TAG, "Data " + message.getData());
        scheduleJob(message.getData());
      }
      
      if (App.isAppInForeground) {
        Log.d(TAG, "foreground");
      } else {
        Log.d(TAG, "background");
        showNotificationIfPossible(getApplicationContext(), extras);
      }
    }
  }
  
  @Override
  public void onNewToken(String token) {
    Log.i(TAG, "onNewToken()");
    
    if (!TextSecurePreferences.isPushRegistered(getApplicationContext())) {
      Log.i(TAG, "Got a new FCM token, but the user isn't registered.");
      return;
    }
    
    //sendRegistrationToServer(token);
  }
  
  private void scheduleJob(Map<String, String> data) {
    
    Bundle bundle = new Bundle();
    bundle.putString("data", data.toString());
  
    FirebaseJobDispatcher dispatcher =
      new FirebaseJobDispatcher(new GooglePlayDriver(this));
    Job myJob = dispatcher.newJobBuilder()
      .setService(NotificationService.class)
      .setTag("notify-job")
      .setExtras(bundle)
      .build();
    dispatcher.schedule(myJob);
  }
  
  /**
   * Create and show a simple notification containing the received
   * FCM message.
   */
  public void createNotification(Context context, Bundle extras) {
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
  
  private void showNotificationIfPossible(Context context, Bundle extras) {
    
    // Send a notification if there is a message or title,
    // otherwise just send data.
    String message = extras.getString(PushConstants.MESSAGE);
    String title = extras.getString(PushConstants.TITLE);
    
    Log.d(TAG, "message =[" + message + "]");
    Log.d(TAG, "title =[" + title + "]");
    
    if ((message != null && message.length() != 0)
      || (title != null && title.length() != 0))
    {
      Log.d(TAG, "create notification 1");
      
      if (title == null || title.isEmpty()) {
        extras.putString(PushConstants.TITLE, getAppName(this));
      }
      
      createNotification(context, extras);
    } else {
      Log.d(TAG, "create notification 2");
      
      extras.putString(PushConstants.TITLE, getAppName(this));
      extras.putString(PushConstants.MESSAGE, "You have got a new task");
  
      createNotification(context, extras);
    }
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
}
