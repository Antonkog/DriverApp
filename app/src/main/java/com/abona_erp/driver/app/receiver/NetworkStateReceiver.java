package com.abona_erp.driver.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.util.ServiceUtil;
import com.abona_erp.driver.core.util.MiscUtil;

import java.util.ArrayList;
import java.util.List;

public class NetworkStateReceiver extends BroadcastReceiver {
  
  private static final String TAG = MiscUtil.getTag(NetworkStateReceiver.class);
  
  protected Boolean mConnected;
  protected List<NetworkStateReceiverListener> mListeners;
  
  public NetworkStateReceiver() {
    mListeners = new ArrayList<>();
    mConnected = null;
  }
  
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "Intent broadcast received");
    if (intent == null || intent.getExtras() == null)
      return;
    
    ConnectivityManager manager = (ConnectivityManager)
      ServiceUtil.getConnectivityManager(context);
    NetworkInfo networkInfo = manager.getActiveNetworkInfo();
    
    if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
      mConnected = true;
    } else {
      mConnected = false;
    }
    
    notifyStateToAll();
  }
  
  private void notifyStateToAll() {
    Log.i(TAG, "Notifying state to " + mListeners.size() + " listener(s)");
    for (NetworkStateReceiverListener eachNetworkStateReceiverListener : mListeners)
      notifyState(eachNetworkStateReceiverListener);
  }
  
  private void notifyState(NetworkStateReceiverListener listener) {
    if (mConnected == null || listener == null)
      return;
    
    if (mConnected == true) {
      listener.networkAvailable();
    } else {
      listener.networkUnavailable();
    }
  }
  
  public void addListener(NetworkStateReceiverListener listener) {
    Log.i(TAG, "addListener() - mListeners.add(listener) + notifyState(listener);");
    mListeners.add(listener);
    notifyState(listener);
  }
  
  public void removeListener(NetworkStateReceiverListener listener) {
    mListeners.remove(listener);
  }
  
  public interface NetworkStateReceiverListener {
    void networkAvailable();
    void networkUnavailable();
  }
}
