package com.redhotapp.driverapp.data.local

import android.content.Context
import android.content.SharedPreferences
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.Constant

class Preferences {

    companion object Companion {
        private fun getSharedPrefs(context: Context): SharedPreferences? {
            return context.getSharedPreferences(
                context.getString(R.string.preferences_id), Context.MODE_PRIVATE
            )
        }


        fun setEndpoint(context: Context, endpoint: String?) {
            val sharedPref = getSharedPrefs(context) ?: return
            with(sharedPref.edit()) {
                putString(context.getString(R.string.preferences_endpoint), endpoint)
                commit()
            }
        }

        fun getEndpoint(context: Context): String {
            val sharedPref = getSharedPrefs(context) ?: return Constant.defaultApiUrl
            return sharedPref.getString(
                context.getString(R.string.preferences_endpoint),Constant.defaultApiUrl
            ) ?: Constant.defaultApiUrl
        }


        fun setAccessToken(context: Context, token: String?) {
            val sharedPref = getSharedPrefs(context) ?: return
            with(sharedPref.edit()) {
                putString(context.getString(R.string.token), token)
                commit()
            }
        }

        fun  getAccessToken(context: Context): String? {
            val sharedPref = getSharedPrefs(context).let {
                return it?.getString(
                    context.getString(R.string.token), null
                )
            }
        }
    }
}