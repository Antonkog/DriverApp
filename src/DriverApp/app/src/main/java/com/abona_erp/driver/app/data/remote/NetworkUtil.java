package com.abona_erp.driver.app.data.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    public static boolean isConnected(Context context) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return deprecatedCheck(cm); //todo: use android.net.ConnectivityManager.NetworkCallback
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Network network = cm.getActiveNetwork();
//            if (network == null) return false;
//            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
//            if (capabilities == null) return false;
//            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
//                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
//                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
//            ) return true;
//            else return false;
//        } else {
//            return deprecatedCheck(cm);
//        }
    }

    @SuppressWarnings("deprecation")
    private static boolean deprecatedCheck(ConnectivityManager cm) {
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
    }
}