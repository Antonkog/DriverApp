package com.abona_erp.driver.app.ui.feature.main;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.Data;
import com.abona_erp.driver.app.manager.DriverWorkManager;
import com.abona_erp.driver.app.ui.base.BaseActivity;
import com.abona_erp.driver.app.ui.event.BackEvent;
import com.abona_erp.driver.app.ui.event.BaseEvent;
import com.abona_erp.driver.app.ui.event.MapEvent;
import com.abona_erp.driver.app.ui.event.TaskDetailEvent;
import com.abona_erp.driver.app.ui.feature.main.adapter.LastActivityAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.DetailFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.MainFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.map.MapFragment;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.util.DoubleJsonDeserializer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.tree.rh.ctlib.CT;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import az.plainpie.PieView;

public class MainActivity extends BaseActivity {
  
  public static final String BACK_STACK_ROOT_TAG = "root_fragment";
  
  private Handler mHandler;

  private Gson mGson;
  private Data mData;
  
  private RecyclerView lvLastActivity;
  private PieView mMainPieView;

  // VIEW MODEL:
  private MainViewModel mMainViewModel;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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

    //mActivityList = new ArrayList<>();

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
    
    FirebaseInstanceId.getInstance().getInstanceId()
      .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
        @Override
        public void onComplete(@NonNull Task<InstanceIdResult> task) {
          if (!task.isSuccessful()) {
            return;
          }
        
          // Get new Instance ID token:
          String token = task.getResult().getToken();
          Log.d("TEST","Firebase registration Token=" + token);
          requestToken(token);
        }
      });

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

    loadMainFragment(MainFragment.newInstance());
  }

  @Subscribe
  public void onMessageEvent(MapEvent event) {
    loadMapFragment(MapFragment.newInstance(), event);
  }
  
  @Subscribe
  public void onMessageEvent(TaskDetailEvent event) {
  
    Notify notify = event.getNotify();
    if (!notify.getRead()) {
      notify.setRead(true);
      mMainViewModel.update(notify);
    }
    
    loadActivityFragment(DetailFragment.newInstance(), event);
  }
  
  @Subscribe
  public void onMessageEvent(BackEvent event) {
    FragmentManager fragments = getSupportFragmentManager();
    Fragment mainFrag = fragments.findFragmentByTag("main");
    
    if (fragments.getBackStackEntryCount() > 1) {
      fragments.popBackStackImmediate();
    }
  }

  private static long back_pressed;
  @Override
  public void onBackPressed() {
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
  protected void onStart() {
    super.onStart();
    App.eventBus.register(this);
  }
  
  @Override
  protected void onStop() {
    super.onStop();
    App.eventBus.unregister(this);
  }
  
  private void requestToken(String token) {
  
    Constraints mConstraints = new Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build();
    
    OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest
      .Builder(DriverWorkManager.class)
      .setConstraints(mConstraints)
      .setInputData(createInputData(token))
      .addTag(UUID.randomUUID().toString())
      /*.setInitialDelay(2, TimeUnit.SECONDS)*/
      .build();
    WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
    //App.getWorkManager().enqueue(oneTimeWorkRequest);
  }
  
  private androidx.work.Data createInputData(String token) {
    androidx.work.Data data = new androidx.work.Data.Builder()
      .putString("token", token)
      .build();
    return data;
  }
  
  private void loadMainFragment(Fragment fragment) {
    
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    
    // Add the new fragment:
    fragmentManager.beginTransaction()
      .replace(R.id.main_container, fragment, "main")
      .addToBackStack(BACK_STACK_ROOT_TAG)
      .commit();
  }
  
  private void loadActivityFragment(Fragment fragment, TaskDetailEvent event) {
    if (event == null)
      return;
    
    Bundle bundle = new Bundle();
    bundle.putInt("oid", event.getNotify().getId());
    fragment.setArguments(bundle);
    
    getSupportFragmentManager()
      .beginTransaction()
      .replace(R.id.main_container, fragment)
      .addToBackStack(null)
      .commit();
  }
  
  private void loadMapFragment(Fragment fragment, MapEvent event) {
    
    if (event == null)
      return;
    
    if (mData != null)
      mData = null;
    mData = new Data();
    String json = event.getNotify().getData();
    mData = mGson.fromJson(json, Data.class);
    
    if (mData.getTaskItem().getAddress().getLongitude() == null)
      return;
    if (mData.getTaskItem().getAddress().getLatitude() == null)
      return;
    
    Bundle bundle = new Bundle();
    bundle.putDouble("longitude", mData.getTaskItem().getAddress().getLongitude().doubleValue());
    bundle.putDouble("latitude", mData.getTaskItem().getAddress().getLatitude().doubleValue());
    bundle.putString("name", mData.getTaskItem().getKundenName());
    fragment.setArguments(bundle);
    
    getSupportFragmentManager()
      .beginTransaction()
      .replace(R.id.main_container, fragment)
      .addToBackStack(null)
      .commit();
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
