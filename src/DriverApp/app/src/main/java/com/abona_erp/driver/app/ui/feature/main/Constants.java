package com.abona_erp.driver.app.ui.feature.main;

import android.Manifest;

public interface Constants {

    String PACKAGE_NAME = "com.abona_erp.driver.app";
    //below keys for communication with mobile app
    String CLIENT_IDS_BROADCAST = PACKAGE_NAME + ".broadcast";
    String EXTRA_DEVICE_ID = PACKAGE_NAME +".DeviceID";
    String EXTRA_CLIENT_ID = PACKAGE_NAME +".ClientID";

    int MOBILE_APP_VERSION = 210010226;
    String MOBILE_PACKAGE = "com.abona_erp.driverapp";
    String MOBILE_PACKAGE_BROADCAST = MOBILE_PACKAGE +".broadcast";
    int FIND_APP_DELAY_MIN = 15;
    String EXTRA_MIGRATION_DONE = MOBILE_PACKAGE +".MIGRATION_DONE";
    //end of comminication keys
    String EXTRAS_ALARM_TASK_ID = "extras_alarm_task_id";
    String EXTRAS_FCM_MESSAGE = "extras_alarm_task_id";
    String ALARM_TAG_SUFFIX = "alarmTask";
    String PARSE_FCM_TAG_SUFFIX = "parseFCMTask";
    String WORK_TAG_DEVICE_UPDATE = "deviceUpdateTask";
    String SECURITY_CODE = "0000";
    int REPEAT_TIME = 5;
    int FLEX_TIME = 2;
    int REPEAT_COUNT = 3;
    int REPEAT_TIME_MIGRATION = 1500;
    int SUCCESS_CODE = 200;
    int DELAY_FOR_CHANGE_HISTORY = 1000; // see that bug7821, for some reason we need delay when sending to server - 1 second
    int DELAY_FOR_UPLOAD_PHOTOS = 5000; // to prevent photos duplication
    int TIMEOUT_SMTP_SEND = 5000; // to prevent photos duplication

    int TEST_TIME_QUOTES = 10;
    int REQUEST_PERMISSIONS_KEY = 111;

    String LOG_FILE_PREFIX = "actionHistory";
    String FILE_PROVIDER_AUTHORITY = "com.abona_erp.driver.app.provider";
    String LOG_FILE_EXTENSION = ".csv";
    String FILE_NAME_DIVIDER = "_";

    String permissions[] = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    int NOTIFICATION_FMC_MESSAGE = 111;
    int NOTIFICATION_CHECK_ALARM_ID = 112;
    int NOTIFICATION_NOT_EXIST_ALARM_ID = 113;
    String NOTIFICATION_CHANNEL_ID = "Abona Tasks";
    int ALARM_CHECK_JOB_ID = 47;
    String EXTRAS_START_SETTINGS = "extras_start_settings";
    String LANG_TO_SERVER_ENGLISH = "en_US";
    String LANG_TO_SERVER_GERMAN = "de_DE";
    String LANG_TO_SERVER_RUSSIAN = "ru_RU";
    String LANG_TO_SERVER_UKRAINIAN = "uk_UA";
    String LANG_TO_SERVER_POLISH = "pl_PL";
    String KEY_KILL_BACKGROUND_SERVICE = "key_kill_background_service";
}