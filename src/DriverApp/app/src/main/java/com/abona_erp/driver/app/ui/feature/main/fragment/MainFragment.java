package com.abona_erp.driver.app.ui.feature.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.ui.feature.main.TabsPagerAdapter;
import com.abona_erp.driver.app.ui.widget.badges.Badge;
import com.abona_erp.driver.app.ui.widget.badges.BadgeSpan;
import com.abona_erp.driver.app.ui.widget.badges.BadgeType;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MainFragment extends Fragment {
  
  private static final int TAB_INDEX_PENDING   = 0;
  private static final int TAB_INDEX_RUNNING   = 1;
  private static final int TAB_INDEX_COMPLETED = 2;
  
  private TabLayout mMainTab;
  private ViewPager mViewPager;
  private TabsPagerAdapter mPagerAdapter;
  
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
    
    mainViewModel = ViewModelProviders.of(this)
      .get(MainFragmentViewModel.class);
    
    mainViewModel.getAllPendingNotifications()
      .observe(getViewLifecycleOwner(), new Observer<List<Notify>>() {
      
      @Override
      public void onChanged(List<Notify> notifyList) {
        TabLayout.Tab tab = mMainTab.getTabAt(TAB_INDEX_PENDING);
        tab.setCustomView(null);
        if (notifyList.size() > 0) {
          BadgeSpan badgeSpan = getBadgeSpanByType(BadgeType.BRIGHT);
          Badge badge = new Badge(notifyList.size(), badgeSpan);
          setTabBadge(TAB_INDEX_PENDING, badge);
        } else {
          setTabBadge(TAB_INDEX_PENDING, null);
        }
      }
    });
    
    mainViewModel.getAllRunningNotifications()
      .observe(getViewLifecycleOwner(), new Observer<List<Notify>>() {
      @Override
      public void onChanged(List<Notify> notifyList) {
        TabLayout.Tab tab = mMainTab.getTabAt(TAB_INDEX_RUNNING);
        tab.setCustomView(null);
        if (notifyList.size() > 0) {
          BadgeSpan badgeSpan = getBadgeSpanByType(BadgeType.BRIGHT);
          Badge badge = new Badge(notifyList.size(), badgeSpan);
          setTabBadge(TAB_INDEX_RUNNING, badge);
        } else {
          setTabBadge(TAB_INDEX_RUNNING, null);
        }
      }
    });

    mainViewModel.getAllCompletedNotifications().observe(getViewLifecycleOwner(),
      new Observer<List<Notify>>() {
        @Override
        public void onChanged(List<Notify> notifyList) {
          TabLayout.Tab tab = mMainTab.getTabAt(TAB_INDEX_COMPLETED);
          tab.setCustomView(null);
          if (notifyList.size() > 0) {
            BadgeSpan badgeSpan = getBadgeSpanByType(BadgeType.BRIGHT);
            Badge badge = new Badge(notifyList.size(), badgeSpan);
            setTabBadge(TAB_INDEX_COMPLETED, badge);
          } else {
            setTabBadge(TAB_INDEX_COMPLETED, null);
          }
        }
      });
  }

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
