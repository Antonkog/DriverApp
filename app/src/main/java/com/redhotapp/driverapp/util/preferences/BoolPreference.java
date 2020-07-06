package com.redhotapp.driverapp.util.preferences;

import android.util.Log;

public class BoolPreference extends BasePreference<Boolean> {
	private static final String TAG = "BoolPreference";

	public BoolPreference(String key, Boolean defaultValue) {
		super(key, defaultValue);
		init();
	}

	@Override
	protected Boolean load() {
		boolean result;
		try {
			if (getPrefs().contains(getKey())) {
				result = getPrefs().getBoolean(getKey(), false);
			} else {
				result = getDefault();
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			result = getDefault();
		}
		return result;
	}

	@Override
	protected void save() {
		getPrefs().edit().putBoolean(getKey(), get()).apply();
	}
}
