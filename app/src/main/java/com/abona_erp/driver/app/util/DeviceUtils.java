package com.abona_erp.driver.app.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.abona_erp.driver.app.logging.Log;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class DeviceUtils {
  
  public static String getUniqueIMEI(Context context) {
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
      } else {
        return null;
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
