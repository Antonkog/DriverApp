package com.abona_erp.driverapp.data.local.preferences

import android.content.SharedPreferences

fun SharedPreferences.putAny(name: String, any: Any) {
    when (any) {
        is String -> edit().putString(name, any).apply()
        is Boolean -> edit().putBoolean(name, any).apply()
        is Long -> edit().putLong(name, any).apply()
        is Int -> edit().putInt(name, any).apply()
        is Float -> edit().putFloat(name, any).apply()
        // also accepts StringSet
    }
}

fun SharedPreferences.putLong(name: String, long: Long) {
    edit().putLong(name, long).apply()
}

// remove entry from shared preference
fun SharedPreferences.remove(name: String) {
    edit().remove(name).apply()
}