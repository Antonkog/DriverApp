package com.redhotapp.driverapp.data.remote

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.redhotapp.driverapp.data.local.preferences.Preferences
import java.util.*


@SuppressLint("NewApi")
class FcmService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(
            TAG,
            "++ FCM Message... latency (" + (System.currentTimeMillis() - message.sentTime) + " ms)"
        )
        mNotifyData.add(message)
    }

    override fun onNewToken(token: String) {
        Preferences.setFCMToken(
            applicationContext,
            token
        )
        Log.i(TAG, "onNewToken: " + token)
    }

    companion object {
        private val TAG = FcmService::class.java.simpleName
        private const val NOTIFICATION_ID = 1453
        private val mNotifyData =
            LinkedList<RemoteMessage>()
    }
}