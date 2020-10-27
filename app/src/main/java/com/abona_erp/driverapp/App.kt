package com.abona_erp.driverapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    companion object {
        fun isTesting(): Boolean {
            return false
        }
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        if (!BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        }

    }
}