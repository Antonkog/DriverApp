package com.abona_erp.driver.app.ui.feature.main;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.BuildConfig;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.receiver.GeofenceBroadcastReceiver;
import com.abona_erp.driver.app.service.BackgroundServiceWorker;
import com.abona_erp.driver.app.service.ServiceWorker;
import com.abona_erp.driver.app.service.impl.GeofenceErrorMessages;
import com.abona_erp.driver.app.ui.base.BaseActivity;
import com.abona_erp.driver.app.ui.event.BackEvent;
import com.abona_erp.driver.app.ui.event.BaseEvent;
import com.abona_erp.driver.app.ui.event.CameraEvent;
import com.abona_erp.driver.app.ui.event.DeviceRegistratedEvent;
import com.abona_erp.driver.app.ui.event.InfoEvent;
import com.abona_erp.driver.app.ui.event.MapEvent;
import com.abona_erp.driver.app.ui.event.SoftwareAboutEvent;
import com.abona_erp.driver.app.ui.event.TaskDetailEvent;
import com.abona_erp.driver.app.ui.event.TaskStatusEvent;
import com.abona_erp.driver.app.ui.event.VehicleRegistrationEvent;
import com.abona_erp.driver.app.ui.feature.main.adapter.LastActivityAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.DetailFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.MainFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.about.SoftwareAboutFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.map.MapFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.PhotoFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.registration.DeviceNotRegistratedFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.settings.SettingsFragment;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DeviceUtils;
import com.abona_erp.driver.app.util.PowerMenuUtils;
import com.abona_erp.driver.app.util.gson.DoubleJsonDeserializer;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.core.base.ContextUtils;
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
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.tree.rh.ctlib.CT;

import org.greenrobot.eventbus.Subscribe;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import az.plainpie.PieView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements OnCompleteListener<Void> {
  
  private static final String TAG = MainActivity.class.getSimpleName();
  
  private final int REQUEST_APP_SETTINGS = 321;
  
  public static final String BACK_STACK_ROOT_TAG = "root_fragment";
  
  private Handler mHandler;
  
  private CommItem mCommItem;
  
  private RecyclerView lvLastActivity;
  private PieView mMainPieView;
  private AsapTextView mVehicleRegistrationNumber;
  private AsapTextView mVehicleClientName;
  
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
  
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // GET A NEW OR EXISTING VIEWMODEL FROM THE VIEWMODELPROVIDER.
    //
    mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

    // ---------------------------------------------------------------------------------------------
    // - Permission Request:
    // - Update FCM Token:
    //
    requestDriverPermission();
    FirebaseInstanceId.getInstance().getInstanceId()
      .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
        @Override
        public void onComplete(@NonNull Task<InstanceIdResult> task) {
          if (!task.isSuccessful()) {
            return;
          }
          TextSecurePreferences.setFcmToken(getBaseContext(), task.getResult().getToken());
          Log.d("MainActivity","Firebase registration Token=" + task.getResult().getToken());
          if (TextSecurePreferences.isDeviceFirstTimeRun(getBaseContext())) {
            AsyncTask.execute(new Runnable() {
              @Override
              public void run() {
                DriverDatabase db = DriverDatabase.getDatabase();
                DeviceProfileDAO dao = db.deviceProfileDAO();
                List<DeviceProfile> deviceProfiles = dao.getDeviceProfiles();
                if (deviceProfiles.size() > 0) {
                  TextSecurePreferences.setFcmTokenUpdate(getBaseContext(), true);
                  DateFormat dfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    Locale.getDefault());
                  dfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
                  Date currentTimestamp = new Date();
                  TextSecurePreferences.setFcmTokenLastSetTime(getBaseContext(), dfUtc.format(currentTimestamp));
    
                  deviceProfiles.get(0).setInstanceId(TextSecurePreferences.getFcmToken(getBaseContext()));
                  mMainViewModel.update(deviceProfiles.get(0));
                }
              }
            });
          }
        }
      });
    
    mVehicleClientName = (AsapTextView)findViewById(R.id.tv_vehicle_client_name);
    mVehicleClientName.setText(TextSecurePreferences.getClientName(getBaseContext()));
    mVehicleRegistrationNumber = (AsapTextView)findViewById(R.id.tv_vehicle_registration_number);
    mVehicleRegistrationNumber.setText(TextSecurePreferences.getVehicleRegistrationNumber(getBaseContext()));
    
    ////////////////////////////////////////////////////////////////////////////////////////////////

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
    
    mHandler = new Handler();
    
    /*
    SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
      Locale.getDefault());
    form.setTimeZone(TimeZone.getTimeZone("UTC"));
    Log.i(TAG, Locale.getDefault().toString());
    Date currentDate = AppUtils.getCurrentDateTime();
    Log.i(TAG, currentDate.toString());
    Log.i(TAG, form.format(currentDate));
    Log.i(TAG, form.format(AppUtils.getCurrentDateTimeUtc()));
    Log.i(TAG, "*********************************************************************");
*/
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
    /*
    mMainViewModel.getRowCount().observe(MainActivity.this, new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        if (integer == null)
          return;
        if (integer == 0) {
          mMainViewModel.getAllLastActivityItems().observe(MainActivity.this, new Observer<List<LastActivity>>() {
            @Override
            public void onChanged(List<LastActivity> lastActivities) {
              if (lastActivities.size() > 0) {
                for (int i = 0; i < lastActivities.size(); i++) {
                  mMainViewModel.delete(lastActivities.get(i));
                }
              }
            }
          });
        }
      }
    });
     */

    mMainViewModel.getAllLastActivityItems().observe(this, new Observer<List<LastActivity>>() {
      @Override
      public void onChanged(List<LastActivity> lastActivities) {
        lastActivityAdapter.setLastActivityItems(lastActivities);
      }
    });

    if (!TextSecurePreferences.isDeviceRegistrated(getBaseContext())) {
      loadMainFragment(DeviceNotRegistratedFragment.newInstance());
    } else {
      loadMainFragment(MainFragment.newInstance());
    }
    
    // Empty list for storing geofences.
    mGeofenceList = new ArrayList<>();
    mGeofencePendingIntent = null;
    populateGeofenceList();
    mGeofencingClient = LocationServices.getGeofencingClient(this);
    
    addGeofencesButtonHandler(null);
    startBackgroundWorkerService();
  }
  
  public BackgroundServiceWorker mBackgroundWorkerService;
  public Intent mIntentBackgroundWorkerService;
  public void startBackgroundWorkerService() {
    if (mBackgroundWorkerService == null) {
      mBackgroundWorkerService = new BackgroundServiceWorker(ContextUtils.getApplicationContext());
    }
    mIntentBackgroundWorkerService = new Intent(ContextUtils.getApplicationContext(),
      mBackgroundWorkerService.getClass());
    if (!isMyServiceRunning(mBackgroundWorkerService.getClass())) {
      startService(mIntentBackgroundWorkerService);
    }
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    
    if (requestCode == REQUEST_APP_SETTINGS) {
      Log.i(TAG, "onActivityResult() called! - REQUEST_APP_SETTINGS");
      
      if (!hasPermissions(Manifest.permission.READ_PHONE_STATE)) {
        Log.i(TAG, "onActivityResult() called! - No READ_PHONE_STATE permission...");
        TextSecurePreferences.setDevicePermissionsGranted(getBaseContext(), false);
        showPermissionErrorMessageAndFinish(getResources().getString(R.string.need_permissions), "No READ_PHONE_STATE permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.ACCESS_FINE_LOCATION)) {
        Log.i(TAG, "onActivityResult() called! - No ACCESS_FINE_LOCATION permission...");
        TextSecurePreferences.setDevicePermissionsGranted(getBaseContext(), false);
        showPermissionErrorMessageAndFinish(getResources().getString(R.string.need_permissions), "No ACCESS_FINE_LOCATION permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.CAMERA)) {
        Log.i(TAG, "onActivityResult() called! - No CAMERA permission...");
        TextSecurePreferences.setDevicePermissionsGranted(getBaseContext(), false);
        showPermissionErrorMessageAndFinish(getResources().getString(R.string.need_permissions), "No CAMERA permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.RECORD_AUDIO)) {
        Log.i(TAG, "onActivityResult() called! - No RECORD_AUDIO permission...");
        TextSecurePreferences.setDevicePermissionsGranted(getBaseContext(), false);
        showPermissionErrorMessageAndFinish(getResources().getString(R.string.need_permissions), "No RECORD_AUDIO permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        Log.i(TAG, "onActivityResult() called! - No WRITE_EXTERNAL_STORAGE permission...");
        TextSecurePreferences.setDevicePermissionsGranted(getBaseContext(), false);
        showPermissionErrorMessageAndFinish(getResources().getString(R.string.need_permissions), "No WRITE_EXTERNAL_STORAGE permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)) {
        Log.i(TAG, "onActivityResult() called! - No READ_EXTERNAL_STORAGE permission...");
        TextSecurePreferences.setDevicePermissionsGranted(getBaseContext(), false);
        showPermissionErrorMessageAndFinish(getResources().getString(R.string.need_permissions), "No READ_EXTERNAL_STORAGE permission...");
        return;
      }
    }
    
    TextSecurePreferences.setDevicePermissionsGranted(getBaseContext(), true);
  
    ServiceWorker serviceWorker = new ServiceWorker(getApplicationContext());
    Intent mServiceWorkerIntent = new Intent(getApplicationContext(), serviceWorker.getClass());
    if (!isMyServiceRunning(serviceWorker.getClass())) {
      Log.i(TAG, "******* START SERVICE WORKER *******");
      startService(mServiceWorkerIntent);
    } else {
      Log.i(TAG, "******* SERVICE WORKER IS ALREADY RUNNING *******");
    }
    
    initFirstTimeRun();
    
    super.onActivityResult(requestCode, resultCode, data);
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
  public void onMessageEvent(DeviceRegistratedEvent event) {
    loadMainFragment(MainFragment.newInstance());
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
      
      DriverDatabase db = DriverDatabase.getDatabase();
      OfflineConfirmationDAO dao = db.offlineConfirmationDAO();
      
      OfflineConfirmation offlineConfirmation = new OfflineConfirmation();
      offlineConfirmation.setNotifyId((int)notify.getId());
      offlineConfirmation.setConfirmType(ConfirmationType.TASK_CONFIRMED_BY_USER.ordinal());
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
          dao.insert(offlineConfirmation);
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
  
  @Subscribe
  public void onMessageEvent(VehicleRegistrationEvent event) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (mVehicleRegistrationNumber != null) {
          mVehicleRegistrationNumber.setText(TextSecurePreferences.getVehicleRegistrationNumber(getBaseContext()));
        }
        if (mVehicleClientName != null) {
          mVehicleClientName.setText(TextSecurePreferences.getClientName(getBaseContext()));
        }
      }
    });
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
    
    if (mCommItem != null)
      mCommItem = null;
    mCommItem = new CommItem();
    String json = event.getNotify().getData();
    mCommItem = App.getGson().fromJson(json, CommItem.class);
    
    if (mCommItem.getTaskItem().getAddress().getLongitude() == null)
      return;
    if (mCommItem.getTaskItem().getAddress().getLatitude() == null)
      return;
    
    Bundle bundle = new Bundle();
    bundle.putDouble("longitude", mCommItem.getTaskItem().getAddress().getLongitude().doubleValue());
    bundle.putDouble("latitude", mCommItem.getTaskItem().getAddress().getLatitude().doubleValue());
    bundle.putString("name", mCommItem.getTaskItem().getKundenName());
    fragment.setArguments(bundle);
    
    getSupportFragmentManager()
      .beginTransaction()
      .replace(R.id.main_container, fragment)
      .addToBackStack(null)
      .commit();
  }
  
  private void initFirstTimeRun() {
    Log.i(TAG, "initFirstTimeRun() called!");
  
    if (!TextSecurePreferences.isDeviceFirstTimeRun(getBaseContext())) {
      TextSecurePreferences.setFCMSenderID(getBaseContext(), "724562515953");
    
      DeviceProfile deviceProfile = new DeviceProfile();
      deviceProfile.setId(1);
      deviceProfile.setDeviceId(DeviceUtils.getUniqueIMEI(getBaseContext()));
      deviceProfile.setInstanceId(TextSecurePreferences.getFcmToken(getBaseContext()));
      deviceProfile.setDeviceModel(Build.MODEL);
      deviceProfile.setDeviceManufacturer(Build.MANUFACTURER);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        try {
          deviceProfile.setDeviceSerial(Build.getSerial());
        } catch (SecurityException e) {
          e.printStackTrace();
        }
      } else {
        deviceProfile.setDeviceSerial(Build.SERIAL);
      }
      deviceProfile.setLanguageCode(Locale.getDefault().toString());
      deviceProfile.setVersionCode(BuildConfig.VERSION_CODE);
      deviceProfile.setVersionName(BuildConfig.VERSION_NAME);
  
      DateFormat dfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.getDefault());
      dfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
      Date currentTimestamp = new Date();
      deviceProfile.setCreatedAt(dfUtc.format(currentTimestamp));
      deviceProfile.setModifiedAt(dfUtc.format(currentTimestamp));
      
      mMainViewModel.insert(deviceProfile);
     
      TextSecurePreferences.setDeviceFirstTimeRun(getBaseContext(), true);
    }
  }
  
  private void requestDriverPermission() {
    
    Dexter.withActivity(MainActivity.this)
      .withPermissions(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
      )
      .withListener(new MultiplePermissionsListener() {
        @Override
        public void onPermissionsChecked(MultiplePermissionsReport report) {
          Log.d(TAG, "onPermissionsChecked() called!");
    
          // check if all permissions are granted:
          if (report.areAllPermissionsGranted()) {
            Log.d(TAG, "areAllPermissionsGranted() called!");
            TextSecurePreferences.setDevicePermissionsGranted(getBaseContext(), true);
            
            ServiceWorker serviceWorker = new ServiceWorker(getApplicationContext());
            Intent mServiceWorkerIntent = new Intent(getApplicationContext(), serviceWorker.getClass());
            if (!isMyServiceRunning(serviceWorker.getClass())) {
              Log.i(TAG, "******* START SERVICE WORKER *******");
              startService(mServiceWorkerIntent);
            } else {
              Log.i(TAG, "******* SERVICE WORKER IS ALREADY RUNNING *******");
            }
            
            initFirstTimeRun();
          } else {
            Log.d(TAG, "!!!areAllPermissionsGranted() called!");
            TextSecurePreferences.setDevicePermissionsGranted(getBaseContext(), false);
            showSettingsDialog();
          }
          
          // check for permanent denial of any permission.
          if (report.isAnyPermissionPermanentlyDenied()) {
            Log.d(TAG, "isAnyPermissionPermanentlyDenied() called!");
            TextSecurePreferences.setDevicePermissionsGranted(getBaseContext(), false);
            showSettingsDialog();
          }
        }
  
        @Override
        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
          Log.i(TAG, "onPermissionRationaleShouldBeShown() called!");
          token.continuePermissionRequest();
        }
      })
      .withErrorListener(new PermissionRequestErrorListener() {
        @Override
        public void onError(DexterError error) {
          Log.i(TAG, "onError() called! " + error.toString());
          
          AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
          builder.setTitle("Permission Error");
          builder.setMessage(error.toString());
          builder.setPositiveButton(getResources().getString(R.string.action_ok),
            new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int i) {
              dialog.cancel();
            }
          });
          builder.show();
        }
      })
      .onSameThread()
      .check();
  }
  
  /**
   * Showing Alert Dialog with Settings Option.
   * Navigates User to App Settings.
   */
  private void showSettingsDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    builder.setTitle(getResources().getString(R.string.need_permissions));
    builder.setMessage(getResources().getString(R.string.permission_message_settings));
    builder.setPositiveButton(getResources().getString(R.string.menu_settings),
      new DialogInterface.OnClickListener() {
      
      @Override
      public void onClick(DialogInterface dialog, int i) {
        dialog.cancel();
        openSettings();
      }
    });
    builder.setNegativeButton(getResources().getString(R.string.action_cancel),
      new DialogInterface.OnClickListener() {
      
      @Override
      public void onClick(DialogInterface dialog, int i) {
        dialog.cancel();
        finish();
      }
    });
    builder.show();
  }
  
  /**
   * Navigating User to App Settings.
   */
  private void openSettings() {
    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", getPackageName(), null);
    intent.setData(uri);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
      | Intent.FLAG_ACTIVITY_CLEAR_TASK
      | Intent.FLAG_ACTIVITY_NO_HISTORY
      | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    startActivityForResult(intent, REQUEST_APP_SETTINGS);
  }
  
  private boolean hasPermissions(@NonNull String... permissions) {
    for (String permission : permissions) {
      if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {
        return false;
      }
    }
    return true;
  }
  
  private void showPermissionErrorMessageAndFinish(String title, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setPositiveButton(getResources().getString(R.string.action_ok),
      new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int i) {
          dialog.dismiss();
          finish();
        }
      });
    builder.show();
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
