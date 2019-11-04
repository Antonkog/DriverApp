package com.abona_erp.driver.app.ui.event;

import androidx.annotation.NonNull;

public class BadgeCountEvent implements BaseEvent {

  private int mTabIndex;
  private int mBadgeCount;

  public BadgeCountEvent(@NonNull int tabIndex, @NonNull int badgeCount) {
    mTabIndex = tabIndex;
    mBadgeCount = badgeCount;
  }

  public int getTabIndex() {
    return mTabIndex;
  }

  public void setTabIndex(int tabIndex) {
    mTabIndex = tabIndex;
  }

  public int getBadgeCount() {
    return mBadgeCount;
  }

  public void setBadgeCount(int badgeCount) {
    mBadgeCount = badgeCount;
  }
}
