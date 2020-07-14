package com.redhotapp.driverapp.data.local

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.Constant

class Preferences {

    companion object Companion {
        private fun getSharedPrefs(activity: Activity): SharedPreferences? {
            return activity.getSharedPreferences(
                activity.getString(R.string.preferences_id), Context.MODE_PRIVATE
            )
        }


        fun setEndpoint(activity: Activity, endpoint: String?) {
            val sharedPref = getSharedPrefs(activity) ?: return
            with(sharedPref.edit()) {
                putString(activity.getString(R.string.preferences_endpoint), endpoint)
                commit()
            }
        }

        fun getEndpoint(activity: Activity): String {
            val sharedPref = getSharedPrefs(activity) ?: return Constant.defaultApiUrl
            return sharedPref.getString(
                activity.getString(R.string.preferences_endpoint),Constant.defaultApiUrl
            ) ?: Constant.defaultApiUrl
        }


        fun setAccessToken(activity: Activity, token: String?) {
            val sharedPref = getSharedPrefs(activity) ?: return
            with(sharedPref.edit()) {
                putString(activity.getString(R.string.token), token)
                commit()
            }
        }

        fun  getAccessToken(activity: Activity): String {
            val sharedPref = getSharedPrefs(activity) ?: return Constant.defaultApiUrl
            return sharedPref.getString(
                activity.getString(R.string.token),Constant.defaultApiUrl
            ) ?: Constant.defaultApiUrl
        }
    }
}