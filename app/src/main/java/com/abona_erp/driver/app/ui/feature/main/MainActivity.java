package com.abona_erp.driver.app.ui.feature.main;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.ConfirmationItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.Data;
import com.abona_erp.driver.app.data.model.DataType;
import com.abona_erp.driver.app.data.model.Header;
import com.abona_erp.driver.app.manager.DriverWorkManager;
import com.abona_erp.driver.app.receiver.GeofenceBroadcastReceiver;
import com.abona_erp.driver.app.service.ServiceWorker;
import com.abona_erp.driver.app.service.impl.GeofenceErrorMessages;
import com.abona_erp.driver.app.ui.base.BaseActivity;
import com.abona_erp.driver.app.ui.event.BackEvent;
import com.abona_erp.driver.app.ui.event.BaseEvent;
import com.abona_erp.driver.app.ui.event.CameraEvent;
import com.abona_erp.driver.app.ui.event.InfoEvent;
import com.abona_erp.driver.app.ui.event.MapEvent;
import com.abona_erp.driver.app.ui.event.SoftwareAboutEvent;
import com.abona_erp.driver.app.ui.event.TaskDetailEvent;
import com.abona_erp.driver.app.ui.event.TaskStatusEvent;
import com.abona_erp.driver.app.ui.feature.main.adapter.LastActivityAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.DetailFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.MainFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.about.SoftwareAboutFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.map.MapFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.PhotoFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.settings.SettingsFragment;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.util.DateConverter;
import com.abona_erp.driver.app.util.DeviceUtils;
import com.abona_erp.driver.app.util.PowerMenuUtils;
import com.abona_erp.driver.app.util.concurrent.MainUiThread;
import com.abona_erp.driver.app.util.gson.DoubleJsonDeserializer;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.tree.rh.ctlib.CT;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import az.plainpie.PieView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements OnCompleteListener<Void> {
  
  private static final String TAG = MainActivity.class.getSimpleName();
  
  public static final String BACK_STACK_ROOT_TAG = "root_fragment";
  
  private Handler mHandler;

  private Gson mGson;
  private Data mData;
  private WorkManager mWorkManager;
  
  private RecyclerView lvLastActivity;
  private PieView mMainPieView;
  
  private PowerMenu mProfileMenu;
  private AppCompatImageButton mMainPopupMenu;

  // VIEW MODEL:
  private MainViewModel mMainViewModel;
  
  private enum PendingGeofenceTask {
    ADD, REMOVE, NONE
  }
  
  // Provides access to the Geofencing API.
  private GeofencingClient mGeofencingClient;
  private PendingIntent mGeofencePendingIntent;
  private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;
  private ArrayList<Geofence> mGeofenceList;
  
  @Override
  public void onComplete(@NonNull Task<Void> task) {
    mPendingGeofenceTask = PendingGeofenceTask.NONE;
    if (task.isSuccessful()) {
    
    } else {
      // Get the status code for the error and log it using a user-friendly message.
      String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
      Log.w("*****", errorMessage);
    }
  }
  
  private OnMenuItemClickListener<PowerMenuItem> onProfileItemClickListener =
    new OnMenuItemClickListener<PowerMenuItem>() {
      @Override
      public void onItemClick(int position, PowerMenuItem item) {
        loadFragment(SettingsFragment.newInstance());
        mProfileMenu.dismiss();
      }
    };
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // ---------------------------------------------------------------------------------------------
    // Start Worker Service:
    ServiceWorker serviceWorker = new ServiceWorker(getApplicationContext());
    Intent mServiceWorkerIntent = new Intent(getApplicationContext(), serviceWorker.getClass());
    if (!isMyServiceRunning(serviceWorker.getClass())) {
      startService(mServiceWorkerIntent);
    }
    
    // ---------------------------------------------------------------------------------------------
    // Permission Request:
    Dexter.withActivity(this)
      .withPermissions(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE)
      .withListener(new MultiplePermissionsListener() {
        @Override
        public void onPermissionsChecked(MultiplePermissionsReport report) {
          Log.i(TAG, "onPermissionChecked() called!");
          TextSecurePreferences.setDeviceIMEI(getBaseContext(), DeviceUtils.getUniqueIMEI(getBaseContext()));
          TextSecurePreferences.setDeviceModel(getBaseContext(), Build.MODEL);
          TextSecurePreferences.setDeviceManufacturer(getBaseContext(), Build.MANUFACTURER);
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
              TextSecurePreferences.setDeviceSerial(getBaseContext(), Build.getSerial());
            } catch (SecurityException e) {
              e.printStackTrace();
            }
          } else {
            TextSecurePreferences.setDeviceSerial(getBaseContext(), Build.SERIAL);
          }
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              //requestToken(App.spManager.getFirebaseToken());
            }
          });
        
        }
      
        @Override
        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
        
        }
      }).check();
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////

    
  
    if (!App.spManager.getFirstTimeRun()) {
      TextSecurePreferences.setFCMSenderID(getBaseContext(), "724562515953");
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    mWorkManager = WorkManager.getInstance(this);

    mMainPieView = (PieView)findViewById(R.id.mainPieView);
    mMainPieView.setPercentageBackgroundColor(getResources().getColor(R.color.clrAbona));
    mMainPieView.setInnerBackgroundColor(getResources().getColor(R.color.clrFont));
    mMainPieView.setInnerText("0 %");
    mMainPieView.setPercentage(0);
    
    mMainPopupMenu = (AppCompatImageButton)findViewById(R.id.main_popup_menu);
    mProfileMenu = PowerMenuUtils.getProfilePowerMenu(this, this,
      onProfileItemClickListener);
    mMainPopupMenu.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mProfileMenu.showAsAnchorRightTop(mMainPopupMenu);
      }
    });

    JsonDeserializer deserializer = new DoubleJsonDeserializer();
    mGson = new GsonBuilder()
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
      .registerTypeAdapter(double.class, deserializer)
      .registerTypeAdapter(Double.class, deserializer)
      .create();

    lvLastActivity = (RecyclerView)findViewById(R.id.lv_last_activity);
    LinearLayoutManager recyclerLayoutManager =
      new LinearLayoutManager(getApplicationContext(),
        RecyclerView.VERTICAL, false);
    lvLastActivity.setLayoutManager(recyclerLayoutManager);
    LastActivityAdapter lastActivityAdapter = new LastActivityAdapter(getApplicationContext());
    lastActivityAdapter.setOnItemListener(new LastActivityAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(int mandantID, int taskId) {
        mMainViewModel.getNotifyByMandantTaskId(mandantID, taskId).observeOn(AndroidSchedulers.mainThread())
          .subscribeOn(Schedulers.io())
          .subscribe(new DisposableSingleObserver<Notify>() {
            @Override
            public void onSuccess(Notify notify) {
              App.eventBus.post(new TaskDetailEvent(notify));
            }
  
            @Override
            public void onError(Throwable e) {
              //Dialogs.showInfoDialog(getApplicationContext(), "Task Item", "Task existiert nicht mehr!");
              App.eventBus.post(new InfoEvent());
            }
          });
      }
    });
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
          App.spManager.setFirebaseToken(task.getResult().getToken());
          Log.d("MainActivity","Firebase registration Token=" + task.getResult().getToken());
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
  
    // Empty list for storing geofences.
    mGeofenceList = new ArrayList<>();
    mGeofencePendingIntent = null;
    populateGeofenceList();
    mGeofencingClient = LocationServices.getGeofencingClient(this);
    
    addGeofencesButtonHandler(null);
  }
  
  private boolean isMyServiceRunning(Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        Log.i(TAG, "isMyServiceRunning? " + "true");
        return true;
      }
    }
    Log.i(TAG, "isMyServiceRunning?" + "false");
    return false;
  }
  
  private void populateGeofenceList() {
    for (Map.Entry<String, LatLng> entry : Constants.BAY_AREA_LANDMARKS.entrySet()) {
      
      mGeofenceList.add(new Geofence.Builder()
        // Set the request ID of the geofence. This is a string to identify this
        // geofence.
        .setRequestId(entry.getKey())
        
        // Set the circular region of this geofence.
        .setCircularRegion(
          entry.getValue().latitude,
          entry.getValue().longitude,
          Constants.GEOFENCE_RADIUS_IN_METERS
        )
        
        // Set the expiration duration of the geofence. This geofence gets automatically
        // removed after this period of time.
        .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
        
        // Set the transition types of interest. Alerts are only generated for these
        // transition. We track entry and exit transitions in this sample.
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
          Geofence.GEOFENCE_TRANSITION_EXIT)
        
        // Create the geofence.
        .build());
    }
  }
  
  public void showNotification(Context context, String title, String body, Intent intent) {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    
    int notificationId = 1;
    String channelId = "channel-01";
    String channelName = "Channel Name";
    int importance = NotificationManager.IMPORTANCE_HIGH;
    
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      NotificationChannel mChannel = new NotificationChannel(
        channelId, channelName, importance);
      notificationManager.createNotificationChannel(mChannel);
    }
    
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle(title)
      .setContentText(body);
    
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    stackBuilder.addNextIntent(intent);
    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
      0,
      PendingIntent.FLAG_UPDATE_CURRENT
    );
    mBuilder.setContentIntent(resultPendingIntent);
    
    notificationManager.notify(notificationId, mBuilder.build());
  }
  
  @Subscribe
  public void onMessageEvent(TaskStatusEvent event) {
    if (event.getPercentage() < 0)
      return;
    
    mMainPieView.setPercentage(event.getPercentage());
    mMainPieView.setInnerText(event.getPercentage() + " %");
    if (event.getPercentage() == 100) {
      mMainPieView.setPercentageBackgroundColor(getResources().getColor(R.color.clrTaskFinished));
    } else {
      mMainPieView.setPercentageBackgroundColor(getResources().getColor(R.color.clrAbona));
    }
  }

  @Subscribe
  public void onMessageEvent(MapEvent event) {
    loadMapFragment(MapFragment.newInstance(), event);
  }
  
  @Subscribe
  public void onMessageEvent(CameraEvent event) {
    loadCameraFragment(PhotoFragment.newInstance(), event);
  }
  
  @Subscribe
  public void onMessageEvent(TaskDetailEvent event) {
  
    Notify notify = event.getNotify();
    if (!notify.getRead()) {
      notify.setRead(true);
      mMainViewModel.update(notify);
      
      Data data = new Data();
      data = App.getGson().fromJson(notify.getData(), Data.class);
      
      Data confirmData = new Data();
      Header confirmHeader = new Header();
      confirmHeader.setDataType(DataType.CONFIRMATION);
      confirmHeader.setTimestampSenderUTC(data.getHeader().getTimestampSenderUTC());
      confirmData.setHeader(confirmHeader);
      ConfirmationItem confirmationItem = new ConfirmationItem();
      confirmationItem.setConfirmationType(ConfirmationType.TASK_CONFIRMED_BY_USER);
      Date date = DateConverter.fromTimestamp(new Date().toString());
      confirmationItem.setTimeStampConfirmationUTC(date);
      confirmationItem.setTaskChangeId(data.getTaskItem().getTaskChangeId());
      confirmationItem.setMandantId(data.getTaskItem().getMandantId());
      confirmationItem.setTaskId(data.getTaskItem().getTaskId());
      //confirmationItem.setTaskItem(data.getTaskItem());
      confirmData.setConfirmationItem(confirmationItem);
  
      Call<Data> call = App.apiManager.getConfirmApi().confirm(confirmData);
      call.enqueue(new Callback<Data>() {
        @Override
        public void onResponse(Call<Data> call, Response<Data> response) {
          if (response.isSuccessful()) {
            Log.d(TAG, "*********************************** SUCCESSFUL");
            Log.d(TAG, response.toString());
          } else {
            Log.w(TAG, "********************************* ERROR in onResponse");
          }
        }
    
        @Override
        public void onFailure(Call<Data> call, Throwable t) {
          Log.w(TAG, t.getMessage());
        }
      });
    }
    
    loadActivityFragment(DetailFragment.newInstance(), event);
  }
  
  @Subscribe
  public void onMessageEvent(SoftwareAboutEvent event) {
    loadSoftwareAboutFragment(SoftwareAboutFragment.newInstance());
  }
  
  @Subscribe
  public void onMessageEvent(InfoEvent event) {
    new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
      .setTitleText("NOT FOUND")
      .setContentText("Task existiert nicht mehr!")
      .setConfirmText("OK")
      .show();
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
    FragmentManager fragments = getSupportFragmentManager();
    Fragment mainFrag = fragments.findFragmentByTag("main");
    
    if (fragments.getBackStackEntryCount() > 1) {
      fragments.popBackStackImmediate();
    } else {
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
  }
  
  @Override
  protected void onStart() {
    super.onStart();
    App.eventBus.register(this);
    
    performPendingGeofenceTask();
  }
  
  @Override
  protected void onStop() {
    super.onStop();
    App.eventBus.unregister(this);
  }
  
  private GeofencingRequest getGeofencingRequest() {
    GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
    
    // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
    // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
    // is already inside that geofence.
    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
    
    // Add the geofences to be monitored by geofencing service.
    builder.addGeofences(mGeofenceList);
    
    // Return a GeofencingRequest.
    return builder.build();
  }
  
  public void addGeofencesButtonHandler(View view) {
    addGeofences();
  }
  
  @SuppressWarnings("MissingPermission")
  private void addGeofences() {
    mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
      .addOnCompleteListener(this);
  }
  
  public void removeGeofencesButtonHandler(View view) {
    removeGeofences();
  }
  
  @SuppressWarnings("MissingPermission")
  private void removeGeofences() {
    mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
  }
  
  private PendingIntent getGeofencePendingIntent() {
    // Reuse the PendingIntent if we already have it.
    if (mGeofencePendingIntent != null) {
      return mGeofencePendingIntent;
    }
    Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
    // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
    // addGeofences() and removeGeofences().
    mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    return mGeofencePendingIntent;
  }
  
  private void performPendingGeofenceTask() {
    if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
      addGeofences();
    } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
      removeGeofences();
    }
  }
  
  private void requestToken(String token) {
  
    Constraints mConstraints = new Constraints.Builder()
      .setRequiresCharging(false)
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build();
    
    OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest
      .Builder(DriverWorkManager.class)
      .setConstraints(mConstraints)
      .setBackoffCriteria(BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
        TimeUnit.MICROSECONDS)
      .setInitialDelay(10, TimeUnit.SECONDS)
      .setInputData(createInputData(token))
      .addTag(UUID.randomUUID().toString())
      .build();
    mWorkManager.enqueue(oneTimeWorkRequest);
  }
  
  private androidx.work.Data createInputData(String token) {
    androidx.work.Data data;
    if (!App.spManager.getFirstTimeRun()) {
      App.spManager.setFirstTimeRun(true);
      // FIRST INSTALL: STATE 0
      data = new androidx.work.Data.Builder()
        .putString("token", token)
        .putInt("state", 0)
        .build();
    } else {
      // TOKEN UPDATE: STATE 1
      data = new androidx.work.Data.Builder()
        .putString("token", token)
        .putInt("state", 1)
        .build();
    }
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
  
  private void loadFragment(Fragment fragment) {
    getSupportFragmentManager()
      .beginTransaction()
      .replace(R.id.main_container, fragment)
      .addToBackStack(null)
      .commit();
  }
  
  private void loadCameraFragment(Fragment fragment, CameraEvent event) {
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
  
  private void loadSoftwareAboutFragment(Fragment fragment) {
    getSupportFragmentManager()
      .beginTransaction()
      .replace(R.id.main_container, fragment)
      .addToBackStack(null)
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
