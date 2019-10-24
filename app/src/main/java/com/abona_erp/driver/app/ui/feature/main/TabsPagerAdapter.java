package com.abona_erp.driver.app.ui.feature.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.abona_erp.driver.app.R;

public class TabsPagerAdapter extends FragmentPagerAdapter {
  
  private final int NUM_PAGES = 4;
  
  @StringRes
  private static final int[] TAB_TITLES = new int[] {
    R.string.tab_text_running,
    R.string.tab_text_pending,
    R.string.tab_text_cmr,
    R.string.tab_text_completed
  };
  
  private final Context mContext;
  
  public TabsPagerAdapter(Context context, FragmentManager fm) {
    super(fm);
    mContext = context;
  }
  
  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case 0:
        return RunningFragment.newInstance();
      case 1:
        return PendingFragment.newInstance();
      case 2:
        return CompletedFragment.newInstance();
      case 3:
        return CMRFragment.newInstance();
      default:
        return null;
    }
  }
  
  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    return mContext.getResources().getString(TAB_TITLES[position]);
  }
  
  @Override
  public int getCount() {
    return NUM_PAGES;
  }
}
