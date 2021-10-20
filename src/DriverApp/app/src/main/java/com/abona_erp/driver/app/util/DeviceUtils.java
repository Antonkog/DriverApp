package com.abona_erp.driver.app.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.abona_erp.driver.app.logging.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class DeviceUtils {

  public static String getUniqueIMEI(Context context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
      return useDeprecatedMac(context);
    else{
      String id =  getUniqueID(context);
      Log.e("DeviceUtils", "deviceID: " +id);
      return id;
    }

  }

  public static boolean isDeviceIDChanged(String oldID,Context context){
    return !oldID.equals(getUniqueIMEI(context));
  }

  /**
   * see:
   * https://developer.android.com/training/articles/user-data-ids#instance-ids-guids
   * https://developer.android.com/training/safetynet/verify-apps
   * @param context
   * @return ANDROID_ID if READ_PRIVILEGED_PHONE_STATE  permission exist, or serial if api is <9 or 12 digits pseudo-unique id - why changed:
   * Between Android 6.0 (API level 23) and Android 9 (API level 28), local device MAC addresses, such as Wi-Fi and Bluetooth, aren't available via third-party APIs. The WifiInfo.getMacAddress() method and the BluetoothAdapter.getDefaultAdapter().getAddress() method both return 02:00:00:00:00:00
   * author A.KOGAN
   */
  private static String getUniqueID(Context context) {
    StringBuilder idBuilder = new StringBuilder();
    try {
      idBuilder.append(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
      if (!idBuilder.toString().isEmpty()) return idBuilder.toString();
      else {
        appendSerial(idBuilder);
      }
      if (!idBuilder.toString().isEmpty()) return idBuilder.toString();
      else {
        idBuilder.append(getPseudoId());
      }
    } catch (Exception e) {
      Log.w(DeviceUtils.class.getClass().getCanonicalName(), "getUniqueID Exception: " + e.getMessage());
    }
    return idBuilder.toString();
  }

  public static String getSerial(){
    StringBuilder sb = new StringBuilder();
    try {
      appendSerial(sb);
    } catch (Exception e){
      Log.d("DeviceUtils", e.toString());
    } finally {
      sb.append("N/A on API > 29");
    }
    return sb.toString();
  }
  private static void appendSerial(StringBuilder idBuilder) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O //serial is available in this range of api only
            && Build.VERSION.SDK_INT < Build.VERSION_CODES.P
    ) {
      idBuilder.append(Build.getSerial());
    } else if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1){
      idBuilder.append(Build.SERIAL);
    }
  }

  @NotNull
  private static String getPseudoId() {
    try {
      return "35" + (Build.BOARD.length() % 10)
              + (Build.BRAND.length() % 10)
              + (Build.HOST.length() % 10) //CPU_ABI
              + (Build.DEVICE.length() % 10)
              + (Build.MANUFACTURER.length() % 10)
              + (Build.MODEL.length() % 10)
              + (Build.PRODUCT.length() % 10);
    } catch (Exception e){
      return "";
    }
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
  @Deprecated
  @Nullable
  private static String useDeprecatedMac(Context context) {
    try {
      TelephonyManager tm = ServiceUtil.getTelephonyManager(context);
      String imei;

      if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          imei = tm.getImei();
        } else {
          imei = tm.getDeviceId();
        }

        if (imei != null && !imei.isEmpty()) {
          return imei;
        } else {
          // GET MAC ADDRESS:
          String mac = getMacAddress("eth0");
          if (mac == null) {
            mac = getMacAddress("wlan0");
          }
          if (mac == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              try {
                Build.getSerial();
              } catch (SecurityException e) {
                e.printStackTrace();
              }
            } else {
              return Build.SERIAL;
            }
          }
          return mac;
        }
      }
  } catch (Exception e) {
    Log.w("Error", e.getMessage());
  }

    return null;
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
