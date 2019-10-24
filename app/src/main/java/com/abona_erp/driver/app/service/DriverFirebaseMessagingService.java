package com.abona_erp.driver.app.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.feature.main.MainActivity;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class DriverFirebaseMessagingService extends FirebaseMessagingService {
  
  private static final String TAG = "DriverFCMService";
  
  private static final int REQUEST_CODE = 1;
  private static final int NOTIFICATION_ID = 1453;
  
  public DriverFirebaseMessagingService() {
    super();
  }
  
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    Log.d(TAG, "Message received [" + remoteMessage + "]");
    
    if (remoteMessage.getData().size() > 0) {
      Log.d(TAG, "Message data " + remoteMessage.getData());
      scheduleJob(remoteMessage.getData());
      sendNotification(remoteMessage.getNotification());
    }
  }
  
  @Override
  public void onNewToken(String token) {
    Log.d(TAG, "Refreshed token: " + token);
    
    sendRegistrationToServer(token);
  }
  
  private void sendRegistrationToServer(String token) {
    // TODO: Implement this method to send token your app sever.
  }
  
  /**
   * Create and show a simple notification containing the received
   * FCM message.
   */
  private void sendNotification(RemoteMessage.Notification notification) {
    
    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this,
      REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    
    Uri uriSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    
    NotificationCompat.Builder notificationBuilder =
      new NotificationCompat.Builder(this)
      .setSmallIcon(R.drawable.ic_notifications)
      .setContentTitle("ABONA Driver App")
      .setContentText("You have got a new task")
      .setAutoCancel(true)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setSound(uriSound)
      .setContentIntent(pendingIntent);
    
    NotificationManager notificationManager = (NotificationManager)
      getSystemService(Context.NOTIFICATION_SERVICE);
    
    notificationManager.notify(NOTIFICATION_ID,
      notificationBuilder.build());
  }
  
  private void scheduleJob(Map<String, String> data) {
    Bundle bundle = new Bundle();
    bundle.putString("data", data.toString());
    //for (Map.Entry<String, String> entry : data.entrySet()) {
    //  bundle.putString(entry.getKey(), entry.getValue());
    //}
  
    FirebaseJobDispatcher dispatcher =
      new FirebaseJobDispatcher(new GooglePlayDriver(this));
    Job myJob = dispatcher.newJobBuilder()
      .setService(NotifyJobService.class)
      .setTag("notify-job")
      .setExtras(bundle)
      .build();
    dispatcher.schedule(myJob);
  }
}
