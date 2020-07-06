package com.redhotapp.driverapp.util.preferences;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.redhotapp.driverapp.DriverApp;


public abstract class BasePreference<T> {
	private final String mKey;
	private final T mDefaultValue;
	private volatile T mValue;

	public BasePreference(String key, T defaultValue) {
		mKey = key;
		mDefaultValue = defaultValue;
	}

	protected void init() {
		mValue = load();
	}

	protected abstract T load();

	protected abstract void save();

	public String getKey() {
		return mKey;
	}

	public T getDefault() {
		return mDefaultValue;
	}

	public T get() {
		return mValue;
	}

    public T getOrElse(T alter) {
        return null == mValue ? alter : mValue;
    }

	public void set(T value) {
		mValue = value;
		if (mValue == null) {
			getPrefs().edit().remove(getKey()).apply();
		} else {
			save();
		}
	}

	public static SharedPreferences getPrefs() {
		return PreferenceManager.getDefaultSharedPreferences(DriverApp.Companion.getContext());
	}
}
