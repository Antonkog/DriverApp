package com.abona_erp.driver.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.abona_erp.driver.app.logging.Log;

import java.util.Collections;
import java.util.Set;

public class TextSecurePreferences {
  
  private static final String TAG = TextSecurePreferences.class.getSimpleName();
  
  public  static final String REGISTERED_GCM_PREF              = "pref_gcm_registered";
  
  private static final String GCM_DISABLED_PREF                = "pref_gcm_disabled";
  private static final String GCM_REGISTRATION_ID_PREF         = "pref_gcm_registration_id";
  private static final String GCM_REGISTRATION_ID_VERSION_PREF = "pref_gcm_registration_id_version";
  private static final String GCM_REGISTRATION_ID_TIME_PREF    = "pref_gcm_registration_id_last_set_time";
  private static final String GCM_SENDER_ID_PREF               = "pref_gcm_sender_id";
  
  public static final String LANGUAGE_PREF                     = "pref_language";
  
  private static final String JOB_MANAGER_VERSION = "pref_job_manager_version";
  
  public static boolean isFcmDisabled(Context context) {
    return getBooleanPreference(context, GCM_DISABLED_PREF, false);
  }
  
  public static void setFcmDisabled(Context context, boolean disabled) {
    setBooleanPreference(context, GCM_DISABLED_PREF, disabled);
  }
  
  public static void setFcmToken(Context context, String registrationId) {
    setStringPreference(context, GCM_REGISTRATION_ID_PREF, registrationId);
    setIntegerPrefrence(context, GCM_REGISTRATION_ID_VERSION_PREF, Util.getCanonicalVersionCode());
  }
  
  public static String getFcmToken(Context context) {
    int storedRegistrationIdVersion = getIntegerPreference(context, GCM_REGISTRATION_ID_VERSION_PREF, 0);
    
    if (storedRegistrationIdVersion != Util.getCanonicalVersionCode()) {
      return null;
    } else {
      return getStringPreference(context, GCM_REGISTRATION_ID_PREF, null);
    }
  }
  
  public static long getFcmTokenLastSetTime(Context context) {
    return getLongPreference(context, GCM_REGISTRATION_ID_TIME_PREF, 0);
  }
  
  public static void setFcmTokenLastSetTime(Context context, long timestamp) {
    setLongPreference(context, GCM_REGISTRATION_ID_TIME_PREF, timestamp);
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
  
  public static String getLanguage(Context context) {
    return getStringPreference(context, LANGUAGE_PREF, "en");
  }
  
  public static void setLanguage(Context context, String language) {
    setStringPreference(context, LANGUAGE_PREF, language);
  }
  
  public static void setJobManagerVersion(Context context, int version) {
    setIntegerPrefrence(context, JOB_MANAGER_VERSION, version);
  }
  
  public static int getJobManagerVersion(Context contex) {
    return getIntegerPreference(contex, JOB_MANAGER_VERSION, 1);
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
