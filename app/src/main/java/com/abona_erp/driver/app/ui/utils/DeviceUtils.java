package com.abona_erp.driver.app.ui.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.abona_erp.driver.app.BuildConfig;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class DeviceUtils {

  /**
   * see:
   * https://developer.android.com/training/articles/user-data-ids#instance-ids-guids
   * https://developer.android.com/training/safetynet/verify-apps
   * @param context
   * @return ANDROID_ID if READ_PRIVILEGED_PHONE_STATE  permission exist, or serial if api is <9 or 12 digits pseudo-unique id - why changed:
   * Between Android 6.0 (API level 23) and Android 9 (API level 28), local device MAC addresses, such as Wi-Fi and Bluetooth, aren't available via third-party APIs. The WifiInfo.getMacAddress() method and the BluetoothAdapter.getDefaultAdapter().getAddress() method both return 02:00:00:00:00:00
   * author A.KOGAN
   */
  public static String getUniqueID(Context context) {
    if(BuildConfig.DEBUG)return "78c5b0bc3b2de700";
    StringBuilder idBuilder = new StringBuilder();
    try {
      idBuilder.append(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
      if (!idBuilder.toString().isEmpty()) return idBuilder.toString();
      else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O //serial is available in this range of api only
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.P
        ) {
          idBuilder.append(Build.getSerial());
        } else if(Build.VERSION.SDK_INT<= Build.VERSION_CODES.N_MR1){
          idBuilder.append(Build.SERIAL);
        }
      }
      if (!idBuilder.toString().isEmpty()) return idBuilder.toString();
      else {

        idBuilder .append(Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.DEVICE.length()% 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10+ Build.TAGS.length() % 10 + Build.TYPE + Build.USER.length() % 10);
      }
    } catch (Exception e) {
      Log.w(DeviceUtils.class.getClass().getCanonicalName(), "getUniqueID Exception: " + e.getMessage());
    }
    return idBuilder.toString();
  }


  /**
   * that is old method for device identification
   * // on api level >= 29 would NOT work -
   * this method requires    <uses-permission android:name="android.permission.READ_PRIVILEGED_PHONE_STATE"  that is ProtectedPermissions/>
   * can't rewrite (some customers already using that method for identification.
   * so i made small change when was returning null  now returns Settings.Secure.ANDROID_ID
   * see https://source.android.com/devices/tech/config/device-identifiers
   * @param context
   * @return mac or serial
   * comments by A.KOGAN
   */
  public static String getUniqueIMEI(Context context) {
    return "356136105761250"; // "356136109798993";
  }
  
  public static String getMacAddress(String interfaceName) {
    try {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface networkInterface = interfaces.nextElement();
        
        if (TextUtils.equals(networkInterface.getName(), interfaceName)) {
          byte[] bytes = networkInterface.getHardwareAddress();
          StringBuilder builder = new StringBuilder();
          for (byte b : bytes) {
            builder.append(String.format("%02X:", b));
          }
          
          if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
          }
          
          return builder.toString();
        }
      }
      return null;
    } catch (SocketException e) {
      e.printStackTrace();
      return null;
    }
  }
}
