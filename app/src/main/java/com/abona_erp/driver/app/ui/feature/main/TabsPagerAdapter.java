package com.abona_erp.driver.app.ui.feature.main;

import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.ui.widget.badges.Badge;
import com.abona_erp.driver.app.ui.widget.badges.BadgeSpan;
import com.abona_erp.driver.app.ui.widget.badges.BadgeType;

import java.util.HashMap;
import java.util.Map;

public class TabsPagerAdapter extends FragmentPagerAdapter {
  
  private static final String BADGE_MARGIN = " ";
  private final int pageCount = 4;
  
  @StringRes
  private static final int[] tabTitles = new int[] {
    R.string.tab_text_running,
    R.string.tab_text_pending,
    R.string.tab_text_cmr,
    R.string.tab_text_completed
  };
  
  private final Context mContext;
  private Map<Integer, Integer> badgeValues;
  
  public TabsPagerAdapter(Context context, FragmentManager fm) {
    super(fm);
    mContext = context;
    badgeValues = new HashMap<>();
  }
  
  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case 0:
        return RunningFragment.newInstance();
      case 1:
        return PendingFragment.newInstance();
      case 2:
        return CMRFragment.newInstance();
      case 3:
        return CompletedFragment.newInstance();
      default:
        throw new IllegalArgumentException();
    }
  }
  
  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    return mContext.getResources().getString(tabTitles[position]);
  }
  
  public View getTabView(int position, Badge badge) {
    View v = LayoutInflater.from(mContext).inflate(R.layout.badged_tab, null);
    AsapTextView tabText = (AsapTextView) v.findViewById(R.id.tab_text);
    
    if (badge != null && badge.isActual()) {
      String badgeText = badge.getBadgeText();
      String tabTitle = mContext.getResources().getString(tabTitles[position]);
      tabText.setText(tabTitle + badgeText, AsapTextView.BufferType.SPANNABLE);
  
      Spannable spannable = (Spannable) tabText.getText();
      spannable.setSpan(badge.getSpan(), tabTitle.length(), tabTitle.length() + badgeText.length(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
  
      badgeValues.put(position, badge.getNumber());
    } else {
      tabText.setText(mContext.getResources().getString(tabTitles[position]));
      badgeValues.put(position, 0);
    }
    
    return v;
  }
  
  public BadgeSpan getBadgeSpanByType(BadgeType badgeType) {
    if (BadgeType.BRIGHT.equals(badgeType)) {
      int colorBadge = ContextCompat.getColor(mContext, R.color.clrFont);
      int colorText = ContextCompat.getColor(mContext, R.color.clrWhite);
      return new BadgeSpan(colorBadge, colorText, 25);
    }
    else if (BadgeType.FAINT.equals(badgeType)) {
      int colorBadge = ContextCompat.getColor(mContext, R.color.clrWhite);
      int colorText = ContextCompat.getColor(mContext, R.color.colorPrimary);
      return new BadgeSpan(colorBadge, colorText, 25);
    }
    
    return null;
  }
  
  public Integer getBadgeValue(int index) {
    return badgeValues.get(index);
  }
  
  @Override
  public int getCount() {
    return this.pageCount;
  }
}
