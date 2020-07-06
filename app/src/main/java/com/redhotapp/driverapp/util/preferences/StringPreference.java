package com.redhotapp.driverapp.util.preferences;

import android.text.TextUtils;
import android.util.Log;


import com.redhotapp.driverapp.util.CommonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringPreference extends BasePreference<String> {
	private static final String TAG = "StringPreference";
	private String[] mAllowedValues;

	public StringPreference(String key, String defaultValue) {
		this(key, defaultValue, null);
	}

	public StringPreference(String key, String defaultValue, String[] allowedValues) {
		super(key, defaultValue);
		mAllowedValues = allowedValues;
		init();
	}

	@Override
	protected String load() {
		String result;
		try {
			result = getPrefs().getString(getKey(), getDefault());
			if (mAllowedValues != null && !CommonUtils.arrayContains(result, mAllowedValues)) {
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
		getPrefs().edit().putString(getKey(), get()).apply();
	}


	// empty elements "" and ";" not allowed
	public void setAsStringList(List<String> value) {
		set(TextUtils.join(";", value));
	}

	public List<String> getAsStringList() {
		final String value = get();
		if (value == null) {
			return null;
		} else {
			final List<String> result = new ArrayList<>(Arrays.asList(get().split(";")));
			// TODO remove
			result.remove("");
			return result;
		}
	}

	public void setAsBooleanList(List<Boolean> value) {
		String result = "";
		for (int i = 0; i < value.size(); i++) {
			result += value.get(i) ? "1" : "0";
		}
		set(result);
	}

	public List<Boolean> getAsBooleanList() {
		final String value = get();
		if (value == null) {
			return null;
		} else {
			final List<Boolean> result = new ArrayList<>(value.length());
			for (int i = 0; i < value.length(); i++) {
				result.add(value.charAt(i) == '1');
			}
			return result;
		}
	}
}
