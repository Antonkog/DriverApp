package com.abona_erp.driver.app.ui.base;

import androidx.appcompat.app.AppCompatActivity;

import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.dynamiclanguage.DynamicLanguageActivityHelper;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        DynamicLanguageActivityHelper.recreateIfNotInCorrectLanguage(this,
        TextSecurePreferences.getLanguage(this));
        Log.d(BaseActivity.class.getSimpleName(), Locale.getDefault().toString());
    }
}
