package com.abona_erp.driver.app.ui.feature.main;

import android.Manifest;

public interface Constants {

    String EXTRAS_ALARM_TASK_ID = "extras_alarm_task_id";
    String WORK_TAG_SUFFIX = "alarmTask";
    String WORK_TAG_DEVICE_UPDATE = "deviceUpdateTask";
    int REPEAT_TIME = 5;
    int FLEX_TIME = 2;
    int REPEAT_COUNT = 3;
    int REPEAT_TIME_MIGRATION = 1500;
    int SUCCESS_CODE = 200;

    int TEST_TIME_QUOTES = 10;
    String PACKAGE_NAME = "com.abona_erp.driver.app";
    int REQUEST_PERMISSIONS_KEY = 111;

    String LOG_FILE_PREFIX = "actionHistory";
    String FILE_PROVIDER_AUTHORITY = "com.abona_erp.driver.app.provider";
    String LOG_FILE_EXTENSION = ".csv";

    String permissions[] = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
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