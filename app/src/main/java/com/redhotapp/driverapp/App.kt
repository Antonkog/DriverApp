package com.redhotapp.driverapp

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this);

    }
}