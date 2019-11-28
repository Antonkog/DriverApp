package com.abona_erp.driver.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.receiver.NetworkStateReceiver;
import com.abona_erp.driver.core.base.ContextUtils;

import java.util.Timer;
import java.util.TimerTask;

public class ServiceWorker extends Service
  implements NetworkStateReceiver.NetworkStateReceiverListener {
  
  private static final String TAG = ServiceWorker.class.getSimpleName();
  
  Context mContext;
  public int mCounter = 0;
  
  private NetworkStateReceiver mNetworkStateReceiver;
 
  public ServiceWorker() {
    //super();
    //Log.d(TAG, "ServiceWorker() 1 called!");
    //mContext = ContextUtils.getApplicationContext();
  }
  
  public ServiceWorker(Context ctx) {
    super();
    Log.d(TAG, "ServiceWorker() 2 called!");
    mContext = ctx;
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
  }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
  
    Log.i(TAG, "********************************************************");
    
    try {
      unregisterNetworkBroadcastReceiver(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
    startTimer();
    startNetworkBroadcastReceiver(this);
    
    return START_STICKY;
  }
  
  @Override
  public void onDestroy() {
  /*
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction("com.abona_erp.driver.RestartWorkerService");
    intentFilter.setPriority(100);
    sendBroadcast(new Intent());
   */
    
    //super.onDestroy();
    Log.i(TAG, "onDestroy() called!");
  
    Intent broadcastIntent = new Intent("restart0");
    sendBroadcast(broadcastIntent);
    stopTimerTask();
  }

  private Timer mTimer;
  private TimerTask mTimerTask;
  
  public void startTimer() {
    // set a new Timer:
    mTimer = new Timer();
    
    // initialize the TimerTask's job:
    initializeTimerTask();
    
    // schedule the timer, to wake up every 1 second.
    mTimer.schedule(mTimerTask, 1000, 1000);
  }
  
  public void initializeTimerTask() {
    mTimerTask = new TimerTask() {
      @Override
      public void run() {
        Log.i(TAG, "in timer +++++" + (mCounter++));
        
      }
    };
  }
  
  public void stopTimerTask() {
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
    }
  }
  
  @Override
  public void onTaskRemoved(Intent rootIntent) {
    Intent intent = new Intent(getApplicationContext(), this.getClass());
    intent.setPackage(getPackageName());
  
    PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),
      1, intent, PendingIntent.FLAG_ONE_SHOT);
    AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    alarmService.set(
      AlarmManager.ELAPSED_REALTIME,
      SystemClock.elapsedRealtime() + 1000,
      pendingIntent
    );
    
    super.onTaskRemoved(rootIntent);
  }
  
  @Override
  public void onLowMemory() {
    super.onLowMemory();
    Log.i(TAG, "onLowMemory() called!");
  }
  
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
  
  private void startNetworkBroadcastReceiver(Context ctx) {
    mNetworkStateReceiver = new NetworkStateReceiver();
    mNetworkStateReceiver.addListener((NetworkStateReceiver.NetworkStateReceiverListener)ctx);
    registerNetworkBroadcastReceiver(ctx);
  }
  
  /**
   * Register the NetworkStateReceiver with your activity.
   * @param ctx
   */
  public void registerNetworkBroadcastReceiver(Context ctx) {
    ctx.registerReceiver(mNetworkStateReceiver,
      new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
  }
  
  /**
   * Unregister the NetworkStateReceiver with your Service.
   * @param ctx
   */
  public void unregisterNetworkBroadcastReceiver(Context ctx) {
    ctx.unregisterReceiver(mNetworkStateReceiver);
  }
  
  @Override
  public void networkAvailable() {
    Log.i(TAG, "networkAvailable()");
  }
  
  @Override
  public void networkUnavailable() {
    Log.i(TAG, "networkUnavailable()");
  }
}
