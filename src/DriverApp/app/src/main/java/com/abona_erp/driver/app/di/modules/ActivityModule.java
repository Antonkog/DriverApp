package com.abona_erp.driver.app.di.modules;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.abona_erp.driver.app.di.scopes.ActivityScope;

import java.lang.ref.WeakReference;

import dagger.Module;
import dagger.Provides;


@Module
public class ActivityModule {
    private WeakReference<AppCompatActivity> activity;

    public ActivityModule(AppCompatActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Provides
    @ActivityScope
    Context provideActivityContext() {
        return activity.get();
    }

    @Provides
    @ActivityScope
    Activity provideActivity() {
        return activity.get();
    }



}
