package com.abona_erp.driver.app.ui.feature.main.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.feature.main.TabsPagerAdapter;
import com.abona_erp.driver.app.ui.widget.badges.Badge;
import com.abona_erp.driver.app.ui.widget.badges.BadgeSpan;
import com.abona_erp.driver.app.ui.widget.badges.BadgeType;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MainFragment extends Fragment {
  
  private TabLayout mMainTab;
  private TabLayout mMainBottomTab;
  private ViewPager mViewPager;
  private TabsPagerAdapter mPagerAdapter;
  
  private int mRunningCount;
  private int mCMRCount;
  private int mCompletedCount;
  private int mRowTaskCount;
  
  private MainFragmentViewModel mainViewModel;
  
  public MainFragment() {
    // Required empty public constructor.
  }
  
  public static MainFragment newInstance() {
    return new MainFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.activity_tab_layout, container, false);
    initComponents(root);
    return root;
  }
  
  @Override
  public void onResume() {
    super.onResume();
  }
  
  private void initComponents(@NonNull View root) {
  
    mPagerAdapter = new TabsPagerAdapter(getContext(),
      getChildFragmentManager());
    
    mViewPager = (ViewPager) root.findViewById(R.id.view_pager);
    mViewPager.setAdapter(mPagerAdapter);
    
    mMainTab = (TabLayout)root.findViewById(R.id.main_tab);
    mMainTab.setupWithViewPager(mViewPager);
    
    for (int i = 0; i < mMainTab.getTabCount(); ++i) {
      TabLayout.Tab tab = mMainTab.getTabAt(i);
      
      // we set no badge (null) on screen creation.
      tab.setCustomView(mPagerAdapter.getTabView(i, null));
    }
    
    mMainBottomTab = (TabLayout)root.findViewById(R.id.main_bottom_tab);
    mMainBottomTab.addTab(mMainBottomTab.newTab().setIcon(R.drawable.ic_home), 0);
    mMainBottomTab.addTab(mMainBottomTab.newTab().setIcon(R.drawable.ic_info_24px), 1);
    
    // set icon color pre-selected:
    mMainBottomTab.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.clrAbona), PorterDuff.Mode.SRC_IN);
    mMainBottomTab.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_IN);
    
    mMainBottomTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        tab.getIcon().setColorFilter(getResources().getColor(R.color.clrAbona),
          PorterDuff.Mode.SRC_IN);
        
        switch (tab.getPosition()) {
          case 0:
            break;
          case 1:
            //App.eventBus.post(new SoftwareAboutEvent());
            App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_ABOUT), null));
            break;
        }
      }
  
      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
        tab.getIcon().setColorFilter(getResources().getColor(R.color.grey_60),
          PorterDuff.Mode.SRC_IN);
      }
  
      @Override
      public void onTabReselected(TabLayout.Tab tab) {
    
      }
    });
  
    mainViewModel = ViewModelProviders.of(this)
      .get(MainFragmentViewModel.class);
    
    mainViewModel.getAllPendingNotifications()
      .observe(this, new Observer<List<Notify>>() {
      
      @Override
      public void onChanged(List<Notify> notifyList) {
        TabLayout.Tab tab = mMainTab.getTabAt(1);
        tab.setCustomView(null);
        if (notifyList.size() > 0) {
          BadgeSpan badgeSpan = getBadgeSpanByType(BadgeType.BRIGHT);
          Badge badge = new Badge(notifyList.size(), badgeSpan);
          setTabBadge(1, badge);
        } else {
          setTabBadge(1, null);
        }
      }
    });
    
    mainViewModel.getAllRunningNotifications()
      .observe(this, new Observer<List<Notify>>() {
      @Override
      public void onChanged(List<Notify> notifyList) {
        mRunningCount = notifyList.size();
        //App.eventBus.post(new TaskStatusEvent(calculateTaskStatusPercentage()));
        TabLayout.Tab tab = mMainTab.getTabAt(0);
        tab.setCustomView(null);
        if (notifyList.size() > 0) {
          BadgeSpan badgeSpan = getBadgeSpanByType(BadgeType.BRIGHT);
          Badge badge = new Badge(notifyList.size(), badgeSpan);
          setTabBadge(0, badge);
        } else {
          setTabBadge(0, null);
        }
      }
    });
    
    mainViewModel.getAllCMRNotifications().observe(this,
      new Observer<List<Notify>>() {
      @Override
      public void onChanged(List<Notify> notifyList) {
        mCMRCount = notifyList.size();
        //App.eventBus.post(new TaskStatusEvent(calculateTaskStatusPercentage()));
        TabLayout.Tab tab = mMainTab.getTabAt(2);
        tab.setCustomView(null);
        if (notifyList.size() > 0) {
          BadgeSpan badgeSpan = getBadgeSpanByType(BadgeType.BRIGHT);
          Badge badge = new Badge(notifyList.size(), badgeSpan);
          setTabBadge(2, badge);
        } else {
          setTabBadge(2, null);
        }
      }
    });
    
    mainViewModel.getAllCompletedNotifications().observe(this,
      new Observer<List<Notify>>() {
        @Override
        public void onChanged(List<Notify> notifyList) {
          mCompletedCount = notifyList.size();
          /*
          if (mCompletedCount > 0) {
            App.eventBus.post(new TaskStatusEvent(calculateTaskStatusPercentage()));
          } else {
            App.eventBus.post(new TaskStatusEvent(0));
          }*/
          TabLayout.Tab tab = mMainTab.getTabAt(3);
          tab.setCustomView(null);
          if (notifyList.size() > 0) {
            BadgeSpan badgeSpan = getBadgeSpanByType(BadgeType.BRIGHT);
            Badge badge = new Badge(notifyList.size(), badgeSpan);
            setTabBadge(3, badge);
          } else {
            setTabBadge(3, null);
          }
        }
      });
  /*
    mainViewModel.getRowCount().observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        mRowTaskCount = integer.intValue();
        App.eventBus.post(new TaskStatusEvent(calculateTaskStatusPercentage()));
      }
    });*/
  }
  /*
  private int calculateTaskStatusPercentage() {
    if (mRowTaskCount == 0) {
      return 0;
    }
    else if (mRowTaskCount == mCompletedCount) {
      return 100;
    } else {
      float percentage = ((100.0f / mRowTaskCount) * ((mRunningCount * 0.5f) + (mCMRCount * 0.9f) + mCompletedCount));
      return (int)Math.round(percentage);
    }
  }
*/
  private void setTabBadge(int tabIndex, Badge badge) {
    TabLayout.Tab tab = mMainTab.getTabAt(tabIndex);
    tab.setCustomView(mPagerAdapter.getTabView(tabIndex, badge));
  }
  
  private Integer getTabBadge(int index) {
    return mPagerAdapter.getBadgeValue(index);
  }
  
  private BadgeSpan getBadgeSpanByType(BadgeType badgeType) {
    return mPagerAdapter.getBadgeSpanByType(badgeType);
  }
}
