package com.abona_erp.driver.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.service.ServiceWorker;

public class WorkerRestartBroadcastReceiver extends BroadcastReceiver {
  
  private static final String TAG = WorkerRestartBroadcastReceiver.class.getCanonicalName();

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "Service stops, let's restart again!");
    Intent serviceIntent = new Intent(context, ServiceWorker.class);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      context.startForegroundService(serviceIntent);
    } else {
      context.startService(serviceIntent);
    }
  }
}
