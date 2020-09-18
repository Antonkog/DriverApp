package com.abona_erp.driver.app.data.remote

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.abona_erp.driver.app.MainActivity
import com.abona_erp.driver.app.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.abona_erp.driver.app.data.local.preferences.PrivatePreferences
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

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.fcmChannelId)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_app_icon_driver)
            .setContentTitle(getString(R.string.fcmChannelId))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                getString(R.string.fcmChannelId),
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        PrivatePreferences.setFCMToken(
            applicationContext,
            token
        )
        Log.i(TAG, "onNewToken: $token")
    }

    companion object {
        private val TAG = FcmService::class.java.simpleName
        private const val NOTIFICATION_ID = 1453
        private val mNotifyData =
            LinkedList<RemoteMessage>()
    }
}