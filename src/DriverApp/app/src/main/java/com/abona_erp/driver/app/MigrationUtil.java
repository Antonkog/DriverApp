package com.abona_erp.driver.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.app.util.DeviceUtils;
import com.abona_erp.driver.app.util.TextSecurePreferences;

public class MigrationUtil {
    private static final String TAG = "MigrationUtil";

    public static void sendBroadcast(Context context) {
        Intent intent = new Intent(Constants.CLIENT_IDS_BROADCAST);
        intent.putExtra(Constants.EXTRA_CLIENT_ID, TextSecurePreferences.getClientID());
        intent.putExtra(Constants.EXTRA_DEVICE_ID, DeviceUtils.getUniqueIMEI(context));
        intent.putExtra(Constants.EXTRA_LANGUAGE,TextSecurePreferences.getLanguage(context));
        context.sendBroadcast(intent);
        android.util.Log.e(TAG, "broadcast sent");
    }

    public static boolean mobileVersionExist(Context context) {
        try {
            int appVersionCode = context.getPackageManager()
                    .getPackageInfo(Constants.MOBILE_PACKAGE, 0).versionCode;
            if (appVersionCode > Constants.MOBILE_APP_VERSION) {
                return true;// found and version support migration change;
            } else {
                Log.e(TAG, "Mobile app old for migration: version is: \n" + appVersionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Mobile app not found: " + e.getMessage());
            return false;//not found
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;//other exceptions
        }
        return false;//other exceptions
// the getLaunchIntentForPackage returns an intent that you can use with startActivity()
    }
}