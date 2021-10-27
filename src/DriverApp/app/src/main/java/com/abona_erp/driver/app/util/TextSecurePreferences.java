package com.abona_erp.driver.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.core.base.ContextUtils;

import java.util.Collections;
import java.util.Set;

public class TextSecurePreferences {
  
  private static final String TAG = TextSecurePreferences.class.getSimpleName();
  
  private static final String PREF_FIRST_TIME_RUN              = "pref_first_time_run";
  private static final String PREF_LOGIN                       = "pref_login";
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
  
  private static final String PREF_ENDPOINT                    = "pref_endpoint";
  private static final String PREF_CLIENT_ID                   = "pref_client_id";
  
  private static final String PREF_REST_API_VERSION            = "pref_rest_api_version";
  
  
  
  public static final String REGISTERED_GCM_PREF               = "pref_gcm_registered";
  
  private static final String GCM_DISABLED_PREF                = "pref_gcm_disabled";
  
  private static final String GCM_REGISTRATION_ID_VERSION_PREF = "pref_gcm_registration_id_version";
  
  private static final String GCM_SENDER_ID_PREF               = "pref_gcm_sender_id";
  
  public static final String LANGUAGE_PREF                     = "pref_language";
  

  public static final String PREF_NOTIFICATION_BEFORE_TASK    = "pref_notification_before_task";
  public static final String UPLOAD_CONFIRMATION_COUNTER = "upload_confirmation_counter";
  public static final String DOWNLOAD_CONFIRMATION_COUNTER = "download_confirmation_counter";
  public static final String PREF_OLD_DEVICE_ID    = "pref_old_device_id";
  public static final String UPDATE_LANG_CODE    = "pref_update_lang_code";
  public static final String UPDATE_ALL_TASKS    = "pref_update_all_tasks";
  public static final String UPDATE_DELAY_REASON = "pref_update_delay_reason";
  public static final String UPDATE_DEVICE = "pref_update_device";
  
  public static final String PREF_MANDANT_ID = "pref_mandant_id";
  
  public static final String PREF_STOP_SERVICE = "pref_stop_service";
  public static final String PREF_REGISTRATION_STARTED = "pref_registration_started";
  public static final String MIGRATION_TO_MOBILE_DONE = "pref_migration_to_mobile";
  public static final String PREF_APP_VERSION_CODE = "pref_app_version_code";
  public static final String PREF_APP_VERSION_NAME = "pref_app_version_name";
  
  public static final String PREF_ONE_TIME_CHECK_ENDPOINT = "pref_one_time_check_endpoint";
  
  public static final String PREF_PATCH_00 = "pref_patch_00";
  public static final String PREF_PATCH_00_STATE = "pref_patch_00_state";
  public static final String PREF_PATCH_00_RANDOM_NUMBER = "pref_patch_00_random_number";
  
  public static String getRestApiVersion() {
    return getStringPreference(ContextUtils.getApplicationContext(),
      PREF_REST_API_VERSION, "");
  }

  public static void setRestApiVersion(String restApiVersion) {
    setStringPreference(ContextUtils.getApplicationContext(),
      PREF_REST_API_VERSION, restApiVersion);
  }

  public static boolean isRegistrationStarted() {
    return getBooleanPreference(ContextUtils.getApplicationContext(),
      PREF_REGISTRATION_STARTED, false);
  }
  
  public static void setRegistrationStarted(boolean started) {
    setBooleanPreference(ContextUtils.getApplicationContext(),
      PREF_REGISTRATION_STARTED, started);
  }

  public static boolean isMigrationDone() {
    return getBooleanPreference(ContextUtils.getApplicationContext(),
            MIGRATION_TO_MOBILE_DONE, false);
  }

  public static void setMigrationDone(boolean done) {
    setBooleanPreference(ContextUtils.getApplicationContext(),
            MIGRATION_TO_MOBILE_DONE, done);
  }

  public static boolean enableLoginPage() {
    return getBooleanPreference(ContextUtils.getApplicationContext(),
      PREF_LOGIN, true);
  }
  
  public static void setLoginPageEnable(boolean enable) {
    setBooleanPreference(ContextUtils.getApplicationContext(),
      PREF_LOGIN, enable);
  }
  
  public static int getMandantID() {
    return getIntegerPreference(ContextUtils.getApplicationContext(), PREF_MANDANT_ID, 0);
  }
  
  public static void setMandantID(int mandantID) {
    setIntegerPrefrence(ContextUtils.getApplicationContext(), PREF_MANDANT_ID, mandantID);
  }
  public static int getNotificationTime() {
    return getIntegerPreference(ContextUtils.getApplicationContext(), PREF_NOTIFICATION_BEFORE_TASK, Constants.REPEAT_TIME * Constants.REPEAT_COUNT);
  }

  public static void setNotificationTime(int time) {
    setIntegerPrefrence(ContextUtils.getApplicationContext(), PREF_NOTIFICATION_BEFORE_TASK, time);
  }


  public static int getUploadConfirmationCounter() {
    return getIntegerPreference(ContextUtils.getApplicationContext(), UPLOAD_CONFIRMATION_COUNTER, 0);
  }

  public static void setUploadConfirmationCounter(int counter) {
    setIntegerPrefrence(ContextUtils.getApplicationContext(), UPLOAD_CONFIRMATION_COUNTER, counter);
  }


  public static int getDownloadConfirmationCounter() {
    return getIntegerPreference(ContextUtils.getApplicationContext(), DOWNLOAD_CONFIRMATION_COUNTER, 0);
  }

  public static void setDownloadConfirmationCounter(int counter) {
    setIntegerPrefrence(ContextUtils.getApplicationContext(), DOWNLOAD_CONFIRMATION_COUNTER, counter);
  }

  public static String getOldDeviceID() {
    return getStringPreference(ContextUtils.getApplicationContext(), PREF_OLD_DEVICE_ID, null);
  }

  public static void setOldDeviceID(String deviceId) {
    setStringPreference(ContextUtils.getApplicationContext(), PREF_OLD_DEVICE_ID, deviceId);
  }

  
  public static String getClientID() {
    return getStringPreference(ContextUtils.getApplicationContext(),
      PREF_CLIENT_ID, "");
  }
  
  public static void setClientID(String clientID) {
    setStringPreference(ContextUtils.getApplicationContext(),
      PREF_CLIENT_ID, clientID);
  }
  
  public static void setStopService(boolean stop) {
    setBooleanPreference(ContextUtils.getApplicationContext(),
      PREF_STOP_SERVICE, stop);
  }
  
  public static boolean isStopService() {
    return getBooleanPreference(ContextUtils.getApplicationContext(),
      PREF_STOP_SERVICE, false);
  }
  
  public static boolean isDeviceRegistrated() {
    return getBooleanPreference(ContextUtils.getApplicationContext(),
      PREF_DEVICE_REGISTRATED, false);
  }
  
  public static void setDeviceRegistrated(boolean success) {
    setBooleanPreference(ContextUtils.getApplicationContext(),
      PREF_DEVICE_REGISTRATED, success);
  }
  
  public static boolean isDevicePermissionsGranted() {
    return getBooleanPreference(ContextUtils.getApplicationContext(),
      PREF_PERMISSION, false);
  }
  
  public static void setDevicePermissionsGranted(boolean granted) {
    setBooleanPreference(ContextUtils.getApplicationContext(),
      PREF_PERMISSION, granted);
  }
  
  public static boolean isDeviceFirstTimeRun() {
    return getBooleanPreference(ContextUtils.getApplicationContext(),
      PREF_FIRST_TIME_RUN, false);
  }
  
  public static void setDeviceFirstTimeRun(boolean firstTimeRun) {
    setBooleanPreference(ContextUtils.getApplicationContext(),
      PREF_FIRST_TIME_RUN, firstTimeRun);
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
  
  public static boolean isUpdateLangCode() {
    return getBooleanPreference(ContextUtils.getApplicationContext(), UPDATE_LANG_CODE, false);
  }
  
  public static void setUpdateLangCode(boolean update) {
    setBooleanPreference(ContextUtils.getApplicationContext(), UPDATE_LANG_CODE, update);
  }
  
  public static boolean isUpdateAllTasks() {
    return getBooleanPreference(ContextUtils.getApplicationContext(), UPDATE_ALL_TASKS, false);
  }
  
  public static void setUpdateAllTasks(boolean update) {
    setBooleanPreference(ContextUtils.getApplicationContext(), UPDATE_ALL_TASKS, update);
  }
  
  public static boolean isUpdateDelayReason() {
    return getBooleanPreference(ContextUtils.getApplicationContext(), UPDATE_DELAY_REASON, false);
  }
  
  public static void setUpdateDelayReason(boolean update) {
    setBooleanPreference(ContextUtils.getApplicationContext(), UPDATE_DELAY_REASON, update);
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
  
  public static String getEndpoint() {
    return getStringPreference(ContextUtils.getApplicationContext(),
      PREF_ENDPOINT, "https://213.144.11.162/" /*https://93.189.159.10/*/);
  }
  
  public static void setEndpoint(String endpoint) {
    setStringPreference(ContextUtils.getApplicationContext(), PREF_ENDPOINT, endpoint);
  }
  
  public static Boolean getDeviceUpdate() {
    return getBooleanPreference(ContextUtils.getApplicationContext(),
      UPDATE_DEVICE, false);
  }
  
  public static void setDeviceUpdate(Boolean update) {
    setBooleanPreference(ContextUtils.getApplicationContext(), UPDATE_DEVICE, update);
  }
  
  public static Integer getAppVersionCode() {
    return getIntegerPreference(ContextUtils.getApplicationContext(),
      PREF_APP_VERSION_CODE, 0);
  }
  
  public static void setAppVersionCode(Integer versionCode) {
    setIntegerPrefrence(ContextUtils.getApplicationContext(), PREF_APP_VERSION_CODE, versionCode);
  }
  
  public static String getAppVersionName() {
    return getStringPreference(ContextUtils.getApplicationContext(),
      PREF_APP_VERSION_NAME, "");
  }
  
  public static void setAppVersionName(String versionName) {
    setStringPreference(ContextUtils.getApplicationContext(), PREF_APP_VERSION_NAME, versionName);
  }
  
  public static Boolean getOneTimeCheckEndpoint() {
    return getBooleanPreference(ContextUtils.getApplicationContext(), PREF_ONE_TIME_CHECK_ENDPOINT, true);
  }
  
  public static void setOneTimeCheckEndpoint(Boolean enable) {
    setBooleanPreference(ContextUtils.getApplicationContext(), PREF_ONE_TIME_CHECK_ENDPOINT, enable);
  }
  
  public static Boolean isPatch00_Completed() {
    return getBooleanPreference(ContextUtils.getApplicationContext(), PREF_PATCH_00, true);
  }
  
  public static void setPatch00_Completed(Boolean completed) {
    setBooleanPreference(ContextUtils.getApplicationContext(), PREF_PATCH_00, completed);
  }
  
  public static Integer getPatch00_State() {
    return getIntegerPreference(ContextUtils.getApplicationContext(), PREF_PATCH_00_STATE, 0);
  }
  
  public static void setPatch00_State(int state) {
    setIntegerPrefrence(ContextUtils.getApplicationContext(), PREF_PATCH_00_STATE, state);
  }
  
  public static Integer getPatch00_RandomNumber() {
    return getIntegerPreference(ContextUtils.getApplicationContext(), PREF_PATCH_00_RANDOM_NUMBER, 0);
  }
  
  public static void setPatch00_RandomNumber(int randomNumber) {
    setIntegerPrefrence(ContextUtils.getApplicationContext(), PREF_PATCH_00_RANDOM_NUMBER, randomNumber);
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
