package com.abona_erp.driver.app.ui.base;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.di.components.ActivityComponent;
import com.abona_erp.driver.app.di.components.ApplicationComponent;
import com.abona_erp.driver.app.di.modules.ActivityModule;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.Util;
import com.abona_erp.driver.app.util.dynamiclanguage.DynamicLanguageActivityHelper;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private ActivityComponent activityComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
       // injectDependency(); only in one place for now
        activityComponent = getApplicationComponent().provideActivityComponent(new ActivityModule(this));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DynamicLanguageActivityHelper.recreateIfNotInCorrectLanguage(this, TextSecurePreferences.getLanguage(this));
        Log.d(BaseActivity.class.getSimpleName(), Locale.getDefault().toString());
    }

    ApplicationComponent getApplicationComponent() {
        return ((App) getApplication()).getApplicationComponent();
    }


    public abstract void injectDependency(); // to ensure that method exist in our activities

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }


    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 1 )
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
        else  Util.askNeedExit(this);
    }
}
