package com.abona_erp.driverapp.data.remote.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil {
    const val TAG = "NetworkUtil"


    fun isConnectedWithWifi(context: Context): Boolean {
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return mWifi.isConnected
    }

//
//    fun checkSomeNetworkConnected(context: Context): Boolean {
//        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        var isWifiConn: Boolean = false
//        var isMobileConn: Boolean = false
//        connMgr.allNetworks.forEach { network ->
//            connMgr.getNetworkInfo(network).apply {
//                if (type == ConnectivityManager.TYPE_WIFI) {
//                    isWifiConn = isWifiConn or isConnected
//                }
//                if (type == ConnectivityManager.TYPE_MOBILE) {
//                    isMobileConn = isMobileConn or isConnected
//                }
//            }
//        }
//        Log.d(TAG, "Wifi connected: $isWifiConn")
//        Log.d(TAG, "Mobile connected: $isMobileConn")
//
//        return isMobileConn&&isWifiConn
//    }

//    const val TYPE_WIFI = 1
//    const val TYPE_MOBILE = 2
//    const val TYPE_NOT_CONNECTED = 0
//    const val NETWORK_STATUS_NOT_CONNECTED = 0
//    const val NETWORK_STATUS_WIFI = 1
//    const val NETWORK_STATUS_MOBILE = 2

//    fun getConnectivityStatus(context: Context): Int {
//        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val activeNetwork = cm.activeNetworkInfo
//        if (null != activeNetwork) {
//            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
//            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
//        }
//        return TYPE_NOT_CONNECTED
//    }


//
//    fun getConnectivityStatusString(context: Context): Int {
//        val conn = getConnectivityStatus(context)
//        var status = 0
//        when (conn) {
//            TYPE_WIFI -> {
//                status = NETWORK_STATUS_WIFI
//            }
//            TYPE_MOBILE -> {
//                status = NETWORK_STATUS_MOBILE
//            }
//            TYPE_NOT_CONNECTED -> {
//                status = NETWORK_STATUS_NOT_CONNECTED
//            }
//        }
//        return status
//    }
}