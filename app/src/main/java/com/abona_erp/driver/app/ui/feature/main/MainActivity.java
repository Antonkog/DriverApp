package com.abona_erp.driver.app.ui.feature.main;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.ActivityStep;
import com.abona_erp.driver.app.data.model.Data;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.ui.base.BaseActivity;
import com.abona_erp.driver.app.ui.event.BadgeCountEvent;
import com.abona_erp.driver.app.ui.event.BaseEvent;
import com.abona_erp.driver.app.ui.event.MapEvent;
import com.abona_erp.driver.app.ui.event.TaskDetailEvent;
import com.abona_erp.driver.app.ui.feature.main.adapter.ActivityStepAdapter;
import com.abona_erp.driver.app.ui.feature.main.adapter.LastActivityAdapter;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.ui.widget.badges.Badge;
import com.abona_erp.driver.app.ui.widget.badges.BadgeSpan;
import com.abona_erp.driver.app.ui.widget.badges.BadgeType;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DoubleJsonDeserializer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.tree.rh.ctlib.CT;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import az.plainpie.PieView;

public class MainActivity extends BaseActivity implements OnMapReadyCallback {
  
  private Handler mHandler;
  
  private TabLayout tabLayout;
  private TabsPagerAdapter tabsPagerAdapter;
  private TabLayout tab_bottom_nav;
  
  private MapView mapView;
  private GoogleMap gmap;

  private Gson mGson;
  private Data mData;
  private List<ActivityStep> mActivityList;
  
  private RecyclerView lvActivityStep;
  private RecyclerView lvLastActivity;

  private PieView mMainPieView;

  // VIEW MODEL:
  private MainViewModel mMainViewModel;

  // MAP VIEW:
  MarkerOptions mMarkerOptions;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    initComponent();

    mMarkerOptions = new MarkerOptions();

    mMainPieView = (PieView)findViewById(R.id.mainPieView);
    mMainPieView.setPercentageBackgroundColor(getResources().getColor(R.color.clrAbona));
    mMainPieView.setInnerBackgroundColor(getResources().getColor(R.color.clrFont));

    JsonDeserializer deserializer = new DoubleJsonDeserializer();
    mGson = new GsonBuilder()
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
      .registerTypeAdapter(double.class, deserializer)
      .registerTypeAdapter(Double.class, deserializer)
      .create();

    mActivityList = new ArrayList<>();

    lvLastActivity = (RecyclerView)findViewById(R.id.lv_last_activity);
    LinearLayoutManager recyclerLayoutManager =
      new LinearLayoutManager(getApplicationContext(),
        RecyclerView.VERTICAL, false);
    lvLastActivity.setLayoutManager(recyclerLayoutManager);
    LastActivityAdapter lastActivityAdapter = new LastActivityAdapter(getApplicationContext());
    lvLastActivity.setAdapter(lastActivityAdapter);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // GET A NEW OR EXISTING VIEWMODEL FROM THE VIEWMODELPROVIDER.
    //
    mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    mHandler = new Handler();
    //notifyDao = DriverRepository.getNotifyDatabase(this).notifyDao();
  
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

    mapView = findViewById(R.id.map);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(this);

    mMainViewModel.getNotReadNotificationCount().observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        if (integer == null)
          return;
        int value = integer.intValue();
        if (value <= 0) {
          ((AsapTextView) findViewById(R.id.badge_notification)).setVisibility(View.GONE);
          ((AppCompatImageButton) findViewById(R.id.badge_notification_icon))
            .setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications_outline));
        } else {
          ((AsapTextView) findViewById(R.id.badge_notification)).setVisibility(View.VISIBLE);
          ((AppCompatImageButton) findViewById(R.id.badge_notification_icon))
            .setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications));

          if (value <= 99) {
            ((AsapTextView) findViewById(R.id.badge_notification)).setText(String.valueOf(integer.intValue()));
          } else {
            ((AsapTextView) findViewById(R.id.badge_notification)).setText("99+");
          }
        }
      }
    });

    mMainViewModel.getAllLastActivityItems().observe(this, new Observer<List<LastActivity>>() {
      @Override
      public void onChanged(List<LastActivity> lastActivities) {
        lastActivityAdapter.setLastActivityItems(lastActivities);
      }
    });

    mMainViewModel.getAllPendingNotifications().observe(this, new Observer<List<Notify>>() {
      @Override
      public void onChanged(List<Notify> notifies) {
        if (notifies.size() > 0) {
          App.eventBus.post(new BadgeCountEvent(1, notifies.size()));
        } else {
          App.eventBus.post(new BadgeCountEvent(1, 0));
        }
      }
    });

    mMainViewModel.getAllRunningNotifications().observe(this, new Observer<List<Notify>>() {
      @Override
      public void onChanged(List<Notify> notifies) {
        if (notifies.size() > 0) {
          App.eventBus.post(new BadgeCountEvent(0, notifies.size()));
        } else {
          App.eventBus.post(new BadgeCountEvent(0, 0));
        }
      }
    });

    mMainViewModel.getAllCMRNotifications().observe(this, new Observer<List<Notify>>() {
      @Override
      public void onChanged(List<Notify> notifies) {
        if (notifies.size() > 0) {
          App.eventBus.post(new BadgeCountEvent(2, notifies.size()));
        } else {
          App.eventBus.post(new BadgeCountEvent(2, 0));
        }
      }
    });

    mMainViewModel.getAllCompletedNotifications().observe(this, new Observer<List<Notify>>() {
      @Override
      public void onChanged(List<Notify> notifies) {
        if (notifies.size() > 0) {
          App.eventBus.post(new BadgeCountEvent(3, notifies.size()));
        } else {
          App.eventBus.post(new BadgeCountEvent(3, 0));
        }
      }
    });
  }
  
  private void initComponent() {
  
    tab_bottom_nav = (TabLayout) findViewById(R.id.tab_bottom_navigation);
    tab_bottom_nav.addTab(tab_bottom_nav.newTab().setIcon(R.drawable.ic_home), 0);
    //tab_bottom_nav.addTab(tab_bottom_nav.newTab().setIcon(R.drawable.ic_location),1 );
    //tab_bottom_nav.addTab(tab_bottom_nav.newTab().setIcon(R.drawable.ic_add_box), 2);
    //tab_bottom_nav.addTab(tab_bottom_nav.newTab().setIcon(R.drawable.ic_danger), 3);
    tab_bottom_nav.addTab(tab_bottom_nav.newTab().setIcon(R.drawable.ic_info), 1);
    
    // set icon color pre-selected:
    tab_bottom_nav.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.clrAbona),
      PorterDuff.Mode.SRC_IN);
    //tab_bottom_nav.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.grey_60),
    //  PorterDuff.Mode.SRC_IN);
    //tab_bottom_nav.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.grey_60),
    //  PorterDuff.Mode.SRC_IN);
    //tab_bottom_nav.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.grey_60),
    //  PorterDuff.Mode.SRC_IN);
    tab_bottom_nav.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.grey_60),
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
  public void onMessageEvent(BadgeCountEvent event) {
    if (event.getBadgeCount() <= 0) {
      setTabBadge(event.getTabIndex(), null);
    } else {
      BadgeSpan badgeSpan = getBadgeSpanByType(BadgeType.BRIGHT);
      Badge badge = new Badge(event.getBadgeCount(), badgeSpan);
      setTabBadge(event.getTabIndex(), badge);
    }
  }
  
  @Subscribe
  public void onMessageEvent(MapEvent event) {
    showMapView();

    Notify notify = event.getNotify();
    if (mData != null) {
      mData = null;
    }
    if (mData == null) {
      mData = new Data();
      String jsonText = notify.getData();
      mData = mGson.fromJson(jsonText, Data.class);
    }
    
    ((AppCompatImageButton) findViewById(R.id.map_view_close))
      .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          hideMapView();
        }
      });

    gmap.clear();
    if (mData.getTaskItem().getAddress().getLongitude() != null
      && mData.getTaskItem().getAddress().getLatitude() != null)
    {
      LatLng marker = new LatLng(
        mData.getTaskItem().getAddress().getLongitude().doubleValue(),
        mData.getTaskItem().getAddress().getLatitude().doubleValue()
      );

      mMarkerOptions.position(marker).title(mData.getTaskItem().getKundenName());
      gmap.addMarker(mMarkerOptions);

      gmap.moveCamera(CameraUpdateFactory.newLatLng(marker));
      gmap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }
  }
  
  @Subscribe
  public void onMessageEvent(TaskDetailEvent event) {

    Notify notify = event.getNotify();
    if (!notify.getRead()) {
      notify.setRead(true);
      mMainViewModel.update(notify);
    }

    showTaskDetailView();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    if (mData != null) {
      mData = null;
    }
    mData = new Data();
    String jsonText = notify.getData();
    mData = mGson.fromJson(jsonText, Data.class);

    ActivityStepAdapter adapter = new ActivityStepAdapter(getApplicationContext());

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ((AppCompatButton)findViewById(R.id.task_detail_view_start_activity))
      .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

          if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
            mData.getTaskItem().setTaskStatus(TaskStatus.RUNNING);
            String raw = mGson.toJson(mData);
            notify.setStatus(50);
            notify.setData(raw);
            mMainViewModel.update(notify);
            hideTaskDetailView();
          } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
            if (mData.getTaskItem().getActivities().size() > 0) {
              for (int i = 0; i < mData.getTaskItem().getActivities().size(); i++) {
                if (mData.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.FINISHED)) {
                  if (i == mData.getTaskItem().getActivities().size()-1) {
                    notify.setStatus(90);
                    mData.getTaskItem().setTaskStatus(TaskStatus.CMR);
                    notify.setData(mGson.toJson(mData));
                    mMainViewModel.update(notify);
                    hideTaskDetailView();
                  }
                  continue;
                }

                if (mData.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.RUNNING)) {
                  mData.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.FINISHED);
                  notify.setData(mGson.toJson(mData));
                  mMainViewModel.update(notify);

                  mActivityList.clear();
                  if (mData.getTaskItem().getActivities().size() > 0) {
                    for (int j = 0; j < mData.getTaskItem().getActivities().size(); j++) {
                      mActivityList.add(new ActivityStep(mData.getTaskItem().getTaskStatus(), mData.getTaskItem().getActivities().get(j)));
                    }
                  }
                  adapter.setActivityStepItems(mActivityList);

                  if (i == mData.getTaskItem().getActivities().size()-1) {
                    ((AppCompatButton)findViewById(R.id.task_detail_view_start_activity))
                      .setText("FINISHED");
                  }
                  break;
                }

                if (mData.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.PENDING)) {
                  mData.getTaskItem().getActivities().get(i).setStatus(ActivityStatus.RUNNING);
                  notify.setData(mGson.toJson(mData));
                  mMainViewModel.update(notify);

                  mActivityList.clear();
                  if (mData.getTaskItem().getActivities().size() > 0) {
                    for (int j = 0; j < mData.getTaskItem().getActivities().size(); j++) {
                      mActivityList.add(new ActivityStep(mData.getTaskItem().getTaskStatus(), mData.getTaskItem().getActivities().get(j)));
                    }
                  }
                  adapter.setActivityStepItems(mActivityList);
                  break;
                }
              }
            }
          } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
            notify.setStatus(100);
            mData.getTaskItem().setTaskStatus(TaskStatus.FINISHED);
            notify.setData(mGson.toJson(mData));
            mMainViewModel.update(notify);
            hideTaskDetailView();
          } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {

          }
        }
      });

    if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
      ((AppCompatButton)findViewById(R.id.task_detail_view_back_activity))
        .setVisibility(View.GONE);
      ((AppCompatButton)findViewById(R.id.task_detail_view_start_activity))
        .setVisibility(View.VISIBLE);
      ((AppCompatButton)findViewById(R.id.task_detail_view_start_activity))
        .setText("START ACTIVITY");
    } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
      ((AppCompatButton)findViewById(R.id.task_detail_view_back_activity))
        .setVisibility(View.VISIBLE);
      ((AppCompatButton)findViewById(R.id.task_detail_view_start_activity))
        .setVisibility(View.VISIBLE);
      int activityCount = mData.getTaskItem().getActivities().size();
      if (mData.getTaskItem().getActivities().get(activityCount-1).getStatus().equals(ActivityStatus.FINISHED)) {
        ((AppCompatButton)findViewById(R.id.task_detail_view_start_activity))
          .setText("FINISHED");
      } else {
        ((AppCompatButton)findViewById(R.id.task_detail_view_start_activity))
          .setText("NEXT");
      }
    } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
      ((AppCompatButton)findViewById(R.id.task_detail_view_back_activity))
        .setVisibility(View.GONE);
      ((AppCompatButton)findViewById(R.id.task_detail_view_start_activity))
        .setText("CMR FINISHED");
    } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
      ((AppCompatButton)findViewById(R.id.task_detail_view_back_activity))
        .setVisibility(View.GONE);
      ((AppCompatButton)findViewById(R.id.task_detail_view_start_activity))
        .setVisibility(View.GONE);
        //.setText("ACTIVITY FINISHED");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    mActivityList.clear();
    if (mData.getTaskItem().getActivities().size() > 0) {
      for (int i = 0; i < mData.getTaskItem().getActivities().size(); i++) {
        mActivityList.add(new ActivityStep(mData.getTaskItem().getTaskStatus(), mData.getTaskItem().getActivities().get(i)));
      }
    }

    if (mData.getTaskItem().getKundenName() != null) {
      ((AsapTextView)findViewById(R.id.tv_activity_step_customer_name))
        .setText(mData.getTaskItem().getKundenName());
    }
    ((AsapTextView)findViewById(R.id.tv_activity_step_customer_no))
      .setText(String.valueOf(mData.getTaskItem().getKundenNr()));
    ((AsapTextView)findViewById(R.id.tv_activity_step_order_no))
      .setText(String.valueOf(AppUtils.parseOrderNo(mData.getTaskItem().getOrderNo())));
    if (mData.getTaskItem().getReferenceIdCustomer1() != null) {
      ((AsapTextView)findViewById(R.id.tv_activity_step_reference_1))
        .setText(mData.getTaskItem().getReferenceIdCustomer1());
    }
    if (mData.getTaskItem().getReferenceIdCustomer2() != null) {
      ((AsapTextView)findViewById(R.id.tv_activity_step_reference_2))
        .setText(mData.getTaskItem().getReferenceIdCustomer2());
    }
    if (mData.getTaskItem().getDescription() != null) {
      ((AsapTextView)findViewById(R.id.tv_activity_step_desc))
        .setText(mData.getTaskItem().getDescription());
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    lvActivityStep = (RecyclerView)findViewById(R.id.lv_activity_step);

    LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(),
      RecyclerView.VERTICAL, false);
    lvActivityStep.setLayoutManager(lm);
  

    adapter.setActivityStepItems(mActivityList);
    lvActivityStep.setAdapter(adapter);

    ////////////////////////////////////////////////////////////////////////////////////////////////
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

  private static long back_pressed;
  @Override
  public void onBackPressed() {
    if (((LinearLayout)findViewById(R.id.task_detail_view)).getVisibility() == View.VISIBLE) {
      hideTaskDetailView();
      return;
    }
    if (((RelativeLayout)findViewById(R.id.map_layout)).getVisibility() == View.VISIBLE) {
      hideMapView();
      return;
    }
    if (back_pressed + 2000 > System.currentTimeMillis()) {
      super.onBackPressed();
    } else {
      new CT.Builder(getBaseContext(), "Press once again to exit!")
        .image(R.drawable.ic_info)
        .borderWidth(4)
        .backCol(getResources().getColor(R.color.clrFont))
        .textCol(Color.WHITE)
        .borderCol(getResources().getColor(R.color.clrAbona))
        .radius(20, 20, 20, 20)
        .show();
      back_pressed = System.currentTimeMillis();
    }
  }
  
  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();
  }
  
  @Override
  protected void onStart() {
    super.onStart();
    App.eventBus.register(this);
    mapView.onStart();
  }
  
  @Override
  protected void onStop() {
    super.onStop();
    mapView.onPause();
    App.eventBus.unregister(this);
  }

  @Override
  protected void onPause() {
    mapView.onPause();
    super.onPause();
  }
  
  @Override
  public void onDestroy() {
    mapView.onDestroy();
    super.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }
  
  @Override
  public void onMapReady(GoogleMap map) {
    gmap = map;
/*
    if (mData == null)
      return;

    if (mData.getTaskItem().getAddress().getLatitude() == 0.0
      && mData.getTaskItem().getAddress().getLongitude() == 0.0)
      return;

    gmap.setMinZoomPreference(12);
    gmap.setIndoorEnabled(true);
*/
    UiSettings uiSettings = gmap.getUiSettings();
    uiSettings.setIndoorLevelPickerEnabled(true);
    uiSettings.setMyLocationButtonEnabled(true);
    uiSettings.setMapToolbarEnabled(true);
    uiSettings.setCompassEnabled(true);
    uiSettings.setZoomControlsEnabled(true);


    //LatLng india = new LatLng(-34, 151);
    //gmap.moveCamera(CameraUpdateFactory.newLatLng(india));
/*
    LatLng markerLocation = new LatLng(
      mData.getTaskItem().getAddress().getLatitude(),
      mData.getTaskItem().getAddress().getLongitude());

    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(markerLocation);
    gmap.addMarker(mMarkerOptions);

    gmap.moveCamera(CameraUpdateFactory.newLatLng(markerLocation));

 */
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
}
