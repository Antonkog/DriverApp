package com.abona_erp.driver.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.core.base.ContextUtils;

import java.util.Collections;
import java.util.Set;

public class TextSecurePreferences {
  
  private static final String TAG = TextSecurePreferences.class.getSimpleName();
  
  private static final String PREF_FIRST_TIME_RUN              = "pref_first_time_run";
  private static final String PREF_PERMISSION                  = "pref_permission";
  private static final String PREF_GCM_REGISTRATION_ID         = "pref_gcm_registration_id";
  private static final String PREF_GCM_REGISTRATION_ID_UPDATE  = "pref_gcm_registration_id_update";
  private static final String PREF_GCM_REGISTRATION_ID_TIME    = "pref_gcm_registration_id_last_set_time";
  private static final String PREF_DEVICE_REGISTRATED          = "pref_device_registrated";
  private static final String PREF_ACCESS_TOKEN                = "pref_access_token";
  
  private static final String PREF_ACTIVITY_BACK_ENABLE        = "pref_activity_back_enable";
  
  private static final String PREF_CLIENT_NAME                 = "pref_client_name";
  private static final String PREF_VEHICLE_REGISTRATION_NUMBER = "pref_vehicle_registration_number";
  private static final String PREF_TASK_PERCENTAGE             = "pref_task_percentage";
  
  private static final String PREF_SERVER_IP_ADDRESS           = "pref_server_ip_address";
  private static final String PREF_SERVER_PORT                 = "pref_server_port";
  
  
  
  

  
  
  
  
  
  public static final String REGISTERED_GCM_PREF               = "pref_gcm_registered";
  
  private static final String GCM_DISABLED_PREF                = "pref_gcm_disabled";
  
  private static final String GCM_REGISTRATION_ID_VERSION_PREF = "pref_gcm_registration_id_version";
  
  private static final String GCM_SENDER_ID_PREF               = "pref_gcm_sender_id";
  
  public static final String LANGUAGE_PREF                     = "pref_language";
  

  


  
  public static boolean isDeviceRegistrated(Context context) {
    return getBooleanPreference(context, PREF_DEVICE_REGISTRATED, false);
  }
  
  public static void setDeviceRegistrated(Context context, boolean success) {
    setBooleanPreference(context, PREF_DEVICE_REGISTRATED, success);
  }
  
  public static boolean isDevicePermissionsGranted(Context context) {
    return getBooleanPreference(context, PREF_PERMISSION, false);
  }
  
  public static void setDevicePermissionsGranted(Context context, boolean granted) {
    setBooleanPreference(context, PREF_PERMISSION, granted);
  }
  
  public static boolean isDeviceFirstTimeRun(Context context) {
    return getBooleanPreference(context, PREF_FIRST_TIME_RUN, false);
  }
  
  public static void setDeviceFirstTimeRun(Context context, boolean firstTimeRun) {
    setBooleanPreference(context, PREF_FIRST_TIME_RUN, firstTimeRun);
  }
  
  public static boolean isFcmDisabled(Context context) {
    return getBooleanPreference(context, GCM_DISABLED_PREF, false);
  }
  
  public static void setFcmDisabled(Context context, boolean disabled) {
    setBooleanPreference(context, GCM_DISABLED_PREF, disabled);
  }
  
  public static void setFcmToken(Context context, String registrationId) {
    setStringPreference(context, PREF_GCM_REGISTRATION_ID, registrationId);
    //setIntegerPrefrence(context, GCM_REGISTRATION_ID_VERSION_PREF, Util.getCanonicalVersionCode());
  }
  
  public static String getFcmToken(Context context) {
    //int storedRegistrationIdVersion = getIntegerPreference(context, GCM_REGISTRATION_ID_VERSION_PREF, 0);
    
    //if (storedRegistrationIdVersion != Util.getCanonicalVersionCode()) {
    //  return null;
    //} else {
      return getStringPreference(context, PREF_GCM_REGISTRATION_ID, null);
    //}
  }
  
  public static void setFcmTokenUpdate(Context context, boolean update) {
    setBooleanPreference(context, PREF_GCM_REGISTRATION_ID_UPDATE, update);
  }
  
  public static boolean getFcmTokenUpdate(Context context) {
    return getBooleanPreference(context, PREF_GCM_REGISTRATION_ID_UPDATE, false);
  }
  
  public static String getFcmTokenLastSetTime(Context context) {
    return getStringPreference(context, PREF_GCM_REGISTRATION_ID_TIME, "");
  }
  
  public static void setFcmTokenLastSetTime(Context context, String timestamp) {
    setStringPreference(context, PREF_GCM_REGISTRATION_ID_TIME, timestamp);
  }
  
  public static boolean isPushRegistered(Context context) {
    return getBooleanPreference(context, REGISTERED_GCM_PREF, false);
  }
  
  public static void setPushRegistered(Context context, boolean registered) {
    Log.i(TAG, "Setting push registered: " + registered);
    setBooleanPreference(context, REGISTERED_GCM_PREF, registered);
  }
  
  public static String getFCMSenderID(Context context) {
    return getStringPreference(context, GCM_SENDER_ID_PREF, "724562515953");
  }
  
  public static void setFCMSenderID(Context context, String senderID) {
    setStringPreference(context, GCM_SENDER_ID_PREF, senderID);
  }
  
  public static String getAccessToken(Context context) {
    return getStringPreference(context, PREF_ACCESS_TOKEN, "");
  }
  
  public static void setAccessToken(Context context, String accessToken) {
    setStringPreference(context, PREF_ACCESS_TOKEN, accessToken);
  }
  
  public static String getLanguage(Context context) {
    return getStringPreference(context, LANGUAGE_PREF, "en");
  }
  
  public static void setLanguage(Context context, String language) {
    setStringPreference(context, LANGUAGE_PREF, language);
  }
  
  public static boolean getActivityBackEnable(Context context) {
    return getBooleanPreference(context, PREF_ACTIVITY_BACK_ENABLE, false);
  }
  
  public static void setActivityBackEnable(Context context, boolean enable) {
    setBooleanPreference(context, PREF_ACTIVITY_BACK_ENABLE, enable);
  }
  
  public static String getClientName(Context context) {
    return getStringPreference(context, PREF_CLIENT_NAME, "");
  }
  
  public static void setClientName(Context context, String clientName) {
    setStringPreference(context, PREF_CLIENT_NAME, clientName);
  }
  
  public static String getVehicleRegistrationNumber(Context context) {
    return getStringPreference(context, PREF_VEHICLE_REGISTRATION_NUMBER,
      context.getResources().getString(R.string.registration_number));
  }
  
  public static void setVehicleRegistrationNumber(Context context, String registrationNumber) {
    setStringPreference(context, PREF_VEHICLE_REGISTRATION_NUMBER, registrationNumber);
  }
  
  public static int getTaskPercentage(Context context) {
    return getIntegerPreference(context, PREF_TASK_PERCENTAGE, 0);
  }
  
  public static void setTaskPercentage(Context context, int taskPercentage) {
    setIntegerPrefrence(context, PREF_TASK_PERCENTAGE, taskPercentage);
  }
  
  public static String getServerIpAddress() {
    return getStringPreference(ContextUtils.getApplicationContext(),
      PREF_SERVER_IP_ADDRESS, /*"https://93.189.159.10"*/"https://213.144.11.162");
  }
  
  public static void setServerIpAddress(String ipAddress) {
    setStringPreference(ContextUtils.getApplicationContext(), PREF_SERVER_IP_ADDRESS, ipAddress);
  }
  
  public static int getServerPort() {
    return getIntegerPreference(ContextUtils.getApplicationContext(),
      PREF_SERVER_PORT, 5000);
  }
  
  public static void setServerPort(int port) {
    setIntegerPrefrence(ContextUtils.getApplicationContext(),
      PREF_SERVER_PORT, port);
  }
  
  public static void setBooleanPreference(Context context, String key, boolean value) {
    PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
  }
  
  public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
    return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
  }
  
  public static void setStringPreference(Context context, String key, String value) {
    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
  }
  
  public static String getStringPreference(Context context, String key, String defaultValue) {
    return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
  }
  
  private static int getIntegerPreference(Context context, String key, int defaultValue) {
    return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultValue);
  }
  
  private static void setIntegerPrefrence(Context context, String key, int value) {
    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply();
  }
  
  private static boolean setIntegerPrefrenceBlocking(Context context, String key, int value) {
    return PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).commit();
  }
  
  private static long getLongPreference(Context context, String key, long defaultValue) {
    return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defaultValue);
  }
  
  private static void setLongPreference(Context context, String key, long value) {
    PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(key, value).apply();
  }
  
  private static void removePreference(Context context, String key) {
    PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).apply();
  }
  
  private static Set<String> getStringSetPreference(Context context, String key, Set<String> defaultValues) {
    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    if (prefs.contains(key)) {
      return prefs.getStringSet(key, Collections.<String>emptySet());
    } else {
      return defaultValues;
    }
  }
}
