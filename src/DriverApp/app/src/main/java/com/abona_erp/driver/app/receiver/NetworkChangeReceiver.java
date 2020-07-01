package com.abona_erp.driver.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.abona_erp.driver.app.data.remote.NetworkUtil;
import com.abona_erp.driver.app.ui.event.ConnectivityEvent;

import org.greenrobot.eventbus.EventBus;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        EventBus.getDefault().post(new ConnectivityEvent(NetworkUtil.isConnected(context)));

    }
}