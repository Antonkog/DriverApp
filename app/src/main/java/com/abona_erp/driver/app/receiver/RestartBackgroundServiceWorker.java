package com.abona_erp.driver.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.service.BackgroundServiceWorker;
import com.abona_erp.driver.core.base.ContextUtils;
import com.abona_erp.driver.core.util.MiscUtil;

public class RestartBackgroundServiceWorker extends BroadcastReceiver {
  
  private static final String TAG = MiscUtil.getTag(RestartBackgroundServiceWorker.class);
  
  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "Service stops, let's restart again!");
    context.startService(new Intent(context, BackgroundServiceWorker.class));
  }
}
