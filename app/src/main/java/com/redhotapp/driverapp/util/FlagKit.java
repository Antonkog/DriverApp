package com.redhotapp.driverapp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatImageView;

import com.redhotapp.driverapp.R;

import java.util.Locale;

@SuppressLint("AppCompatCustomView")
public class FlagKit extends AppCompatImageView {
  
  private static final String TAG = FlagKit.class.getCanonicalName();
  
  private String mCountryCode;
  
  public FlagKit(Context context) {
    super(context);
    init(null);
  }
  
  public FlagKit(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }
  
  public FlagKit(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }
  
  private void init(AttributeSet attrs) {
    super.setScaleType(ScaleType.CENTER_CROP);
    super.setAdjustViewBounds(true);
    
    if (isInEditMode())
      return;
    
    if (attrs != null) {
      TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.FlagKit, 0, 0);
      try {
        String countryCode = ta.getString(R.styleable.FlagKit_countryCode);
        if (countryCode != null && !countryCode.isEmpty()) {
          setCountryCode(countryCode);
        } else {
          defaultLocal();
        }
      } finally {
        ta.recycle();
      }
    }
  }
  
  public void defaultLocal() {
    setCountryCode(Locale.getDefault().getCountry());
    Log.d(TAG, "defaultLocal " + Locale.getDefault().getCountry());
  }
  
  public String getCountryCode() {
    return mCountryCode;
  }
  
  public void setCountryCode(String countryCode) {
    mCountryCode = countryCode != null && !countryCode.isEmpty() ? countryCode.toLowerCase() : "";
    if (!countryCode.equals(this.mCountryCode)) {
      mCountryCode = countryCode;
      updateDrawableWithCountryCode();
    }
  }
  
  public void setCountryCode(Locale locale) {
    setCountryCode(locale.getCountry());
  }
  
  private void updateDrawableWithCountryCode() {
    Log.d(TAG, "*********************** " + mCountryCode);
    if (mCountryCode.isEmpty()) {
      setImageResource(0);
    } else {
      Resources resources = getResources();
      final String resName = "flag_" + mCountryCode.toLowerCase();
      final int resourceId = resources.getIdentifier(resName, "drawable",
        getContext().getPackageName());
      if (resourceId == 0) {
        Log.w(TAG, "CountryCode is wrong");
      }
      setImageResource(resourceId);
    }
  }
}
