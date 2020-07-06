package com.redhotapp.driverapp.util.preferences;


import android.util.Log;

public class IntPreference extends BasePreference<Integer> {
	private static final String TAG = "IntPreference";
	private Integer mMinValue;
	private Integer mMaxValue;

	public IntPreference(String key, Integer defaultValue) {
		super(key, defaultValue);
		init();
	}

	public IntPreference(String key, Integer defaultValue, int minValue, int maxValue) {
		super(key, defaultValue);
		mMinValue = minValue;
		mMaxValue = maxValue;
		init();
	}

	@Override
	protected Integer load() {
		Integer result;
		try {
			if (getPrefs().contains(getKey())) {
				result = getPrefs().getInt(getKey(), -1);
			} else {
				result = getDefault();
			}
			if ((mMinValue != null && result < mMinValue) || (mMaxValue != null && result > mMaxValue)) {
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
		getPrefs().edit().putInt(getKey(), get()).apply();
	}

	public Integer getMin() {
		return mMinValue;
	}

	public Integer getMax() {
		return mMaxValue;
	}
}
