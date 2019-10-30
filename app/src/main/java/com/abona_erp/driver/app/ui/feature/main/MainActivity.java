package com.abona_erp.driver.app.ui.feature.main;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.AppExecutors;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.repository.NotifyRepository;
import com.abona_erp.driver.app.ui.base.BaseActivity;
import com.abona_erp.driver.app.ui.event.BaseEvent;
import com.abona_erp.driver.app.ui.event.MapEvent;
import com.abona_erp.driver.app.ui.event.TaskDetailEvent;
import com.abona_erp.driver.app.ui.feature.main.steps.ActivityStep;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.ui.widget.badges.Badge;
import com.abona_erp.driver.app.ui.widget.badges.BadgeSpan;
import com.abona_erp.driver.app.ui.widget.badges.BadgeType;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.greenrobot.eventbus.Subscribe;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, StepperFormListener {
  
  private Handler mHandler;
  
  private TabLayout tabLayout;
  private TabsPagerAdapter tabsPagerAdapter;
  private TabLayout tab_bottom_nav;
  
  private MapView mapView;
  private GoogleMap gmap;
  
  private NotifyDao notifyDao;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    initComponent();
    
    mHandler = new Handler();
    notifyDao = NotifyRepository.getNotifyDatabase(this).notifyDao();
  
    tabsPagerAdapter = new TabsPagerAdapter(this,
      getSupportFragmentManager());
  
    ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
    viewPager.setAdapter(tabsPagerAdapter);
  
    tabLayout = findViewById(R.id.tabLayout);
    tabLayout.setupWithViewPager(viewPager);
    
    // Setting custom layout here.
    for (int i = 0; i < tabLayout.getTabCount(); ++i) {
      TabLayout.Tab tab = tabLayout.getTabAt(i);
      
      // we set no badge (null) on screen creation.
      tab.setCustomView(tabsPagerAdapter.getTabView(i, null));
    }
    
    FirebaseInstanceId.getInstance().getInstanceId()
      .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
        @Override
        public void onComplete(@NonNull Task<InstanceIdResult> task) {
          if (!task.isSuccessful()) {
            Log.d("TEST", "Firebase getInstanceId failed " + task.getException());
            return;
          }
        
          // Get new Instance ID token:
          String token = task.getResult().getToken();
          Log.d("TEST","Firebase registration Token=" + token);
        }
      });
    
    registerCloseTaskDetailView();
    //registerStartActivity();
    
    notifyDao.getNotificationCount().observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        if (integer.intValue() <= 0) {
          ((AsapTextView) findViewById(R.id.badge_notification)).setVisibility(View.GONE);
          ((AppCompatImageButton) findViewById(R.id.badge_notification_icon))
            .setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications_outline));
        } else {
          ((AsapTextView) findViewById(R.id.badge_notification)).setVisibility(View.VISIBLE);
          ((AppCompatImageButton) findViewById(R.id.badge_notification_icon))
            .setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications));
          
          if (integer.intValue() <= 99) {
            ((AsapTextView) findViewById(R.id.badge_notification)).setText(String.valueOf(integer.intValue()));
          } else {
            ((AsapTextView) findViewById(R.id.badge_notification)).setText("99+");
          }
        }
      }
    });
    
    notifyDao.getPendingTaskCount().observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        
        if (integer.intValue() <= 0) {
          setTabBadge(1, null);
        } else {
          BadgeSpan badgeSpan = getBadgeSpanByType(BadgeType.BRIGHT);
          Badge badge = new Badge(integer.intValue(), badgeSpan);
          setTabBadge(1, badge);
        }
      }
    });
    
    notifyDao.getRunningTaskCount().observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        if (integer.intValue() <= 0) {
          setTabBadge(0, null);
        } else {
          BadgeSpan badgeSpan = getBadgeSpanByType(BadgeType.BRIGHT);
          Badge badge = new Badge(integer.intValue(), badgeSpan);
          setTabBadge(0, badge);
        }
      }
    });
    
    notifyDao.getCMRTaskCount().observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        if (integer.intValue() <= 0) {
          setTabBadge(2, null);
        } else {
          BadgeSpan badgeSpan = getBadgeSpanByType(BadgeType.BRIGHT);
          Badge badge = new Badge(integer.intValue(), badgeSpan);
          setTabBadge(2, badge);
        }
      }
    });
    
    notifyDao.getCompletedTaskCount().observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        if (integer.intValue() <= 0) {
          setTabBadge(3, null);
        } else {
          BadgeSpan badgeSpan = getBadgeSpanByType(BadgeType.BRIGHT);
          Badge badge = new Badge(integer.intValue(), badgeSpan);
          setTabBadge(3, badge);
        }
      }
    });
    
    mapView = findViewById(R.id.map);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(this);
  }
  
  private void initComponent() {
  
    tab_bottom_nav = (TabLayout) findViewById(R.id.tab_bottom_navigation);
    tab_bottom_nav.addTab(tab_bottom_nav.newTab().setIcon(R.drawable.ic_home), 0);
    tab_bottom_nav.addTab(tab_bottom_nav.newTab().setIcon(R.drawable.ic_location),1 );
    tab_bottom_nav.addTab(tab_bottom_nav.newTab().setIcon(R.drawable.ic_add_box), 2);
    tab_bottom_nav.addTab(tab_bottom_nav.newTab().setIcon(R.drawable.ic_danger), 3);
    tab_bottom_nav.addTab(tab_bottom_nav.newTab().setIcon(R.drawable.ic_info), 4);
    
    // set icon color pre-selected:
    tab_bottom_nav.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.clrAbona),
      PorterDuff.Mode.SRC_IN);
    tab_bottom_nav.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.grey_60),
      PorterDuff.Mode.SRC_IN);
    tab_bottom_nav.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.grey_60),
      PorterDuff.Mode.SRC_IN);
    tab_bottom_nav.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.grey_60),
      PorterDuff.Mode.SRC_IN);
    tab_bottom_nav.getTabAt(4).getIcon().setColorFilter(getResources().getColor(R.color.grey_60),
      PorterDuff.Mode.SRC_IN);
    
    tab_bottom_nav.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        tab.getIcon().setColorFilter(getResources().getColor(R.color.clrAbona),
          PorterDuff.Mode.SRC_IN);
        
        switch (tab.getPosition()) {
          case 0:
            break;
          case 1:
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
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
  }
  
  @Subscribe
  public void onMessageEvent(MapEvent event) {
    showMapView();
    
    ((AppCompatImageButton) findViewById(R.id.map_view_close))
      .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          hideMapView();
        }
      });
  }
  
  @Subscribe
  public void onMessageEvent(TaskDetailEvent event) {
    Notify notify = event.getNotify();
    notify.setRead(true);
    AppExecutors.getInstance().diskIO().execute(new Runnable() {
      @Override
      public void run() {
        notifyDao.updateNotify(notify);
      }
    });
    
    showTaskDetailView();
    
    ((AppCompatButton) findViewById(R.id.task_detail_view_start_activity))
      .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          notify.setStatus(50);
          AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
              notifyDao.updateNotify(notify);
            }
          });
          hideTaskDetailView();
        }
      });
  
    ActivityStep activityStep1 = new ActivityStep("Driving to loading");
    ActivityStep activityStep2 = new ActivityStep("Waiting for loading");
    ActivityStep activityStep3 = new ActivityStep("Loading complete");
  
    VerticalStepperFormView stepperFormView = (VerticalStepperFormView)findViewById(R.id.stepper_form);
    stepperFormView.setup(this, activityStep1, activityStep2, activityStep3)
      .includeConfirmationStep(false)
      .displayBottomNavigation(false)
      .init();
  }
  
  public void setTabBadge(int tabIndex, Badge badge) {
    TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
    tab.setCustomView(null);
    tab.setCustomView(tabsPagerAdapter.getTabView(tabIndex, badge));
  }
  
  public Integer getTabBadge(int index) {
    return tabsPagerAdapter.getBadgeValue(index);
  }
  
  public BadgeSpan getBadgeSpanByType(BadgeType badgeType) {
    return tabsPagerAdapter.getBadgeSpanByType(badgeType);
  }
  
  @Override
  public void onResume() {
    mapView.onResume();
    super.onResume();
  }
  
  @Override
  protected void onStart() {
    super.onStart();
    App.eventBus.register(this);
  }
  
  @Override
  protected void onStop() {
    super.onStop();
    App.eventBus.unregister(this);
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }
  
  @Override
  public void onMapReady(GoogleMap map) {
  }
  
  public void removeStickyEvent(final Class<?> eventType) {
    final int delayMillis = 100;
    mHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        App.eventBus.removeStickyEvent(eventType);
      }
    }, delayMillis);
  }
  
  protected <T extends BaseEvent> void removeStickyEvent(final T event) {
    final int delayMillis = 100;
    mHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        App.eventBus.removeStickyEvent(event);
      }
    }, delayMillis);
  }
  
  @Override
  public void onCompletedForm() {
  
  }
  
  @Override
  public void onCancelledForm() {
  
  }
}
