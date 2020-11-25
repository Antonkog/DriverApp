package com.abona_erp.driverapp.data.remote

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
import androidx.core.graphics.drawable.toBitmap
import com.abona_erp.driverapp.MainActivity
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.preferences.PrivatePreferences
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.*


@SuppressLint("NewApi")
class FcmService : FirebaseMessagingService() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(
            TAG,
            "++ FCM Message... latency (" + (System.currentTimeMillis() - message.sentTime) + " ms)"
        )
        Log.d(
            TAG,
            "FCM Message(" + message.data.toString() + " )"
        )
        sendNotification(message.data.toString())
        RxBus.publish(RxBusEvent.FirebaseMessage(message.data.toString()))
    }

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.fcmChannelId)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_app_icon_driver)
            .setLargeIcon(resources.getDrawable(R.drawable.ic_app_icon_driver, null).toBitmap())
            .setContentTitle(getString(R.string.fcmChannelId))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.fcmChannelId),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID/* ID of notification */, notificationBuilder.build())
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
    }
}