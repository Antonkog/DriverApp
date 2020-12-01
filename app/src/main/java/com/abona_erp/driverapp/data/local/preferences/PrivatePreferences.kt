package com.abona_erp.driverapp.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import com.abona_erp.driverapp.data.Constant

class PrivatePreferences {

    companion object Companion {
        private fun getSharedPrefs(context: Context): SharedPreferences? {
            return context.getSharedPreferences(
                Constant.preferencesId, Context.MODE_PRIVATE
            )
        }


        fun setEndpoint(context: Context, endpoint: String?) {
            val sharedPref = getSharedPrefs(
                context
            ) ?: return
            with(sharedPref.edit()) {
                putString(Constant.preferencesEndpoint, endpoint)
                commit()
            }
        }

        fun getEndpoint(context: Context): String? {
            return getSharedPrefs(
                context
            )?.getString(
                Constant.preferencesEndpoint, null
            )
        }


        fun setAccessToken(context: Context, token: String?) {
            val sharedPref = getSharedPrefs(
                context
            ) ?: return
            with(sharedPref.edit()) {
                putString(Constant.token, token)
                commit()
            }
        }

        fun getAccessToken(context: Context): String? {
            getSharedPrefs(
                context
            ).let {
                return it?.getString(
                    Constant.token, null
                )
            }
        }

        fun setFCMToken(context: Context, token: String?) {
            val sharedPref = getSharedPrefs(
                context
            ) ?: return
            with(sharedPref.edit()) {
                putString(Constant.tokenFcm, token)
                commit()
            }
        }

        fun getFCMToken(context: Context): String? {
            getSharedPrefs(
                context
            ).let {
                return it?.getString(
                    Constant.tokenFcm, null
                )
            }
        }
    }
}