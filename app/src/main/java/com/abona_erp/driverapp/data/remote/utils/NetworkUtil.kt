package com.abona_erp.driverapp.data.remote.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.os.Build
import androidx.annotation.RequiresApi

object NetworkUtil {
    const val TAG = "NetworkUtil"

    fun isConnected(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isConnectedNewApi(context)
        } else {
            isConnectedOld(context)
        }
    }

    @Suppress("DEPRECATION")
    private fun isConnectedOld(context: Context): Boolean {
        val connManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetworkInfo
        return networkInfo?.isConnected ?: false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isConnectedNewApi(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities?.hasCapability(NET_CAPABILITY_INTERNET) == true
    }

    /*
     fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }

    fun checkSomeNetworkConnected(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isWifiConn: Boolean = false
        var isMobileConn: Boolean = false
        connMgr.allNetworks.forEach { network ->
            connMgr.getNetworkInfo(network).apply {
                if (type == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn = isWifiConn or isConnected
                }
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn = isMobileConn or isConnected
                }
            }
        }
        Log.d(TAG, "Wifi connected: $isWifiConn")
        Log.d(TAG, "Mobile connected: $isMobileConn")

        return isMobileConn&&isWifiConn
    }

    const val TYPE_WIFI = 1
    const val TYPE_MOBILE = 2
    const val TYPE_NOT_CONNECTED = 0
    const val NETWORK_STATUS_NOT_CONNECTED = 0
    const val NETWORK_STATUS_WIFI = 1
    const val NETWORK_STATUS_MOBILE = 2

    fun getConnectivityStatus(context: Context): Int {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }



    fun getConnectivityStatusString(context: Context): Int {
        val conn = getConnectivityStatus(context)
        var status = 0
        when (conn) {
            TYPE_WIFI -> {
                status = NETWORK_STATUS_WIFI
            }
            TYPE_MOBILE -> {
                status = NETWORK_STATUS_MOBILE
            }
            TYPE_NOT_CONNECTED -> {
                status = NETWORK_STATUS_NOT_CONNECTED
            }
        }
        return status
    }

     */

}