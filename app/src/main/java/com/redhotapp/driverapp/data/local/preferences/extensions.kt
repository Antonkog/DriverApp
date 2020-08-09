package com.redhotapp.driverapp.data.local.preferences

import android.content.SharedPreferences

fun SharedPreferences.putAny(name: String, any: Any) {
    when (any) {
        is String -> edit().putString(name, any).apply()
        is Int -> edit().putInt(name, any).apply()
        is Boolean -> edit().putBoolean(name,any).apply()
        is Long -> edit().putLong(name,any).apply()
        is Float -> edit().putFloat(name,any).apply()
        // also accepts StringSet
    }
}
// remove entry from shared preference
fun SharedPreferences.remove(name:String){
    edit().remove(name).apply()
}