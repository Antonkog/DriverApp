package com.redhotapp.driverapp.data.remote;

import android.annotation.SuppressLint;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.redhotapp.driverapp.data.local.preferences.Preferences;

import java.util.LinkedList;

@SuppressLint("NewApi")
public class FcmService extends FirebaseMessagingService {
  
  private static final String TAG = FcmService.class.getSimpleName();
  
  private static final int NOTIFICATION_ID = 1453;
  private static LinkedList<RemoteMessage> mNotifyData = new LinkedList<>();



  @Override
  public void onMessageReceived(RemoteMessage message) {

    Log.d(TAG, "++ FCM Message... latency (" + (System.currentTimeMillis() - message.getSentTime()) + " ms)");
    mNotifyData.add(message);
  }

  @Override
  public void onNewToken(String token) {
    Preferences.Companion.setFCMToken(getApplicationContext(), token);
    Log.i(TAG, "onNewToken()");
  }
}
