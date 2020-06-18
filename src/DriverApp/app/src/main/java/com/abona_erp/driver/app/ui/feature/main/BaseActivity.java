package com.abona_erp.driver.app.ui.feature.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.di.components.ActivityComponent;
import com.abona_erp.driver.app.di.components.ApplicationComponent;
import com.abona_erp.driver.app.di.modules.ActivityModule;


public abstract class BaseActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private ActivityComponent activityComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        activityComponent = getApplicationComponent().provideActivityComponent(new ActivityModule(this));
        super.onCreate(savedInstanceState);
//        injectDependency();
    }

    ApplicationComponent getApplicationComponent() {
        return ((App) getApplication()).getApplicationComponent();
    }


    public abstract void injectDependency();

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }
}
