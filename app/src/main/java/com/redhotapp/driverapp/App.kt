package com.redhotapp.driverapp

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){
    lateinit var Prefs: SharedPreferences



    override fun onCreate() {
        super.onCreate()
        Prefs = PreferenceManager.getDefaultSharedPreferences(this)

        FirebaseApp.initializeApp(this);

    }
}