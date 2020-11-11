package com.abona_erp.driverapp.data.remote.connection

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.abona_erp.driverapp.data.remote.connection.base.ConnectivityProvider
import com.abona_erp.driverapp.data.remote.connection.base.ConnectivityProviderBaseImpl

@RequiresApi(Build.VERSION_CODES.N)
class ConnectivityProviderImpl(private val cm: ConnectivityManager) :
    ConnectivityProviderBaseImpl() {

    private val networkCallback = ConnectivityCallback()

    override fun subscribe() {
        cm.registerDefaultNetworkCallback(networkCallback)
    }

    override fun unsubscribe() {
        cm.unregisterNetworkCallback(networkCallback)
    }

    override fun getNetworkState(): ConnectivityProvider.NetworkState {
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return if (capabilities != null) {
            ConnectivityProvider.NetworkState.ConnectedState.Connected(capabilities)
        } else {
            ConnectivityProvider.NetworkState.NotConnectedState
        }
    }

    private inner class ConnectivityCallback : NetworkCallback() {

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            dispatchChange(ConnectivityProvider.NetworkState.ConnectedState.Connected(capabilities))
        }

        override fun onLost(network: Network) {
            dispatchChange(ConnectivityProvider.NetworkState.NotConnectedState)
        }
    }
}