package com.abona_erp.driver.app

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){
    companion object {
        fun isTesting (): Boolean {
            return false
        }
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this);

    }
}