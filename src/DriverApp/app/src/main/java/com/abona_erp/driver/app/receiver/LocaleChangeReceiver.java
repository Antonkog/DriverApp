package com.abona_erp.driver.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.app.ui.feature.main.fragment.settings.SettingsFragment;
import com.abona_erp.driver.app.util.TextSecurePreferences;

import java.util.Locale;

public class LocaleChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Locale.getDefault().getISO3Language().toLowerCase()){
                case "rus":
                    TextSecurePreferences.setLanguage(context, Constants.LANG_TO_SERVER_RUSSIAN);
                    break;
                case "ukr":
                    TextSecurePreferences.setLanguage(context, Constants.LANG_TO_SERVER_UKRAINIAN);
                    break;
                case "eng":
                    TextSecurePreferences.setLanguage(context, Constants.LANG_TO_SERVER_ENGLISH);
                    break;
                case "pol":
                    TextSecurePreferences.setLanguage(context, Constants.LANG_TO_SERVER_POLISH);
                    break;
                case "deu":
                    TextSecurePreferences.setLanguage(context, Constants.LANG_TO_SERVER_GERMAN);
                    break;
                default:
                    break;
            }
            SettingsFragment.updatePreferenceFlags();
        }
}
