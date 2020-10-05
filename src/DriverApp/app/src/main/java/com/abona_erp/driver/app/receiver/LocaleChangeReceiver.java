package com.abona_erp.driver.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.dynamiclanguage.DynamicLanguageContextWrapper;

import java.util.Locale;

import static com.abona_erp.driver.app.util.Util.updatePreferenceFlags;

public class LocaleChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Locale.getDefault().getISO3Language().toLowerCase()){
                case "rus":
                case "ukr":
                    TextSecurePreferences.setLanguage(context, Constants.LANG_TO_SERVER_RUSSIAN); //as ukrainian is not implemented on server.
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
            DynamicLanguageContextWrapper.updateContext(context,
                    TextSecurePreferences.getLanguage(context));
            updatePreferenceFlags();
        }
}
