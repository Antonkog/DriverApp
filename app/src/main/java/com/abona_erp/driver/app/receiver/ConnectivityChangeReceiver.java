package com.abona_erp.driver.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.ConnectivityEvent;
import com.abona_erp.driver.app.ui.feature.main.MainActivity;

import java.net.URL;
import java.net.URLConnection;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
  
  @Override
  public void onReceive(final Context context, final Intent intent) {
    Log.d(ConnectivityChangeReceiver.class.getSimpleName(), "onReceive() called!");
    
    final String action = intent.getAction();
    if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
      checkConnectivity(context);
    }
  }
  
  private void checkConnectivity(final Context context) {
    
    ConnectivityManager cm = (ConnectivityManager)context
      .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    if (!(activeNetwork != null && activeNetwork.isConnectedOrConnecting())) {
      App.eventBus.post(new ConnectivityEvent(0));
      return;
    }
    
    int connectionType;
    if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
      connectionType = 0;
    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
      connectionType = 1;
    } else {
      connectionType = 0;
    }

    final Handler handler = new Handler();
    new Thread(new Runnable() {
      @Override
      public void run() {
        final boolean isConnected = isAbleToConnect("http://www.google.com", 1000);
        handler.post(new Runnable() {
          @Override
          public void run() {
            if (isConnected) {
              if (connectionType == 0) {
                App.eventBus.post(new ConnectivityEvent(1));
              } else {
                App.eventBus.post(new ConnectivityEvent(2));
              }
            } else {
              App.eventBus.post(new ConnectivityEvent(0));
            }
          }
        });
      }
    }).start();
  }
  
  private boolean isNetworkInterfaceAvailable(Context context) {
    ConnectivityManager cm = (ConnectivityManager)context
      .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
  }
  
  // This makes a real connection to an url and checks if you can
  // connect to this url, this needs to be wrapped in a background thread.
  private boolean isAbleToConnect(String url, int timeout) {
    try {
      URL myUrl = new URL(url);
      URLConnection connection = myUrl.openConnection();
      connection.setConnectTimeout(timeout);
      connection.connect();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
