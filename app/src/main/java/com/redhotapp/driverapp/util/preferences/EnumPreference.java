package com.redhotapp.driverapp.util.preferences;


import android.util.Log;

public class EnumPreference<T extends Enum> extends BasePreference<T> {
	private static final String TAG = "EnumPreference";

	public EnumPreference(String key, T defaultValue) {
		super(key, defaultValue);
		init();
	}

	@Override
	protected T load() {
		T result;
		try {
			String strResult = getPrefs().getString(getKey(), getDefault().name());
			result = (T) Enum.valueOf(getDefault().getClass(), strResult);
			result = result == null ? getDefault() : result;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			result = getDefault();
		}
		if (result == null) {
			result = getDefault();
		}
		return result;
	}

	@Override
	protected void save() {
		getPrefs().edit().putString(getKey(), get().name()).apply();
	}
}
