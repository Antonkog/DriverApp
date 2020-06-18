package com.abona_erp.driver.app.ui.feature.main;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.BuildConfig;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.converters.LogLevel;
import com.abona_erp.driver.app.data.converters.LogType;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.LogItem;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.model.AppFileInterchangeItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ResultOfAction;
import com.abona_erp.driver.app.data.model.TaskItem;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.receiver.ConnectivityChangeReceiver;
import com.abona_erp.driver.app.receiver.GeofenceBroadcastReceiver;
import com.abona_erp.driver.app.service.BackgroundServiceWorker;
import com.abona_erp.driver.app.service.impl.GeofenceErrorMessages;
import com.abona_erp.driver.app.ui.base.BaseActivity;
import com.abona_erp.driver.app.ui.event.BaseEvent;
import com.abona_erp.driver.app.ui.event.ConnectivityEvent;
import com.abona_erp.driver.app.ui.event.DocumentEvent;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.event.ProfileEvent;
import com.abona_erp.driver.app.ui.event.ProtocolEvent;
import com.abona_erp.driver.app.ui.event.TaskStatusEvent;
import com.abona_erp.driver.app.ui.event.VehicleRegistrationEvent;
import com.abona_erp.driver.app.ui.feature.login.LoginActivity;
import com.abona_erp.driver.app.ui.feature.main.adapter.LastActivityAdapter;
import com.abona_erp.driver.app.ui.feature.main.fragment.DetailFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.MainFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.about.SoftwareAboutFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.document_viewer.DocumentViewerFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.map.MapFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.photo.PhotoFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.protocol.ProtocolFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.registration.DeviceNotRegistratedFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.settings.SettingsFragment;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DeviceUtils;
import com.abona_erp.driver.app.util.PowerMenuUtils;
import com.abona_erp.driver.app.util.RingtoneUtils;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.core.base.ContextUtils;
import com.devexpress.logify.alert.android.LogifyAlert;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.MessageDialog;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import az.plainpie.PieView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity /*implements OnCompleteListener<Void>*/ {
  
  private static final String TAG = MainActivity.class.getSimpleName();
  
  private final int REQUEST_APP_SETTINGS = 321;
  
  public static final String BACK_STACK_ROOT_TAG = "root_fragment";
  
  private Handler mHandler;
  
  private CommItem mCommItem;
  
  private RecyclerView lvLastActivity;
  private PieView mMainPieView;
  private AsapTextView mVehicleRegistrationNumber;
  private AsapTextView mVehicleClientName;
  
  private CircleImageView mProfile1;
  private CircleImageView mProfile2;
  
  private PowerMenu mProfileMenu;
  private AppCompatImageButton mMainPopupMenu;
  private AppCompatImageButton mBtnGetAllTasks;

  // VIEW MODEL:
  private MainViewModel mMainViewModel;
  
  private enum PendingGeofenceTask {
    ADD, REMOVE, NONE
  }
  
  // Provides access to the Geofencing API.
  /*
  private GeofencingClient mGeofencingClient;
  private PendingIntent mGeofencePendingIntent;
  private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;
  private ArrayList<Geofence> mGeofenceList;
  */
  private TelephonyManager mTelephonyManager;
  private ConnectivityChangeReceiver connectivityChangeReceiver = null;
  private SignalStrengthStateListener signalStrengthStateListener = null;
  /*
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
  */
  private OnMenuItemClickListener<PowerMenuItem> onProfileItemClickListener =
    new OnMenuItemClickListener<PowerMenuItem>() {
      @Override
      public void onItemClick(int position, PowerMenuItem item) {
        loadFragment(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_SETTINGS),
          null), SettingsFragment.newInstance());
        mProfileMenu.dismiss();
      }
    };
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    if (TextSecurePreferences.enableLoginPage()) {
      Intent intent = new Intent(this, LoginActivity.class);
      startActivity(intent);
      finish();
      return;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // LOGIFY
    initializeLogify();
    
    connectivityChangeReceiver = new ConnectivityChangeReceiver();
    signalStrengthStateListener = new SignalStrengthStateListener();
    mTelephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
  
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
          
          if (TextUtils.isEmpty(TextSecurePreferences.getFcmToken(getBaseContext()))
            || TextSecurePreferences.getFcmToken(getBaseContext()).length() <= 0
            || !TextSecurePreferences.getFcmToken(getBaseContext()).equals(task.getResult().getToken())) {
            TextSecurePreferences.setFcmToken(getBaseContext(), task.getResult().getToken());
            Log.d("MainActivity","Firebase registration Token=" + task.getResult().getToken());
            if (TextSecurePreferences.isDeviceFirstTimeRun()) {
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
    
    mProfile1 = (CircleImageView)findViewById(R.id.profile1);
    mProfile2 = (CircleImageView)findViewById(R.id.profile2);
    
    mMainPopupMenu = (AppCompatImageButton)findViewById(R.id.main_popup_menu);
    mProfileMenu = PowerMenuUtils.getProfilePowerMenu(this, this,
      onProfileItemClickListener);
    mMainPopupMenu.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mProfileMenu.showAsAnchorRightTop(mMainPopupMenu);
      }
    });
    
    mBtnGetAllTasks = (AppCompatImageButton)findViewById(R.id.btn_get_all_task);
    mBtnGetAllTasks.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View view) {
        
        mBtnGetAllTasks.setEnabled(false);
        
        Call<ResultOfAction> call = App.getInstance().apiManager.getTaskApi().getAllTasks(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
        call.enqueue(new Callback<ResultOfAction>() {
          @Override
          public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
            if (response.isSuccessful() && response.body() != null) {
              handleGetAllTasks(response.body());
            } else {
              
              switch (response.code()) {
                case 401:
                  handleAccessToken();
                  break;
                default:
                  // TODO: Log
                  break;
              }
  
              mBtnGetAllTasks.setEnabled(true);
            }
          }
  
          @Override
          public void onFailure(Call<ResultOfAction> call, Throwable t) {
            // TODO: Wahrscheinlich kein Internet!
            mBtnGetAllTasks.setEnabled(true);
          }
        });
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
              App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_TASK), notify));
            }
  
            @Override
            public void onError(Throwable e) {
              App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_TASK_NOT_FOUND), null));
            }
          });
      }
    });
    lvLastActivity.setAdapter(lastActivityAdapter);
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    mHandler = new Handler();
    
    mMainViewModel.getNotReadNotificationCount().observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        if (integer == null)
          return;
        int value = integer;
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
    
    mMainViewModel.getAllOfflineConfirmations().observe(this, new Observer<List<OfflineConfirmation>>() {
      @Override
      public void onChanged(List<OfflineConfirmation> offlineConfirmations) {
        int count = offlineConfirmations.size();
        if (count > 0) {
          ((AsapTextView)findViewById(R.id.badge_process)).setVisibility(View.VISIBLE);
          if (count <= 99) {
            ((AsapTextView)findViewById(R.id.badge_process)).setText(String.valueOf(count));
          } else {
            ((AsapTextView)findViewById(R.id.badge_process)).setText("99+");
          }
        } else {
          ((AsapTextView)findViewById(R.id.badge_process)).setVisibility(View.GONE);
        }
      }
    });

    mMainViewModel.getAllLastActivityItems().observe(this, new Observer<List<LastActivity>>() {
      @Override
      public void onChanged(List<LastActivity> lastActivities) {
        lastActivityAdapter.setLastActivityItems(lastActivities);
      }
    });

    if (!TextSecurePreferences.isDeviceRegistrated()) {
      loadMainFragment(DeviceNotRegistratedFragment.newInstance());
    } else {
      loadMainFragment(MainFragment.newInstance());
    }
    
    /*
    // Empty list for storing geofences.
    mGeofenceList = new ArrayList<>();
    mGeofencePendingIntent = null;
    populateGeofenceList();
    mGeofencingClient = LocationServices.getGeofencingClient(this);
    
    addGeofencesButtonHandler(null);
     */
    startBackgroundWorkerService();
    
    App.eventBus.post(new TaskStatusEvent(TextSecurePreferences.getTaskPercentage(getBaseContext())));
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
        TextSecurePreferences.setDevicePermissionsGranted(false);
        showPermissionErrorMessageAndFinish(getResources().getString(R.string.need_permissions), "No READ_PHONE_STATE permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.ACCESS_FINE_LOCATION)) {
        Log.i(TAG, "onActivityResult() called! - No ACCESS_FINE_LOCATION permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        showPermissionErrorMessageAndFinish(getResources().getString(R.string.need_permissions), "No ACCESS_FINE_LOCATION permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.CAMERA)) {
        Log.i(TAG, "onActivityResult() called! - No CAMERA permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        showPermissionErrorMessageAndFinish(getResources().getString(R.string.need_permissions), "No CAMERA permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.RECORD_AUDIO)) {
        Log.i(TAG, "onActivityResult() called! - No RECORD_AUDIO permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        showPermissionErrorMessageAndFinish(getResources().getString(R.string.need_permissions), "No RECORD_AUDIO permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        Log.i(TAG, "onActivityResult() called! - No WRITE_EXTERNAL_STORAGE permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        showPermissionErrorMessageAndFinish(getResources().getString(R.string.need_permissions), "No WRITE_EXTERNAL_STORAGE permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)) {
        Log.i(TAG, "onActivityResult() called! - No READ_EXTERNAL_STORAGE permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        showPermissionErrorMessageAndFinish(getResources().getString(R.string.need_permissions), "No READ_EXTERNAL_STORAGE permission...");
        return;
      }
    }
    
    TextSecurePreferences.setDevicePermissionsGranted(true);
  /*
    ServiceWorker serviceWorker = new ServiceWorker(getApplicationContext());
    Intent mServiceWorkerIntent = new Intent(getApplicationContext(), serviceWorker.getClass());
    if (!isMyServiceRunning(serviceWorker.getClass())) {
      Log.i(TAG, "******* START SERVICE WORKER *******");
      startService(mServiceWorkerIntent);
    } else {
      Log.i(TAG, "******* SERVICE WORKER IS ALREADY RUNNING *******");
    }
    */
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
  /*
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
  */
  /*
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
  */
  private static boolean mConnected;
  @Subscribe
  public void onMessageEvent(ConnectivityEvent event) {
    AppCompatImageButton connectivity = (AppCompatImageButton)findViewById(R.id.connectivity);
    if (event.getConnectivityStatus() == 0) {
      mConnected = false;
      connectivity.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_no_signal));
      connectivity.setColorFilter(getApplicationContext().getResources().getColor(R.color.clrAbona));
    } else if (event.getConnectivityStatus() == 1) {
      mConnected = true;
      connectivity.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_signal));
    } else if (event.getConnectivityStatus() == 2) {
      mConnected = true;
      connectivity.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_signal_wifi_24px));
      ((AppCompatImageButton)findViewById(R.id.connectivity)).setColorFilter(Color.parseColor("#009432"));
    }
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
  public void onMessageEvent(PageEvent event) {
    switch (event.getPageItem().pageItem) {
      case PageItemDescriptor.PAGE_BACK:
        FragmentManager fm = getSupportFragmentManager();
        Fragment mainFragment = fm.findFragmentByTag("main");
        
        if (fm.getBackStackEntryCount() > 1) {
          fm.popBackStackImmediate();
        }
        break;
        
      case PageItemDescriptor.PAGE_TASK:
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
        loadFragment(event, DetailFragment.newInstance());
        break;
        
      case PageItemDescriptor.PAGE_MAP:
        loadFragment(event, MapFragment.newInstance());
        break;
        
      case PageItemDescriptor.PAGE_CAMERA:
        loadFragment(event, PhotoFragment.newInstance());
        break;
        
      case PageItemDescriptor.PAGE_DOCUMENT:
        loadFragment(event, DocumentViewerFragment.newInstance());
        break;
        
      case PageItemDescriptor.PAGE_ABOUT:
        loadFragment(event, SoftwareAboutFragment.newInstance());
        break;
        
      case PageItemDescriptor.PAGE_TASK_NOT_FOUND:
        messageBox_Ok("Not Found", "Task existiert nicht mehr!");
        break;
        
      case PageItemDescriptor.PAGE_DEVICE_REGISTRATED:
        loadMainFragment(MainFragment.newInstance());
        
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString deviceID = new SpannableString(DeviceUtils.getUniqueIMEI(getBaseContext()));
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        deviceID.setSpan(boldSpan, 0 , deviceID.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        builder.append("Device registration was successfully!\n\nRegistration Number\n");
        builder.append(deviceID);
        
        messageBox_Ok("Successful Registrated", builder.toString());
        break;
        
      case PageItemDescriptor.PAGE_NEW_DOCUMENTS:
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Notify notify = event.getNotify();
            
            MessageDialog.build((AppCompatActivity) MainActivity.this)
              .setStyle(DialogSettings.STYLE.STYLE_IOS)
              .setTheme(DialogSettings.THEME.LIGHT)
              .setTitle(getApplicationContext().getResources().getString(R.string.new_document))
              .setMessage(getApplicationContext().getResources().getString(R.string.new_document_message)
                + "\n"
                + getApplicationContext().getResources().getString(R.string.order_no)
                + ": "
                + AppUtils.parseOrderNo(notify.getOrderNo()))
              .setOkButton(getApplicationContext().getResources().getString(R.string.action_ok),
                new OnDialogButtonClickListener() {
                  @Override
                  public boolean onClick(BaseDialog baseDialog, View v) {
                    return false;
                  }
                })
              .show();
          }
        });
        break;
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
        
        if (event.isDeleteAll()) {
          mMainPieView.setPercentage(0);
        }
      }
    });
  }
  
  @Subscribe
  public void onMessageEvent(ProfileEvent event) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (event.getImageUrl() != null && !TextUtils.isEmpty(event.getImageUrl())) {
          /*
          Picasso.with(getBaseContext())
            .load(event.getImageUrl())
            .centerCrop()
            .transform(new CircleTransform(50, 0))
            .fit()
            .into(mProfile1);
           */
          
          
  /*
          String b = "0x89504E470D0A1A0A0000000D4948445200000099000000960806000000CD0A2A6F0000000467414D410000B18F0BFC6105000000097048597300000EC200000EC20115284A800000FFB249444154785E3CBD65741D67A2ACED642873263393994C980D71CC6CCBB2C5CCCCCCCCCCCCCCCCCC2C8B192DCBB264999921B613274ECECCB977ADFAAA77CE777FF4DAD2D6A6EEB7DEAAA7BA7BB7B6DC7EFA1AF79FBDC6D3973FE1E54F3FE1A75F5EE3975F7FC6CFBC7DFDE6155EFFFA231E3C7B888D1B57B0767903F32B8B985B5DC2F4D9794CCECFE2D2B5F378F5E30DBCF9E5229E3C5BC6F9F531CC2E0E627E79100B4B2398981AC0D06837DABA5B51D75E8BE1894EF49F69414D4B1986C76BF1F0C1109E3D1FC6A5AB1D189B28C7C05011FA068BD0DC9EC6C7A56373B31CB76ED4E0D9D33E3EB6138F1ED6E0E79F1AF0DFBF4EE1D75FE7F0CB2F9378F96210E75773B0793117AF5F37E3BFDFB4E0E9A362FE9E804B9772B1BC9884EB574BF8BC61BCFE7188CF9DE53287EF9F75E2D27A262EAC24E2C6956CFCF0B217AF5E74E0C9E30A3C7D528E1F5FB6F03DA6F1EB9B19BCFA7E006F7E1EE6B619E4DF1BB0B19E86572F3BF0DFBFB4E3CD9B33B87FAF1E776E17E08717ED7CFC029FB7C0F72AC3F74FE371E7461A9E3C6AE0E346F1CB9B41BCE46BCF4E7BA3A5DD121535DAA86BD14673873E26A73DF0E851155FAB12333381A8A834447DBD29CE0C79E0EC72241EDC135EE33C5FFB0A7EFC61109736E23031E386CE5E0BB47559F23901985F08456B9B039C3DF64251E7CFD0B7FA14EE81C7E1152C86D8641504454923344202037DAEA2E74F4DB8A2AE4E1B19D972F00EDC07579FBD884C9040759D0D7AFA7C51506A02DF7009A4E76AA2ABD7090383AE58588C454D930DCC1DBF8389FDB7088B9542598D19CE9FCFC1DA5A0E2A6BEC1095AC89842C6384C46862CB859B2F70FDC10FB8F1F0056E3C78827B4F9FE2F18B67B8F5E801AEDEBB8347DF3FC6BD270F70E5EE4D6CDEBA8EF39B1B38B7BE8ACBD737288C555CBCB440716D50649B78FDD33A1E3C5EC4DDFB53B8797B0C0B677BD13DD88A5E8AAABDB7014535C528AB2F456D7B252A5B8AD1D25D8EA5732D58596BA6201B3032568EEEBE3CB47664A0A139111D9DE9144819D6D78AB071211FD7AE15E3C9D34A8AAA5E34E02201FC32C1C11CC6F56BE5B879A304AF5E35E1871F5AF1F076192E5E48E34A67E0EC5232055AC9C78E515C8B7CDE327F9EC3C37BCD98998CC2DC7424CEAFC4E1F9E3468A6088AFDD85373FB6E0A71FAAF1E6A73691387F7DC3F7A2307FFD651A0FEED7E3DE9D72FCFC334543910BF73D7ED88CAB171371E77A320559C7F719E5FD55F8F14524EEDC8CC7C3BB557CAD592E93B87BAB008BF34158DF48C6858D685CB99684D5F3519C28D15C87425CB99C8FF1715F0C535C13633E989E0EC6F0882FEEDF1FA0C83729EC558AB7099736133829AD50D3A04501D8626AC61FE7D712B0B21C8F846445D8B9EFA4A8C4119FA68838FE9E92A98990281904069FC2E47820B74F0CB76B30DFD7176D9DC6C82B524174BC0442A2A590536A80E21A73C4A5ABC2D5FF04C2E365D1D2E980F66E47747439A2B6C51A2979EA48CE5343599D01C7D9111B1B29387B361E0D2DF6884C52454C8A06523375B165F1CA330842DBBCFD0C57EE3DC3B57B8FF9F35DAC5CBE8AF357AEE2DADD5BB8F5E00E0576036B57AF62E9C21A2E5C5AC7FD8757F0E4F925DC7B780ECFBEBFC88D7D4534C35EFFB4861F7F5CC68B57F3B87263147367BB70F67C2F1DAC09F99539482FCA42616D218AEAF251DE5888D69E5274F6176264BC14E39365E81FC8431B5DACAB3B0367067339838B71F776056E71602E5DCAC43A85736E399983514A710D52002378F17D176E5D2FE54AA6E3CE9D223C7A50C581AC12B9D8CA4A1A56B93C7DDCCC419FE2E0AF70A0CEF176193FBE1AC1CDEB15B8B0968DC58578BA64DD6F42FA7546E44EAF5F56E3E5B3123EB716AF7F18E7F305712EF3F59BF9994AF0C3AB3EFCF47A106F5E0FE3F1BD725C5E8FA47023795F06DFA39E8F2DA01BC6E2D9C31CDCBF534A971CA00BB6E2EC823FE666BD71F55A3AC59A41F72AC285F5048C8EFAF0B3A4731DB3313E1188D6563A5D85017272D55058A48DCB97EBB99DCF727D7BF9B9B370F56A2666177C5159AF8AF26A1DD437997290A339E1B2D0DA6E4317D2A6004D51576F89D23223A4A5ABC3DBFF381C3D0EA1B8C498EF15C9C991C6A488A2708C9093AF88987819B85354B119AA48CED74262962ABC434E2038568263E58CFE616F91A3F50D79A17BC8072D3D8EA86B35C399510F5CB99ACD250BD37321C829D643569E0E06FA3DB16566F319162F3FC5B9CB8FE956CF71E3D1335CBE738FE2BA83BB8FEFD1E56E302AAFE2ECE625CCAC9EC7F4B9559CDB5CC78D3B1B78F8641D8F9FAEE187D71B1C986B5C2EE3A79FCEE3D9B3193C7C3C836BB7C6B1B6D98F8D2B03585EED40D740399A3ACB50589587D4C234D192579181E2EA5434B7A673C6967029C6F85801E6668AB1B65A811BD7AA298626BC7859CB8D9F89D19104B4B58460663A9D8E36C6011DC3E347CD74BB1C4C4F45E22285F6F0019DE66EBD68B08686C330311147F135E3FBE77D14C628DD644614693FFF3481E7CF3AF0F4693BA3351FB76F957110F93746F1AB97DD78F6A814DF3F29C2BDDB7914550307A49FC2E6DF5EF4F3BD3BF9981EBEFF10FECD68FCE1651B1EDFCFC0CBE749741ABECE9B527EEE243C79184DC1A7F0B6064F28CE0BAB89181EB4C7D0800D274B282747129D2E190B144B57B70D8687BDB0BE9ECA758FA6CBBAA1B84803C9C90A686BB3C7D52B4578F1BC076BE753D1D9658BC5C5184C4F06A0BA421BD17162A8AAD3C4DCBC173637A2D0D36D89EA1A438C8EFBA3BDC30185857A888993453C1D2D3C561691B1D2989DF3A12B6750BCD1A86DD04550F861D8BAEE8389ED6E7806492121530B6D3D2E68EAB04555A3199D3284A28EC0E0B02723DA15C35321985B8E42DF195774F53B728C6371E3560E96CF45A3ACDA88EF6F8A7314FD96F10BCF30B5F104F3171F62FDC6235CBFFF840CF6043FFCFC3D7E7AF38242BB8BA58D8B985EB980F1A5F3983CBBCADF2F3036CFE1CAF5B314D90A7EFAF90207E60A0768132F5F2EE0D1931946E63CD62E8E627CBA150BCB6D58BBD08E736B1D98596C46635719D20AD31193918094FC24649524A2B2368E2B5BCD99CEC17AD846A6EA15CDD89F7EEAA22B74F23D5AC9356D74B47EC64139D6CF57E1E7D7F31CE4293A4B3B07251F0BF3499C95E5144D1B2E5F2AC5D8781C2AEB9C51506ECB288EA7000BE8009514A5F01EEDB87FB781C22AA7587A45A27978BF965139C9F7EC275F15F231A57C8F26DE16D121A2C861197C4C1345D6CBC751AC3F93D37E15627B86EBDE80E74F28B2EFB3442EF6CB9B060AA280824FE6E7AEA2B8CF50A84D98E5408D8FB963712E081B6B749DAB6964AD028A2D1EA31314DF8823CEAD72B29027E7677DD044779AE173EEDC29E1676CC1F3E70D989AF44561891A85E784E11E5FD49598212B4B91036F829E7E0BD4D668212F5701F9799A8CD9000ACD13F985EA080E3B4907D3474E811EECDDF621235799C2B6A5C00C109B280E57BF3D30B0FC860C278648F25B419509C6E7833134E94D73B0E1E7F3276307A36FC4131D7D6E68E870620A79A2A9DD0ED54DE67C9C2F36AFD05D97A2D1D46A259A00F7EF9660CBE4FA7308913977F9111637EF53680FC9604F44027BFDF373DC79748FA2DAC4EC850D4C5268A3F3CB04FF7338BB7E16B7EFAF924B2E7123DFE072133FBF59A5E3CCE1D50F2B5CD671F1CA2486C65A30B3D08AB5F52E8AAC9DA5A089B0DAC20F5789D2EA0CD4B766A3A12D9D6C9145D8EEE4609DE1324C3798FD6DF03888BFFCDAC7F8EAE6000EE0FB1773B87DB393B3BF05BFFC3CCFC72DD2A18668FD02BBE571A508E077EA30361683AE9E20D434BA23BFD41635F52EE49C3016890CBA5C2E85564C97CCA4C8F2C9716D1CBC163EB79A82EB1715807B774BE9C855140BFFF6B48AB11D417187D32552286A81DD041EECE1676CE3E7A8A38309B113CDCF96445CA8E16BB6E2C18332DCBA9DCED71BFC2D661F7533E20A71F94A16459C85E78CC9470FCA299E263C7B9243618561FD620A1F9347800F431B076A60C05DF4199F3FAB25EC77725D9B3981B288148E48CFD44056BA165212541112218EAA5A4334B758202F5F031111A7E1E2BA07D5F53A1899B640459D0EC1FE381253C85D718A30B4DA066BD7BD088B23A3454AC027EC185C030EC233F80451C60C0DDDF668ED73C0D86C00FA47DD915BAA89946C651455E8A3B1DD1683630168687747489C0252F3B4D0CCD89CA56BDDB95B86F31B99E81970633AA4918F3BB165EAC2636CDC7D890BB79E626EE30EC5741BEBD7EFE2FE93FB14DB3DFE7C8DA2BA84B9B50D4C2DAF6272710973E7CEE2DCC6321E3F5BE5C613447687CB6D6EDC158A8070FBCB1A1BE70AD62F0DB36576B3248CE1DE8339DCBC3582EB3706FF5F3138BFCE98BADC8A73E76B08FEC5DCE06DFC50DD8CA47E11A0FFF2EB79DE929F08EC3FF2FEFB771BC91BF574954C3A402585479191AD5E7E3F88AB978BE80665B873AB1A57AEE4F331895C5208C299B8B8598DB9852C3A4824C512875B3705A1E4521439145A368591C7480DA413C6E2E1C346F2D7A8A84CBCFEA99D93A805DF13E42F5C48E263FCB1BC144F8EAAC44F6CA93FFFD8C872C038BD934651F86262D20B231C900B04EA9BD7D328D44C0AF5B7C7FEFACB3CD76D9482EAC363329ED04C05277CF2A89ABC46C13D2DE13A0A0D7692EF7D06F7EE958A26C49D3B4D9C007D6CD7D578FCA090BF17B20015B2E505C327541CBA669F41CBF84B98D9EF4226D9AD9BE2A86FB04744B42AF40DBF81A7DFD728AD51407296026C9CF7C2CA7127BC03C460E9B80B46D63BE0E87D14CE7E47E1117C1CC1D19248CDD1445BBF1379CB99B70EE81FF342D7A01B320AD4D814C5283243F258000DA68126528EEC120BB2B6052618A3EB977370E94A1E56D79331331F229A642F5FB612FC2F3F2187BDC4D50764B3CD3B183F771D33E7AF63EDCA4DAC5EBE8ED9F397303CBF864946E5F2DA795CBD751137EE5E2267D1C9EE2D8A5CEBA79F37280881C756B92117F1E3EB45DCB93742C76A623CB6E1EA0D4164F3B87D7714B7EF9CA18B4C53F1A3B878B91D97AF09515ACF012AC0B97302DC97B0C2D329D8FE7EF97515FFFE6505BFFE3C8EEFC94E972F1562662A96D01C4B48EFA4B0D7E8288B748176724F2907AA9383D1C5995FCC6290C7C7D4D21D6B797F2F675505792712036702F83E14CA5D81F7CA195F09181A0A639C98A1A7C78F11D7CDD79DE72238E998C8415FBD6A20BB948B9EB7BA9ACC58CFA2B0AA08F42C050F72B034EF8FE2527D6464A921AF500B8383B6146E30A19F05E00D85F8D3009761463B1BE79B598A6E98313CCE6508D72E65E0DC923B9E3DAEE484F96D97CCAFBF08CEDD4A810FF2F9ABBC8FC9F0A2958F2960C32C249B65A18351E5E17B003AA61F72F914FE2162E81B7412715D51B1315B9D0952537451536A86FA5263444748C3CE69375CBDF7C3D9FD20CCECBE83B6E957B0713B00179F23A8A8B766DB7766C2B890BB42D04B919533462B1ACD51526386A45C5514B0843476D8617A319E06D3C171AD424DAB3B7A468231B5188D919970FEDD09638CCDCB575339399BB8CE8314D9D5A7B844E0DFBCFB04CB57EE6372ED26C656AE617EFD0605760DA30B1BE89F3E4BA5AEE2C6ED75BC7A7D154F9E5DE11B2C7121E03F9AC3CB571C6C32D9CF6F2EE0C9D3695CBB416E3ADFC646598DF1997AAC6D0CF0F75E0C8D56E2CC30C17EAA12F38BD5387FA1964B3D5B65095ADA92D0DD938CA1C1145C382F34C711919BFDFCD31807BE934269A70B96626E4E18E854F21A07804DF1F5AB493A5C13EEDEACE120F4D20D46F1EAFB160AA30E2F5E908BF8F3F367C36C9915E4045FD437BAE2CC99408AB990AE9685FEBE50D4D4B822205491EDCA824CD84E71CDF0BD1708F467793BC1D76C654C35D281EAE92E592C1D2174BD385C24E8AEAF84A2B5D91C31B1528888154766BE3CF9CF864E1949874DE57AD450288C7CD13E323654D1C4205290EB9E3EA8C6F5CD3CDC64237BFDA29693894EF6A64724B05F7FA5D87F19E132C36D30499E2BE3A448E0E4CCC4958B3158E040B65000A5A51A282CD6444FAF3313218CDBD012A11162C8CC31456B933BAEAED125D9A0677B7D101729C9581583A9ED76C86B7C40FEDA86B07805E416EB6360DC1BE3B3414C97785CBBC9755C08428F20DA7E3714541A21AB8C2580026C1BA010CF786285056B782A1CE54D8CD57E5F0C8C05A2A9C7099564C8E9B9007ECE42D1E412D6994CF60433EB0FB178F13E962F3D6064DEC504DD6C6EFD361DED1AA6CE5DC5F8E2452CD1C51E3FBDC808D9A02B11FA6FCC13FCA771F7DE0263F22A057693CA5DA5330D712634617296E299ADC1E45C2396567A30490E6BEB2E404D5316AA1BD259790BC9697538BB52CB0D938986C67836A2640CF4266064209111D2C28D3DC9F8181231D29B9F84C67706D7AF573132AB39E802BB2D504003B871B506B779DFBD5BF584EF76C65D2B07B11A77EFE4311A2B299C513A56010A8A6D91956F8E860627465F38468643D14BF7AAABB3476EAE010A0A4CF8F806BEEE390EF23A1761C7E70CDFBF054F1F9732EA5A71970E383AEE83F6360B4CB2B60F314AD2D265915FA0CAD6E5463876C3C4B43D2E5DF4637C47B36136522874440AE8E79F27F8F30A1D6E08DF93F35E50B83FFD3083373F8EE2274E885F7E6C221A082227EB51603FFED0C70944866309585808C5193A6457AF39A6275CB1C9F679F9421C6E5E4960EC93311F3770C2176078D40E49A9A7C95EEA28C833C4B9B9383C67B9B93C178DC2740D38BB1E80AACE273829FB2E54743F4562863A1ADBAC31C898EF3BE34217F2C1D2D9488E973F0B5B18CD4070271B94D41BA263D005D5E4C48A7A4B34B43AA2B8D6127955A62C00D634143734775B23B34493AF67C98256F1BFAE3C862DC32B0F30387F072367EF6086429B26934D505853ABD731BD7A83C2BB8BC5F5ABB87A73951BF93C33768D3F2F60E9DC3816CF8DF2768C8E768EEA5FC1C6A5496C5E1D623B6AE3D2C299D5C4BFB7E2ECF91E7EF85A918B75F6E613425350DF968AB1E9128AAD008D4D491CE83874B6C55160F118E8E1065C2FA44B09B121700A998603FE86D178FF5E272B7E0DC52D34BC790A6A82AED14DA155E3DAE5125CD9CC657496E0DAD55C4C8C87B2892560F56C31CE0C44A0942D3325CB04F945161CB0400C0E0452D8FE8CC9408C8E8662FD420E07FE0C37CC26DFEFA288F78422B0B9998285C5106C10CA372FA53336C358CDFD44A0BEBCC8C6C5B6B5B99185274F1AE9E4B5C480384E1E4FDCB8914E619DA17028A2D7DD2C08139CD993DC864DFFEB70BD7C8FB36C9E837874AF0A6F5E0B3B5B07F978AEF34F8364CB5C2C2D26D039C33039E6C97570E5C408E47D61B8BCC9C8677978409EFBFE292724E3F8F97326C3F908228107CAD90CA363259191AE84B63A4B8C76BBA0BEC41836D6DF415AF17D4829BF0F45CD0F111A2DC514B1C1EAB928A288372AAB7491CE68AC6FB6C0149D6D62CA1F4D2D6668E9B446E7903B0AAA4DD84EF5919CA68930168EA45CE1888531BAFBCD5052A58AD87429E4956AE1E6CD3291C05EBF6EC396C1C57BE89DBD8D81C59B985ABB8BE9B55BBFB9D7F2252E97B1B07E0BF3AB9B6C3C2B64A9553C7D769940BF8CDEE101B4F475A1A6A30D8DDD6DE81B6EC7598AEECEFD39DCB8354CB01FA465F689B86B69A58D33A306C3E314597F1E4A2A13515012CD0F9EC1F659C4D99787AECE04918BCD4D26626C388E9C934C272821183732FE7A3828B39CAD43DC8835686D8FE67352F8B77EAEC8327EF8719ECE514B51C5619ACC766E25039313B1E8ED0DE36303D1D2EC4BC70927AFF823B3C00251893A28ABB0466FB737073049B4C3F7E24609C5DAC2F711224DD8EFB74E51084DB6128BE48DC1215F74F7B9A0A3C7812EE88AAB9B81B8752D1C0F6EA6E315A3FCCD9B0951C40AAD5800F7E7CF5BC9A78288E678DF2423AF9FAF37C25B81CF842814268E20BA29DCE07A9E5D8A234F0A6D5538AA304696ACC7F4ACBF6847F1EA0A1D9EEDADAFC7151756B371E37236362E4462733D166717038917491C1B811B4B590AB238C1F2313D1D8E3A72567CA23CDC3DF6222D490ECD145E42981414553E625C7E0259B50F446520AF509BE2F5E6C463132FD2413423348305A0A9D5063DFDCE6869B5E4787B300E5D9090AB8B840C1DA4E5192332451B8555DC8E83C26E0C53D434E8A2AA51171D5D5614590E4DA09B9853892D6796EE6170E90E8657D82CD92E97376F62F9E2752CAC5DC5F4CA0666572E11EAD6186D2BACDDAB6C3D9B585899475D47072A5B5A51D1D28482DA1A5A651D596C92CC769E0D7389D63D41C81FA0BB517CABC221A322CEB0020C0CE5A3AD2B0B8565D128AE88E14A087BF54B313F9D85F9A9549C5F49C3D45434592904DD5DC1B8702E1B572F1591C34A194399648458E4977BA0A0D489F71590B92638680B74922A46983FFAFBC3D03F184177F4E3FBC5A3B73F0EE5959EACFCFE6869F74362BA316293F5B831CD31391EC5C12AC4C5F54ADE16891AEBCBEFD96C7F59A22016B981FA08D9251CBC4636A54EBA591A06471CE9C87638BBE0862B6B817871A708BFB20D0B879684E789DA3097DFCA83C0768BA2BFFD4A36F9E9F5198A47D8E5C1C7D22905777EF56A00572E17B0F9522CFC2C4F9F0E8BF6155EBF9E8F8DF51C0AA68CAE198E2932D319B2D0E020A3BAD91A65422C359AA1A9C982DBC90DB3D3D1181F0FA01994F3B336D37DE9E41341A8AFB74074CC490487EE476BBD2EAA0BD5616EF2158E4BBE033199BFB2997E0D57827F5CA2126228C8E08853888A5340208B44558D015B623063D40DC575264829D04564BA2ED29804AD3D9EE839E383D9C5542CB0C54FCEF87271C7F2721056960271FB460651A1936E5B8A2D136BF731B1F6903179076BD7EFE0EA9D1BB879EF3AEE3EBC864BD72E6171751D9384FEE5D565BAD332DBCD599C5B9BC4D0E41934740A426B466D7B1DCE5DE8A6B8A6190FB4FF1F96F1F0F13456D73A09908D8CCD6AACAC1613080BB04C009F98624412F4B30AC3505C1689F9390A6D3603B3C329981F4DC2F8682CC512CE0615C88D1787C585048C8E85A2B38FFCD4E68BCC225B94D538617E219E1156CC76D9879BD76AD0C9C75756B9A2A2C683106F8BF6AE10F4F547A3A0C805B9858EC82B714464820E37A6265232F4D0DAEA853E827F57773086CE8461612E99ADB399AD8F93E5FB1191F02EAE67887669BC79738E8DB2838248C4E4B4371BA9B308FA9FDCCCC3CF2C17BFD0A17E3BF02E70DC590A49D80D32C0E70A87BE0477140E980F8B5AE98F3FFC16FFC2CEE4DBB76AC89855989F8FE144A04BD28D37D6D9B0F9397EA243BFE4E7787C5F68B174A9CBE918233B9511C263A24F22314E0A61A1E2484CD024171A22264E8DEB1287294EC6A1213F74B6DBA1A5C58262D140739B2A5FDB045D6DFA7073D90545B5CF2126F51EC4A4FF061D936F60CDE6E9177C02612C0649490A747A5D4CCC7873CC73396EC1C8AB304248A20AA2D2F491596A8DA1091AC04622E63806832341985FF2C7CAB920AE4B3A195168DFB964F4364EFE326C3977E321962E3F1009ECE6C33B78F4FC269E3CBF8147CFAE63F3DA3A2616CFA17F6A81429B26E8CFE2EE83395CBF3587B3145AEF702FEADA9B3138D6C9C639859F7E9E629C72433E18660118608B6C64A364BBBCD0845BB76B08D5F56C888D583E57868EDE34D4B7A4A26F208751918DA5E9140CB5C6A2AF8ED1D41B4FB748E04689658B4BE460A7E3D26606FA07029059688ED43C1342761899A74474007CED7C2ED6CE15A2B1C19FD06B41B0F767148653A02904FB50C4261AC3D94B11DEC1AA9CB5D2F00992837FA80A172584C56AD3DD4C5155E58CEE4E3F168454BA5733E3B381CF0D6154A58A4A87B01BE1C5B33E3CBE57C2880DA0405DB0BE9689E7C2D915C2FE2DF294B0CB4388C7373F8F5318652C27AD742FDEFF8B703CF43C5DB71BF76F17E2D9A35AC27F17DB700B270739F2722D666662290A6B8C8F7833B67B29C84991607F611B15ED98FE992DFA7639A332091BAB11E862A3AD2830445686161293751114A6026DE3AFE0E471128EAE47E0E979081E9E07E962A7915720476C50C5C8882127A132B2B3B4E88081F0F153C1E1137F86A2FAC7D0D0FB1CE191E2747D6D517959DB08C3C6660CD93A9A86608B842C1D7887CBC1374A19F1398668EC74C1D44C101A5BED3826DA1C0F279C1971C1EAF9345CBE94878B9BD91CEB5A3A5B01B65C7FF01497EF3CC4AD87F7F0F8D95D3CFDFE3645760BD7EF5EC1D4D20A7A2696D03B3E87A98509F2D5146EDF9DA68026B1B23681E1C93E368E2E4C2F0DE1D50FE7E8624BB8FFE00C2E5CEAC4D45C03DA3A8B99D76C9833756C2C2538BF56CEF8E4065D28414B571A5ABBB2E94695B87AB91CCB64B0AEBA087454C6A0B3398EB91E87C133D11CF444DCB955CA0D5F4DFB0FA38399A39671317886B6BC928EC9C968740851D81C8CAC6C67F887E8B1253A636C24892E5441878A4376813D6C9CA4A1AAB71B0E9EA71012A3452E334548941EC2A2F5119F648ACA4A573A61008A8BED909B678FB44C1B0404CB93F132E84A4B14C91AC5B24871F4888AC0EC4C24C551449712CEB698A6C80629C461FE3C44F76AC683BBF9142505F833A394B0FF3385FAE07E056E5FCFE2922312EBF70FABC97659A272D2D3E38571C6DB03DE2F2A3AE4B2DFE2563828BF20DAED21B8E0DD9B9978703B932E9A889B571B71E70E27EDD95464E71BC1DA652F9CBC0E42CBE47348C8FF056A3A1FC0D26E1B5C3D76232CFC10A2620E2032E6186A6B2CB92DE3904E7470F739C9F59482A6EE9708A293F5F5BBE0CAB564DC7F98871B9C10FDC33E047C49B8049C846BB0345C83A41095AE839C72B64A325B659D19F28AF5515E63C25432407583033ABA0328BA281607818DD3B0E5F2FD27D8B8790F0F9EDCC7C327B719953770E5C635CCAFADA37B744E24B0B1F93966EF041D6C4AC45BB7EFCDD28D863034DE83F60142FD7C1F5EBC5CE146614C3E1916896C6EB90967C6AAD0D65388FAF65CD4B6A4A1AE3985E09F8B8131E1EC8B0C5434C4F203A57216676062280ECD1561A8C80F457E5E200A4B02515B1784BEDE68026E092DB892AE168F91516FBA9F13AA6B6D45BB24F28B1C91956B435075A22339C12B500F9E7E6AC82F14765364328242915B620FCF200598D91DA5B034C81CBA484DB7466A9A25D2332C292C07545678A0A13E98A06C0C7DF37D50D0FC0AA6D687C9396CB9AF85F3C37E63AD9F5F8FE2C9C34ADCB95984E7CF5AF83B41FE753FBE7F56879F7FECA320CE8880F7D5F7F578FA50389FAC0E8F1F5630FE8AD880E3716E39043313FE5CA7445C5A4FC228813A275F0D69596A181B0BC7AB179D7C8D3EBE5717DF5378AD41E2470F23B6170F19995736A3D8BCA3588A0AC98C03FC0CBD74F3146E0F0B04848921345606766EFBA169F0091CDDBE4350E8211414EA705D15E1ECFA2D9CDD77233B5B0D0D4D96DC0E32A2A658506C0C5BE73D70F4DC8B9A46530C8FB933FED8B6C9C2AD14BF47D011D8FB9E803305E6E87F9A423B8550466771B53963D8015575364867510889918157F0690444281085AC79BF1FBA99585B1644FBC66EE1E683BBB8F7F0062EDFB88285F317E960CB98585AC2FAE5B3D8BCBE840B9BD38CC415CED675BCFAF11C996B49746C7279B58FEE364166398B37E48EC78F0771FD66075D7184D13AC8BF576364AA0C67C6ABD0DC958F82AA443690643475A65164F1C82B0BE346F6A7F507A23A371899C93E84504F24A57BB2F5F8A2AD2D9AD59D6D73218BB04DC89C8FA47BF1C377BBA1A4CC1611B17A484836A7FBB8A1A884609FE64CB630805F9006E292CC1012A9819068157805493336B519174E8C550F5455BA70635B916F1C515BED8CD4543A5BB8218CAC8E4042E1331C97FE00924A9FA2883CF798D0FF8283F9E32BBACB2F3314552F84B32E5E116CEF3FA8A563A6E0FCB9383A5B0E1B6233FFD6C5682BC13D36AC678F2A71F34A1A1666BDC8950E8C7C7B74B3294E10D2CF2EC7A291B157C3D93F3A9EC6882EA2383B18CF5DDCCE3DA2E5A7D79D6CCFC281F12AB65036E1252FAC9E0DC6E6C554224436F92B085D7CBDA6660744B341BAF8EE87BED5E750D5790F2E1EDB919E2D8EBE417B9621071497AAD0B50E528CC7111E258180D01368EBB06753F7416A962A82A3C5290E75C4A49CE66B49A0ACD60EC1B1D2B0F7DA07278ACC334412EE21A7E1E22F06BF4879145458927B3D515A6981D0183918DBEC8495F321583A1DA4D04FC12B4017AD9D45D832B67A1F67966E61F5CA2DDCB87B1D576F5FC3ECF975F48C2FE2DCA5653C7D7101DFBFDAC4A3271728AE0DFCFCE62257FA1CF9EB3C9E3D5F267F8DF2F75946E5025EBC9AC0CD5B7D74C2763C7E3244210EB271B6E1E2A5668AB513A3D3B5ACBC49C8288C407D6B0A9A29B4B29A68B63D0F24C5BBA3323714D919818888F3405C8A07E1DC836E16C2D61888F5F305B8B89687E5A558CC4D0712D223580E7C51586889EA4A778C0EA5E2ECD9322C2E55D2EDF2B8E21EF00F538595D371387B9E207FC92085D0DADAE285A17E16829E600A3884AF932B8AD4B0082D0AEC28E434B741496727D40D764245773B422314C960B198998C656B4AE3A00B675D084DB11F0FEF5753587CFE48004A2ACC50C54199E4E304465C9C4FC0C65A221EDD17CE87CBE3FD3EA8AAD6471D9DA2ABC701E7573344C762D7C83017D68B28CE41BC7CD14D410BEED7CCF798E4C4EDA68B3550CC64D96BF19C60EE14A61F96170319ADBEE45D0FBA8E27F9D59F8EED86C8785918587D043DCB8FE11DC8788C3B86D26A79162DE16840388B58241BB670B6EA51D8B9EE8285FD76D4D599B3F97BA1B85C0F31C9B28849928747C051D87A307AFD8EC1CA751FCC1D77C13D481C3E11D2F00A3B0DEF500904C7A8B0809971FCEC9091AFCFA438090BE77DF009E1630225E0EAAB42E1066164A253D8197B1F9D333730307F0D4B1BD744272A4EAE6CA0676C01CBEB4B78FE72033FFC7409AF5E5FA4D82EB0752EE3CEFD293C7F318F070F856390C384FA4142622F39AD8BCED58699A55A2CAE34619E91B972BE1997AFB6E1EA4DFE7EB60A157509A8AC2773F565A167281715F5B11494271DC915A9293E888AF76003F4A6F0BC28324F0CF4331E386385B3569F3F6DC0A54B69589C0DC6EC54180607FC2916369B992C3C7930C0E8E2E77A3EC47656CD8D1E40175345609822D9431F31B11AA2A5B1DE0D6383A138B798C1C86922EFB5B28925F2713A30B71767A5DF0F3D8B031C84E32C096270F5627B4BD6163DB7AAC284AF5FCF2863C9797D0637AEE58B4E8C6CE6007B7110FC38086959868C2E179457D8B0E9B9B31C24E1EEAD5C5CBD44E6ECB62306A8636CC20F57AE14D219FBF1F4491379B35E14874F1ED5B174D0FD9E74F33DE6F1D34FBDA2F3E89E3D2D2780B369CF799161FDD0D3EB80A25203F2900966E642B9DDB3D1DB1F88F0E813B072F8822E2586B26A3D74F7D932F6FCB878E3C6CD5C8AB81DB76EE5B37DEBC0D4E60B286AFD0DAE6EFB909CA68CA804190AE5289C7D0ED18DF6C0D9EF301C7D8F5264FB4542730F16877FAC3C7C63A411102D87E0382584C42B2120520E8E3EC728C4BD70F0388ECC5C03A4646A233649382A5080F1E93E6C1958B88B9E999BE89ABA46715DC7ECEA654C09FBC7CEADE3E2B50DBAD74DFCFCCB4DBC7E7319CF5E9DC7A5EB13D8B83C48861B621C0E33B77B3133CF811A6BC2D8541BC6A69B58086A188FD5E81EAC445B5F31D55C8C9B77182B572BD1D143F7AA8D42435B22DA7B5251561783E44C3FBA972BFC23ECE0176E8F88784FA4647BA3BADE4F74E2E18F2FEAF0D3CB7ABC785E836B5733304B910D0F07B0FD05E0D245A11474915D6609C73304E7160C8F4412848D11C4365453EB443106202BD310D1D19A28C8B3647B0DC7FD3BDDE4A8213A51358A4BEC580C4EC3D0F204EBFC51B6B4FD1417F92652169EDE9CB501EA68AA0E440763F6C9C35AD161A0D73F70C06E64E3DAE56254336EC3A21451C06A9F5F6286D27233A4666872BD34313D1D8F9BD7F3F1EC6129DD2D109D3D36B84EF8BF43777BFAB486624BA3ABC5E2DA954CCCCF856360D007972F9789CEA57BFEAC86222CA61B66301E83D9DC4259A2BC5154A9C36D258EF078490C703B5CBC5C818585646285237A5B2C313914C0ED5484278F9BF91AAD9CE4E96CF8E178F0A0068F1ED56364DC1FC9E9F214D35668997E06239B6F6168B30D1AC69F42D7EC2BD8BB1F80A3F71158BB1D80B5E7610446292131C70809B9FA084B5115B54C8F404667E0490492C33C79AB67B903168E071093A88AB86435C4A79920A728880E998C2DBD73771897F74491B970F116D6AEDCC08DFBD7F1F4E51DBCFCF12E7EFAE52E5DEC069EBDDCC4E3E72BB87D9F2DF3DA002EDDECC1B5BB7DA2335F2766DB3031DD49E73AC3486C47D7992AB4F496A3BAA50069C50928AD4BC4B59BCDB87BBF47B453B6A43A12752DF1A86F8B4741651852B30368F56EB47127517466E4F8A1A93D04F38B51B871238F9C5289E70FF339A02964197F728D0FCE9C09C5E2423A1E3F6AC1CBE7C2B96683140D9BDF46019BA63B9BA601615797601D86A181209609725B0E01354DD88119CA99DD875B373B19BFD9A8A8B4A5A824A0637408DA4647606279189D6DFE18ED4B467F433A469A72D05B998AAED2447496D3858BA3D157198DF6121F9C9DCCC44057A86817484D9D2B21DA0CF90506484E51437BB7376EDEA8C1BD7B555C872A3CBC978DBB7752E9583974E63C3C7B5C880B17C2D8BAC3B0C808ACAC334466811A0ACA743030E441A017BE1F904AC78D63A30DC7D9D550740DB0FD96ABC2D67327BCC30E103BECB0B09C48A1C6E3DAC5385CE7739E3F28E2A4138E1E4CB1B48C10577258966CD8F66D30B7108985A51034B59A2232F134DD6A3F2396626323D531F90C8565A68479678E85220CED76C1CEF32845ED8A9A165FA4159A21285611AE7E2761E6B01B968C4701F683A36560E37608BA967CBCDB1132F12994949B2335D3188EEECAD8D23D7B0723E71E60EAC25D5CBB77170F9FDDC68B1F6FE0975FEFD2196E312EAFE0FAEDF358B9304DD01F276F0D63E16C3766163BB0C99F6FDC9BC2C69533A2E679F3DE22CE9EEFC7C048034A1B0B9055928AD8AC286415C773E584B32E5AD133988BF2BA689193D534C7223DCF0F19796C942531C8CA0B435E4908EB30A3702886AC924CA74A672BCBC0E58D780C0F79A2AEDE1EAD1DBE74807A324D3B6E5FAFC2FA6A3A36CEE7F0E7725CBF5229DAD7D5DEEE47E748A6130A87AC82199966C8CDB6637B3465237566BC24F0B57CD1DCEC8AA9498AA4D601DAFAFB606129897A0A69B6BF060DF9B1480E764684A715BC2D75E069A686586F2B6447BAA138DE1BE5C9BE2861B457A47AA1A6C00BF5959EC8CE31416C9C320B861927482991A20697AF24D36D0B392172C95C6974518AE1AAB00B2496B01F8EDBB7D3E8540984641B32911802A38F22345E9CB1A687E9193FAC2C87F135523135E7CF09AB89B82C69B8061CA69B1D436EB13296CEF952C83978CA92F1E06E015EBF12BE34D381572FFBE89A95181C0DE4A43645FF90039DCF820C692B3A08DED26585BC7215B4F5B8A2B1DD136555E6D864FCCFCEC4B285CB4355FF13B8079C64DA38A1B8964913A9402490871BEFD3B7DE061DCBAD8CD283B0713F02237B2286BB18EC1999BE41A739712D4498E2E0AA802DED93B731B47C1FF39B7771F5FE4D3C7A719BB1780B2F5F5F27C85FA6735DC4F2DA02A69627B0706E1CB374ABA189768CD3BD36AF9FC1ED0793B8757704DF93D19E7FBF84CDCB677066AC01D9651948C84940526E12A252C3D95AA2505C118BA2F208C268149A3A52E964C9C82D11841589EA860CC27A02375A30CAABC308B3F174AA64CCCD26606A8AD03B9780BE7E3FD662472CAFE4E1C58B093C7C3880D1D1440AC50B0D64ADD1E1488AB2921C5486B1B1785C58CD119DCED3D244216759B03D6AC1C95D0E9E7ECA888CD185BB8F3C42B8D1464783D0D9E9033F4F4D54E5C72133D21B810E6608743483BB850E3C2C7461A7A30427233518C89F849B9926ECF5E411EE668260273D84BAE823C2DD00599174CA4853941558D1A192D9B49B71612385DC14404165715D0245C71A9796FCE9242628AF3420C0FB93ED72293A72DBD56CBA77309ADA2C10972A45773F4D50B7177DA944F8724643BB35DD440DC979BA8849E5676D32C5C8B42539399E8C5C4E41571165D258BACAF0E4691966E73979F898FC4A7DD139FAF3CB01DC769EB8C44478FEAC932C5D8A59329BF019E71713D1D7E7CE969CC4B2650F6BBA94AEF937E4ADA34866544625EB53FCAA884CD68007215F45FF23689B6F85A5CB11689AED80A2FE178C4F29960629BA991823D300DE016A3026826C6999B885AEB9DB9858BB8595AB3770F1CE0DACDFBC860B5737199D6B58BA708EE25AC2E2DA3C563616E85413B8B0398ACDABC364AC7E5CB939803BF784EF4E4EE3C1A309CC2EB630260B91989F84C48214A415A5212A2512C1F1FE0889A3E5E686104A63397B32D1DA9D89D2DA18149447A2B82A8E561D81BCE22094550671B6FD26B295B36918198E6601884073AB1779228EE26A117D077366260795D51E64206702B523F20B1DE85E85A283E57DBD51A2C34C4D8D9EE8EE0A4273930F32322DC9584A5CF1C3847AD67D6F6978789F4266A60E792B1A49418E88F3758097951E6C7414A0277D92E252818EF4713818A9C040591CB6FA6A30D7605DE7CF363AD2B0D393810317274339F8582921D85903111E5A68ADF3641427616C2498AD34151B64CBB9A9080CF4B8A3B1D106D189721491321A9A9D303911858D8D0C4E9C7ABC7CD5866BD74A30331BC2469884ABD773981C31A86EB642429622324A0C90596A8AEE33894C9826DC7B98CB725080BB8C62C1F56A1883BDC3EEE81F764342B634E2B3959052A08DC45C7574B0300867ED7EFFBC0DBFFC3281972F85A2C6CF34E282DE0137F4F4398ABE86979EA5017BB2981599CCDC652F3C8325E14B0E8B66014A2397B9FA1CA6CB7D0C2B9743743715983A1D86ACCE676C9747E0E2738A25E008C546C74DB241599939B6744CDF61BBBC83FEC59B183F7F03936B5731B47401BDD3CBE89B5AC4E0F40286E6E731717686825B64B35C61195865D35CC48D3BC3B876AB1F8F9E8C526453B872AD0F9DFDA5C8ABCCE686A093E526724513119E148EA0F820F846F922223900F9A591A86F49662CC6209BCE95551884843477A4E5316E0A7D50DB184A5B8FA483096754A411DCC3C858D648483541576F98687FD219FE3D2E419F4D4A87D96FC656674BD827E40F26D0BD4A28B03072981D8A0A6C31391E8F99C904B4B705B149D19D7C651114A6062FDE76B4D9E1AC70766C7D2EB2423CE16BC58D68AA017D3936263569E8CA1C87A6C421E82B9C80B1CA69B89AE9C0465B112EC61AB0D49080B795163CCD55E065AA0C5B0D3198A91E8089EA7EF8D82BC1C3FE347AC976372F95E2EA462EAE5CC8C2E8195F6466E9208D0D2C2A5E95355F178545866CB8EE84FC0A220AB727E17C63330D1B97527189C5606A3E9813F7B773B56A5BCDD149412C9C15A2583895BB92719B2E3A0F2CAF541B2189A7109341974D91806FE431F847B3F5468973BBCB514C3E8CD54A91B8DEBC99C2B3675DE8197041124B407195211146D8EFE5C0CFA48ED8543524A4B355A76A21999F312C4E0189FC3DA7D004BE6152A2B2E0EC2B8E60B66E5B4FAEB7C35E0484C988C0DF3BF0142262B5D0CD892E9CC5BB65E0EC6374CDDE45EFE22D0CAFDCA0C02EA37BE21CDA4716D03D2E886C19FD53B3E89998C4F4D9693C7C728E1BE23C7E78BD2472B1CD6B1DB8F76080603F80E1A96A14566720BF2A073915599C3D098848894270620802E20211181F80D88C50543424A0B123850E168AA42C5F72881BFCC22C11146D89C40C5754D606A3A32B0C5D3DC2E9398C09B6CCF44C2756742354547BA2B32B04C9194670F192E60A2970C61820AFD0156DEDE1E8EB89404F4F1499C00B9919D6C8CBB3417F4F301D259A0CE64920376454EA202C521B3EBE0AC84AB54675461042ED4C10666F84401B3D386AC9C1DD40056EC6046C6D5998AB4AC0D3441D91CE668876B344A49329A29CCC91E4C978F4B5455E982B72429C10EF6E020F13BA9CEA4118AB1D84A6CC4EC4061BE0C18D4ADC252F3EBE5F878DB51C46B31FBA3BBD919EA18B80504934D1CDBA7BECC8705904F63EB6C21A567F5F6E237372AB115A7B1D9926A9189B0DC7DC920FC13D10CD3D6E4C036F4E362774F55921BD488B4D8F839B22474CD1825F8C243C428EB31C1C670B3D8580A81384772BAC53B8C2379F1E3E6AC7FA4609B9CF8C9F419C63A08CD45C0D42BE2111460F95F5D6A8E3E76A6C734771A50D82631410C8B61D9DA80E9F303AB8D771DEA7CAC75BD38DED31C8C9D3D9E58CFC627D54D7591057BC70F366B1E8DCB92D7D4B0FD1397B9B4E761BC3CB37D03FBF89EEA90BE8183F4B71AD60707685B7F3E8191BC7D4E2149D4CF81ADA32BE7F3987F39B2C00CBF598596A40DF688DE8CBBA85357928ACCD654CA620B530057199B1084D0AA3C88210921448A688E66C4CE6064C62B30CE56CF14258AC23720B03C96641884B714141893F6AEB4319837E8889B34442822D45E68ADC7C178A25019393898C5C570486D39299FDC261A58A6A7FC64E165B67228A4ABD919DEB86AA4A1F7474B0C5D605506CB6080AD580AB87347CC90A7E011A880C354155069D34D41D493E764874B5407EA00BF2FCF9797C9C90E5ED80822037E406B8A22C2A00D53121684D8B456D6C30170E7452087A33A23145D79EABCCC14C693A5A13031166A7053395635016DF0E43F5634808D5C385E50CBC621C3E7B5A81E9A950F26330EAEB9D119BA08E961636D35A732C2DC7E367B6C117DF378A845452A38DDC3255D4B75BE1DC7A3A56CE27626A2600DD7D2C309972088A13BED97D0C71E9328C515D3A9D0EB22B8CB9DD8DE0117A1AB65E07E0E2BB0FE9B90A088B3F89806831B2992919311853D3A16442532467AAC127589C0292433C1D2CAD5007C5145E49B5095BAE31AAEBED90926308677F0946E72158BA1E66EB64B3743D80C24A632C2C4688BE2EB8B6964076F44305D74368C6E7CF8792331358241219978CCA4E364C4164830BD7D033BD8ECE89F3E89E3C87C19973185E388F3333CB14D90466CE8E91032671FFD1246EDE1DC3C25A17BA476A51D95682ECEA1CC6641EEA3A2B51D35E8E120AADAA311F19458914593067961F8212FCC808211462340AAAC290962F9CDFE5C58D1080CEDE4C32430692323C111E63CB0874A3E3D8C1C7CF1021A1E68849B0A3D0EC196F01181D89415D23E3A1C80E6DADFE1CA420960AA1148450AC5E080A67D50E235857049033D2285A1F7805A8C3DD4F09FAE647A06F7C144E4E0AA82F884539633C3BD81DD941EE182EC9C1F9E66AAC3755E0526319964A33314BAE1C4A8BC250761C162A72B1C465B62403CB5CD7CBCDE5B8D3598F07BD4D783CD8C2DB7ADC68AFC45821CB8E8B014C348E414B761F6C0C245059E0862B9B31B87C395CB433B5B9C59585838E5BE580C4540DB87B1D434EAE11E3A51A0FEF95919DE2C9490E58580EC1FA662236D950975642D1D16D43C4D04078A224C21224109D2AC772A54966B3137DABA8AAC586623341A4687FD649C69D2CA35047744901AFB063DCFE2AA86BB34529E3D137E8183C024FC0C4F63BB6487124E7EA20A39893B6C40889145C6492C061CAE4315991A80C6DBF8599F31EC2FE3ED141F3AE016B2C2C0488CE372BAF36E37850E8857AC4205334B59833516C981E7AD8D23A7593E07F4BB49FAC6FF612DAC757D13AB282CE313AD9E4599C995DA5932D8A9C6C66791C97AE4D62FDF20897618C2D74A2AEBB16F93545482DCA4456590ECA9A8AB914A07B4038BFBF85AE9587C8B4503A992F392100D1E901DC2874AC0C2FC4A478526404F2FC40D43425889684340F0AC218C111B6088BB22773D9229AA20B0A3387ABA732D2528D314E91095FE61D1B8DC5CA4AB6E8A8405A96131DD19A11AA07331B0518599E86878F1AB2F384C32DA6A283E681615AB0B23B0525B57D080FB04445723862DCAC911BE285C5A61ABC5E5DC2AF6717F1C3C419BC1CEDC1F3331D78D8D384DBED35B8D5568DFB5D4D78D0D58807DD8D783ED08E1F867BF07AA40F3F0CF5E08733DD7831D88E47FD0DB8D15185C1BC78A4FADA20D05E07565AA711E0A285FC7C33E416697022A9233E559B6EED4AD7E567F6394E90FE024A9A9FA0A8D09262CCC4F56B85D8B8184BD62DC5AB578D2C3AF974376F74F498A2B05C13F1E9B2DC860AA86A764047BF2FFA87BC598A3CD0DA658322BA5042B63A5BBD3C328B54E94C2C0B2C0C41715222E1A517514C85DA088E9242748A12FC858BB0C42B2029479BAFA9296AAE11498AF00E15834FB870284901369E475800C4F977159635038C4E7B607CD6039D7DF6A86EB014C5AD8BEF61AECB6104844BA0B058173DFD4E6CC716D8D23C7913834B373177E10E4696AE322657D132B4243A403EB2B0C66515BDE3331898107661CC606D731A67D7C6783B8EF973C3A8E7462FAA2F45018556DA5088FCEA2CAE54063AFA4A09AC4D68E92E202344716605233A2D8415389059EECD3AEC8AF0387766BC176F5D111AE384E0487BF8875AC1CDC788706ECC2871446C9C3DD2D29D786B0517774514E459E3C24A26EEDFAEC3A5F562AC9ECB11ED070B0AD3A7ED1BC3DE551B2656B2B0B09783839B32DC7DD4799F024CAD25F87C6558DACA415B470CA519518875B3225B9961A834173FAF9EC30FF3D37835DC8F1F877BF1D348375E0F77E2C7A10EFC30D88697FD2DF879A4073F0B7F1BEAC59BE16EFC32DC879F29AED7839D78D5DF8E17FDAD78DA4F2176D761BDBE100D49010877A2635869C0C9581E19490EF08B100EDF48B0E60BC7058FC0D6ED28742DBE858AE1D750D2FE9C316F20BAE8CAA30795B8FF3013771FA4E2DAF52CD1C55516E67D44DF466A69B3A073EB223E4D1E650DB6440F378ACF1E63E38E181CB147539B15E3CE48541492F358025806A25215E0157A9290BE17EEC1626CFA72C8C8D54265831993C58CC962C172A024125278821ADC024FC2DCF9B79DB14E7EA7E1E07B8263A78CDC723D749DB1C5AD7B1958DB484465A32D92B375447BFFADDDE9DA2EBBC8CEA7E9D6B65862E42F2D860AFBC96E616EFD2ED66FDC177D0DAE7F669DCD721533E72EE2FC95EB58D9BC8C89A565CCAE2C62657D092B176630363388D9E5114C2E8CA0B9BB99F65B8BA6AE7AB4F7D5A3A1B39C31998BE2EA14543765A1A2299B7C164FD70AE3870F40281D2D28CA1B1EC10EF00E71E46C72A5B01CE113644DB73183B3A72E1C5C35E1E4AA8E8C2C7B2E76282BF7447D6320AAABBD30321889738BC9B8BA9E8FB565C6195B634DB53B4223F4D9D6ACE0EAAB0B434B2998D9CBC2DA8936EF280D73FB53D0333B0E2DFD13D0D03D014B0B253417A7A3263906ED9909B845B108027BDAD78927EDB578D45A851774AB577DCDF871B0153F0C084B0B7F6EC74F0382A8BAF9B736FC4051BDEC6DC5733ADC736E87673DCD78DC43A7E3B6B8D65C86D5C60294735D63DCCD10EE628CBC782FD145484A2AAC9093672CDA1725A3FA2FA8196D87B2DE56C8697CCC06AD8EB959E1DBEAB1989A7527837960712915172F15907DE250D7C086DDED8096566BA4E7A83306B59045D8AEA835445BA709DBA105FA065DE86ADEA2ABED44A59D66BB97865FA4380524462E235B39EE8692CEA76C8912C8643C669618A2B8CE0A29F97A482FD427C6E88BF6E6AB1B7E0C13BBEF6064BF8BE23CCC3113A7138AA1B6D9988D3613E7D75351516747DE5344108B4646BE26720BD4D0D1698FD109771A40041EDC2D1298EC16CE5EB98F4BB7EF63E5321BE6C206C6172FE0C2D56BB8F3F01EAEDDBE81731B6B14D70ACE5D3C8BB3E7E730323984C1F17EF47066B774B7A0951BB7EB4C1B99A0890E5687A6763A5B651A4AEB3250DD9A8BECB2048A2C4424AEC0482FD1E2E863031B77535AB213BC2836E1986558BC233FAC252D5707AEDEFA484E679DCEB0454D9D7069A33036CE480C0EC662B03F0253E3F1989F4EE26D2C5A1B7D919A628DA4141B72860ECCADA561EF220BEF00658A56998EA604335B1968EB1F83A232F9C3510F676A8BD1959F86B1926CDCA7681E51340FDB6A70A7BA00B7AAF2F180EEFC7D5B3D5E7436E069671D9E75D551480DA2E50505F5BC9510DF5A8B672DB578D0588907CD5578D05A83FB1DB5B8DF5D8F9B4DE5B84B94982E4F477EA80762BDCC91E86F43C104629962191B0FE7BAA940C7E46B46E556C8AA7F024DA36F44BB01B28483CCFC5B0841BDACCA0AE7376AB1BC5A4C04F1A4E39F644356465189360A8BF418BFBAA8A7E09A841DB5390A7438758AD31BB37364CD423584279FE2F6D713B95440942C594A89C2D9056D93EDDCF6328849D3426CBA16520BF4C8D42668A240CAC857761E0760EBBE1FD66EDFC1D26D3F9CFDC461E1BC1F26F6DB9159A08281114734B34DA6E71BD251D5441776593917C5C69A26BAE0CDD99518D1D91DE778BBA567FE16962F3FC0FA4D8AECD22D4C9EBD8CD1850BD8BC79030F9F3EC4CD07B7188DC261250AECC2221629B2F1B9318CCE8EA29FD1D1D6D38A560AADBD57105B3D2B6F3957BA9475B9102D9D4568E8C8A3C52630FB8345E20A88F440709C0FDC831C99E1968C0E1B387A9BC3CDD78C1B55109A3D7F36848D93267C024D111261CA5AEC8EAA9A001497FA219F609F93275CB84D3803231C037D51A8ADF14164941142C274E1EEA9026B3B39D1A9C531B13AACE73A8C4B1518999F86B9D509585B1F4172B43B06AB8B315A9687F5966ADCA513DD6C28C7E5E20C5C2E48C3E5C2345C2BC9C2A386523C6EADC663F2D8138AE72921FF49470D9EB5D5E169730D1E3712D2EB2B71A7A614B7EB4B709B65E1766B05EEB457E326DDF01EC5B6D150828EAC3814447823918C76F35A3EAEDFC8C5C464148ACA8D61E1B0976EF691E82B6A76AE87101825836072905FB070F980DD9C987268EE88446D933745A40EFFF0A3088F154371B9AAE8227AC2E5A2844344F3672390C616295C466A74D21EF30B41847B3D32AF2212B2D4447166E54291D8EE82A1D50E8A4E990D551B51C91AF08F96827FA4249ADB9DE99CC128ADA4780B19A5C28990BCDFCC690FCC1C0F40CDE41B281A7CCCD6BA97D1C95290A682D004721F1D70662E04738B21387F218B2E578791D160D14EDD14F2E796E1E55B3877E521CE6EDEC6DCEA758C2D6E12F4CFE2FCE5ABB8FBE8016E3CB889CDAB1B58593B8BA5D505CCAD302EE7C7F8B871F48DF5A1A5AB05752D75A2A58AADACA83A1FC5B57904D202347514A2A2211DA9F9518860540A4E161CED8D90581F84C67A2232C11D9E81563062E5D73357619EEBC1D99B31E2A80D531B3578F89970669B213A89CD32C78DA5C009A1D19664377302BD074A4ABC9197EB8A98380B38BAAB12FAD561E5200F35DDE3D035E220B9C98A5CCCC0E2349434F6D3C90EC0DAF6248A32C230D35C89D9EA225CEB20A8375661A3281D17F393B09997848BB989B8949F4257CBC7E3962A3C12160A4D70AB872D9578DA5283271499B03C6AA8C4BD7AB6CC060A8C22BBD35685DBC2C2B27097EE7789421D2D4A435D4A040AD8B097A6D8509762D0DF1780F24A2B46FA3E9C90FE33D4F43F17C5917F9404629315596494E81072A21D9F352C08B30BD1A22FD0760F1A6368DC0253736E3833EAC4966D84B979E1FB913E28A954A79369A0AA5E1FC36384F21EE17BA62ADCCE0AA21DA43A66DF3092FFC9267902B9150688C950E4FB49C133E4A408DA6B1BACD878FD5150A88BFC222D0AD980F8710272DAEFE384C23B3824FD368E2BFD05AAA65BE1C7B29094C7829022CF26AA81FC3223246428232E8D91596CC57550852D1BA9B093768BF0A5DE15C6E5DCDA4D4C2C5FC5F0DC3ACE4CADD0B13671EDD64D5CBF771D976E5EC2CAC60A05368FE9A5290CCD8CE0CCF4103ACF7491C71A188BE528AC2AE65284FC8A7C667C0685964D26CB464179122D358E3326889119CCDB40CE1A37C4A532E272FCE01F660B53072DE6BF1CE3420EBA664AD022249B59ABC393F0EF1F6A0A571F6D780719C02B902BEDAD8DC8584B6466BB229AB79EBE3A6C93E42E3B050A4C05EAFA629052D88353323BA1A07A003AC662D0323A0149A53D1097DE0D49D97DC84C0CC37C6B1D16298E8BF564273AD8527A34D6B2E3B09E9380758AEC2A9DEC0105F2AAB3511499CF59709E5190DF7736E107BAF78F04FD973D2D78DE5E8FA75C1E313AEFD1156F5384379ACB718B6E768B421376854C9466A0392316F9D101E86E08C2CCB82F067ADC38498409B50FE2B27F818EE9D7F0263385C7CBD0410C29205734B6D8A0B9D503EBEBB978F8B01CD76F25617EC589B86281610A4C38C69947B8EF1D704545B5165BA73252B395E11970980E268784143904869F4242B20A72720C38614FC3C9672F5D4C960DD10875CDC29514954457E611A2349682C9C9D7E284D665EA4889CE2BD3B3D80619AD0F21AEF21E0ECBFE018764FF0C45C3AF18C34AA2180E66633575DC0927DFA3B0733F0075E31D30B0390823DBBD50D2FB128ABA9F63CBE0D275CCAEDFC1E4CA3591C846E7D731BE701E8BE7D6B071E5122EDDD8C4FA950BA24B454D2FCF626C6102FD1383E819ED47FB4017CA9B6B915D5EC84A9C83828A2291D052F3530994A928AC4843764122B9211949D9515CC258BD8369F94E8864A38C4F734750841D37AE251C3CF5616AAF4E912952640A30B15483AD93166C1CD56064210F0D7D49A8E99D26C04B2132DA0AE51581888DB7839DB32A748C08F6A63230309787BADE2948C8EFC571C91D9056DA2F12999AEE519C92DD8BC3C776E0E889EF909B1C85E9A61A2CD695E27C7521CEB3042C5264AB8CB54B0529B84E513C20B33DA7485E534CBFB241BE19EAC64F6C913F0D74E0A77E2EBC7DCDE5875E364FA282A80070794041DE6679B8DBD5805B64B44DB2DD6459161A5263901B1188AA023F0CF678A1A196F09F6F007736371BE70348E1609771D01A1ACD303EED808D8D58AC5F48131DBCBF7CA50877EF55E2CAB57446A107D273655920D4282405C4A4CA13256C5818E458A20EC2C39FEB6CF11927ED07D036FE82ADFA3B64A453883D8174446746278558A0C594714057BF13615D9B02934750B4D02CC5E0E67F8C5C2C4F773B0D7D8BED228795D5FA18A759500E49FF090724DEC169E58FF83871A28D3C0CADBF8584EA0738A9F44F48A97D8253CA5F404CF9733EFE0B0AF3133EF70BC1C984E39537317D5EB844D4152CAC5DC2D2850DF2D779AC5D6201B8B4C69F5730BFBA4811CEB0184CA28315BEA5BF134DBDEDACBFE548CC49473297BCD24256E1628A2C0D49998964841466723CF33D099939B148CE08454E7104328AFC919243888DB613892C3A51D885614F1B372394AA73C564A069204761494355E734E4354E424AE50497E350D014834F80112AAB42C8673E70F5D282BE8914744D242940C2BDA104E4540F434AF10094358FD21125A1A27908874EECC08143DB70E8C80E64C58761AEBD11E34559385B9683F38CB37339F114592CAE57E43002CBF18411FA3DD9EB25215F70AD37835DA2E5E7331D78C1A2F3A2BB09DF93E5BE278F3EEB6AC64B16871F29C6EF29BC2774B8871D8DB84AA73C5B9E83713A7B6B562232437D909BE483D4745D149799B2C8B8A3A5CD0D358D74AC0E6B0C0DBBB1180433FE7CB07A3E1AEB17D33034EA8BF1A9289C3D978AC9A95046A033CAE874A1B1E20889398DD44C5564E568B145EF84A6C98750A5B8F4CCBF8496C967643D729ECB01B4B438607E2E110343612822DCE75618238DA09F94A58EA0586978D3E1DC02C461EEB41F46363BE0E47D08D6E437239B5D5033FC12B21A1FE1A4DC5FB1E7E4DBF8EED8DB3822F92EB41999F6EE87A0C1B222AEF4118EC8FC0387B99C50FA0C622ABF09EDA4E2C79056FD185B26D76E60FEF26D2C5CBC8ED52BD7B17EFD3A9BE565AC6EAE63E3EA459C27F44FAD2C60E2EC3C4616A7D1373182FA9E763A5803CA9BEA91515C80D8343A55561AB20AD9248BF290989944C78AA7A892109B188DB4CC7832463452324344272C963586B376FBC02FCC025E01E6088F734044BC0367A21D3FB8BE282E95B525A1A829415151349A9290E5AD3245A76E280D535B15844731F7E36DE1E5AFCF985485A59D122CED15A16B7C1AEABAE2D0E0A26D700A8666E29051DC857D47B7613F4526237B0C85FC3C336C87C305E998CC4AC01223F25269166ED515E1666D216EB05DDE142D0504FA52DC6DA8601C92C1DAEB44B777E880376A8AF89C7C2E857C1EE19FA2BCD55489479DCD784277BBC798DDAC29C6424926CEF0F56B18D1A9811E488B72137D3BA8A6DE8CA2F2C3C2523446C603D0DAE5404179617E3912A35321A28BC80D3156DB7BDCD9F6CC09F166E8EAF1C1D272349657223036E68DE66633B4B7DBA1A2DC4474F92715BD8F2934B25DD03146A00C1BE2114424AAD0B15CD0D9ED8AAA3A2BD1AE8EB23A1382BB2223EE089CFD854538E3623F8CEDF7C0990CE61F2A0E47CF439CF0BBF99A9F434EFD23482AFE1DA714FE0B87A5DEC15E8A4DDDE833387A1C1689ECB4CAE7382EF711452644EAFB90D4DC0A79DDED7C3E856AB50B5B6637AE93C96E71B98EB56B3770F1266FAF5EC2F94B1B8CCB4D2C5E58A5B86671666E0ABD1458D7C820CA5B1B91575589829A0AE632E3B1201BE95C32B8A464A722312301A9792948CA4844784C081253A2E86811146008E334848FF54144921357C884B9AFC3B83425DC3B13509DE0176A095B175DCE46053A9914398D1168A4001503196818CB8A7ED7E5DF4C6C94292E65364775B87BEB2229D90EB939CE74395D9858C9B141C931264F405A611F0E9EDC8E03C7B7E3D8C9EFA0AC7C0AE514D76843157AB393D113178ED1A4085CA6A0AE3322AF303ED785C3463949188D0FC76452141629C4F374E70B851958CBCFC04A760A2613A3D01AE0856C7B33A4581A23C7C91A15BEAE184C8AC42427D9C55AB655A2C462791E4658226AE24391ECE78EFC641734345BA2A9C98A6DCE09FD83BE68EE7447499539CAEBAC599E1CC8B20E28AD36452541BCA4D2128969AA48CE54E7F39C29B208DCBC95837B770AB0BC1886EA1A2B4E664D38FB1C83BED5B774982F60E7B9178131C2D513251096A486943C3D364155D1991819052A9CE01A74300938F91D812D8562ED760806B63BC8567B38F1251147613A7A1E8601A350D3988D52EB23C8A9FD03F62E7B11E0270E7DD36FD8FEF7222C5C0AB60EE45F3A99B4C65714DBA78CD4BFE3B8E20714191933449AE23F892DCB976E62E5EA2D9CBF7613AB57AF8BBE48B276F912562F6E882E7A37BEB880C1D9190CCE4DA26772149D14594D472B055685A23A61A910417F0E67756A6E3A12E8609985C92821F867E427B06E07312A59E14BB9085F7F2B0D426CAA1B3F80156DD90C6E3E2688A2E05273BDD84C5CE966D67070D521AC4B93C1A4385358084C1579CBD6C81855D639056D1319185A2A429F0C6662250FDF007D34D607A2B1CE0F5131A614A92AD98EEEA7B21F274E7F8B63A77712FAF7404E89B3D6D90C8BD32398A493F532CE7B12233157908995D21C0A2402D501EE88B5D0878BB22C2C25C5E0A5268F745B23B4067B6288313B9D1A87DE707F64D99AC05EFA04F48EEF83D2DE1D503BB80B0E0A9248B0364699971BBA6223304941CE96E561BC380B1D74CFBCD00034E605E3DA5421AE7756A1BF3594C02E34674344256A202C4E8DC02DCF1894E7EF729CA45A440925C4252910E295919CA68E8A2A230C8F78516091E862DC26651A711BAB2280032A7C91C3DCF1008CE940D6AE07E1E27F8251280DB7A0D3700F3A05AFD0D3E42F09F84688C125E018ECBD8EB068E8223CC100664E07F8DC3D4C0779E4E7EB11438E4249F743823F1DC9FE0027ED7788E5E72A67CC060591DD1895D9E91A149D181BAB10D35FD1457790C33EC21EF13FE0A8FCFB307339010B67316C39774D689782C8845D19D770F6A210956C93EBEB047DE14C8C190CCC90C51667D03F332E72B3C69E4E9434D422B7B294D05F80ECD23CA47183A6E72593B91251DE9081EA961C369F78C42785A0B49233B989F73526B21C44223EC38FB3CC81F5D80C8161568C5A57B2823FB9CE190191C68C3D65A8E99C840645A64391195A11EE4D95A0A22B0D69153128A89F642CCA41DF4C41C460BE7EBA686D0C425D95377FD66469908696215BA5E111BAD911C8AB1C84B6D17158315223A3DC3140505F3ED38BB1EA6274A5C460B9B60C55FE1E8833D387BF8E3A8C4E1D87E29E5D90DCC1CABF6707F44F1C42A88E0A9A29B40932666BA81F8275546126710C8ABBB742EBC85E38AACAC34B5B15CE4A5208D3D3428EB32D9AA28231909680F1820C0CD2D9F343FCD093148D8D926CF4F8B9A131D30945C2C1EC388A244C169189AAF008141C480A419172F00D964434FF565D658D33837EE4B524B4773172B3D5909CAE8E9068558A47112EDEA7D8BAC5E96612B0F33AC5E8131309C3D87E172C5D0EC28C0DD6DC690F1BDF0E46E821BAD70158BA321E1D76F3BD54B8CDB56068F31D540D3E8523EF0F8F90E1E4DD0149B5F778DF67A2D73265F439B91E424ABC02B23354114ED1A6B095A653FCFE8127D8EE7742D782CF51FF147B24DFC17E997F414AFB5B2819ECA19351608B9B374467C5AE5EBB8E7397AF62E5D2252C5C58C7C4D259748FD3C1C62730B2C0C89C9D40E7309DACAD95F0584EC0CFA54008FDB9698CC074E4B2519655A7A1AA2193A249669D8E64EB0943615902EF4B46556302051887DCF248BA9727BC03ACE0E26948F83727CFB923BB58F83A9C151CDCB8D28C4407376D583B68C1C85C999C2543903D09098563B46E81B924A14911AAEA88C1C2469E0DCA1199697670765586B2FA41185B9D829B9F12AC9C2854E363B0749080ADB3027C7CCC30D6DF86D5F111ACF4B4A23F27192DF1FC3CA6068830D583A39C0CCC652461ADA20C8DE3C720B17D1BA4B67F03F353C7D0151588D58A624C66A721DA946D98F799499C84B5820C2477EFC0896F3E838BAA225CE56510A6AB810237277427C560824E39519885CAC820B44606A3D2C516192AB2288D36434DAD17BAFB599A4AECD81885E3AFA710124FC1252BC0C9EB38B79124AA2A6D44D7535B5D4D476B873702A3A4E1EA779C021222ED089475BF21E80B717708EE813274B113A25D098A7A5F40C79293C47A27F4187D066C9A668E7B61EEBC9BBF6F858AE12714D1C77CEED750D6FB8C2DF19F38ADF02E9DE95F1418194CE55D48ABFD8D1CFC25D40D84AFD0BD0F1BDB9DA82CD24311859E102B85EA0A7D7E3EE1EAD94A743D6E03C57FE1A0ECBFB09F8C7650F6233ADA27D832B37615E3AB97B1B0C198BC7E933C761D8B828BADAC626C61095DA3E314D93845368DD1F929B40FF4A2A4AE9A2C56824C427F4E4921C5958BDC922C3A5626CAAAD2198D8922718547F9D1E64339F32291941E88942C7FE49685325E85BDD381F00BB681AD13998C6D313ED581D6EFC8D66441CED282A3BB160223CC38438D60C0B854164A80C669681928C2D85C0DDA8C4E058A4E4EE528D4F54EC398CDD2DA46163AFAE21097DA09138B53F0F05385A9CD290AF4286F65C912A7111FE58347A38358A2139FEFE9C0404E1A92EDCCE1A62C0733F1E3303B21067369091CFCF453ECFBF863C8EFDD0D43B1E3303E790C837111B843B03F575A8C247373441A1AC04F5505A5E12150DCBF0F0A870EC0574B1B71166628F7F3A6A0C23091C7C8E4F699CACB44151DB0C2CF03951E8E68B0B144459C0D6A1B83313D5741DE0A424ABA363CFD4EC053B84870AC0CDCFC4FC1C6E5187C289CB068253A97025CFC2444DF0A5223702BEB6DA708BEC481537F86A4F20764D55D2C5232B0773B0925BD6F20AFFD051D681FFCC395101CAD46614AD195F6324EE93A565BF9980FA0A8F33EE4753E808CE6FB90D3FA14E2F2EFE298CC9F704CEE8FA21DB092643155A3AFA16620ECF3FA9082FE001164BDF4246964A7C9A28145A2A6DA58F4ED2C1BA7C338A9F2098EC97F8ABD52FFC01E89F7B0EFF43FB1658A021B5DB988F9F5CB384F91AD32326757D73075F61C8667E7D13D328681A9098C2FCF629C226B13ED1BAB4369630D4AEB2B51565F817236B0F2864254D6E7B005A58AF68D45278672A304902742A8F22084467BC29F119998EE4DC70A414A4E2059C209C1E1568C0C2F9454F8924384D35E74E1E4A983308A2D32C106CE9E06D03721F853609A7AD28C3C3D2EBAD062D39451380A5965E1A0379BA8DA099C96D983E3A7BFC361B1AD743F498A55095A7AC7E0E66181C06067985BABC0C3DD143D09847396942B43FD182ECC41BC9511C5A1080F7525181D3F0A3F5D1DBA990C5DEA3";
          byte[] decoded = hexStringToByteArray(b);
          ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
          //InputStream is = bais;
          
          //BufferedInputStream bis = new BufferedInputStream(b);
  
          Bitmap bmp = BitmapFactory.decodeStream(bais);
          mProfile1.setImageBitmap(bmp);
          
   */
        }
      }
    });
  }
  
  @Subscribe
  public void onMessageEvent(ProtocolEvent event) {
    loadFragment(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_PROTOCOL),
      null), ProtocolFragment.newInstance());
  }
  
  @Subscribe
  public void onMessageEvent(DocumentEvent event) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        
        // -----------------------------------------------------------------------------------------
        // BEGIN: GET DOCUMENTS
  
        Call<ArrayList<AppFileInterchangeItem>> call = App.getInstance().apiManager.getDocumentApi()
          .getDocuments(event.getMandantID(), event.getOrderNo(),
            DeviceUtils.getUniqueIMEI(getApplicationContext()));
        
        call.enqueue(new Callback<ArrayList<AppFileInterchangeItem>>() {
          @Override
          public void onResponse(Call<ArrayList<AppFileInterchangeItem>> call, Response<ArrayList<AppFileInterchangeItem>> response) {
            
            if (response.isSuccessful()) {
              if (response.body() != null) {
  
                Gson gson = new Gson();
                String raw = gson.toJson(response.body());
                
                final List<AppFileInterchangeItem> appFileInterchangeItems;
                appFileInterchangeItems = gson.fromJson(response.body().toString(), ArrayList.class);
                if (appFileInterchangeItems != null) {
                  if (appFileInterchangeItems.size() > 0) {
                    
                    mMainViewModel.getAllTasksByMandantAndOrderNo(event.getMandantID(), event.getOrderNo())
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new DisposableSingleObserver<List<Notify>>() {
                        @Override
                        public void onSuccess(List<Notify> notifies) {
                          if (notifies.size() > 0) {
                            for (int i = 0; i < notifies.size(); i++) {
                              ArrayList<String> _list = new ArrayList<>();
                              _list.add(raw);
                              notifies.get(i).setDocumentUrls(_list);
                              mMainViewModel.update(notifies.get(i));
                            }
                          }
                        }
  
                        @Override
                        public void onError(Throwable e) {
    
                        }
                      });
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append(getApplicationContext().getResources().getString(R.string.new_document_message));
                    sb.append("\n" + getApplicationContext().getResources().getString(R.string.order_no));
                    sb.append(": " + AppUtils.parseOrderNo(event.getOrderNo()));
                    
                    messageBox_Ok(getApplicationContext().getResources().getString(R.string.new_document), sb.toString());
                    
                  }
                }
              }
            }
          }
  
          @Override
          public void onFailure(Call<ArrayList<AppFileInterchangeItem>> call, Throwable t) {
    
          }
        });
  
        new RingtoneUtils().playNotificationTone();
        
        // END: GET DOCUMENTS
        // -----------------------------------------------------------------------------------------
      }
    });
  }
  
  public static byte[] hexStringToByteArray(String input) {
    int len = input.length();
    
    if (len == 0) {
      return new byte[] {};
    }
    
    byte[] data;
    int startIdx;
    if (len % 2 != 0) {
      data = new byte[(len / 2) + 1];
      data[0] = (byte) Character.digit(input.charAt(0), 16);
      startIdx = 1;
    } else {
      data = new byte[len / 2];
      startIdx = 0;
    }
    
    for (int i = startIdx; i < len; i += 2) {
      data[(i + 1) / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4)
        + Character.digit(input.charAt(i+1), 16));
    }
    return data;
  }

  //private static long back_pressed;
  @Override
  public void onBackPressed() {
    FragmentManager fragments = getSupportFragmentManager();
    Fragment mainFrag = fragments.findFragmentByTag("main");
    
    if (fragments.getBackStackEntryCount() > 1) {
      fragments.popBackStackImmediate();
    } else {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          MessageDialog.build((AppCompatActivity) MainActivity.this)
            .setStyle(DialogSettings.STYLE.STYLE_IOS)
            .setTheme(DialogSettings.THEME.LIGHT)
            .setTitle(getApplicationContext().getResources().getString(R.string.action_exit))
            .setMessage(getApplicationContext().getResources().getString(R.string.exit_message))
            .setOkButton(getApplicationContext().getResources().getString(R.string.action_quit),
              new OnDialogButtonClickListener() {
                @Override
                public boolean onClick(BaseDialog baseDialog, View v) {
                  MainActivity.super.onBackPressed();
                  finish();
                  return false;
                }
              })
            .setCancelButton(getApplicationContext().getResources().getString(R.string.action_cancel),
              new OnDialogButtonClickListener() {
                @Override
                public boolean onClick(BaseDialog baseDialog, View v) {
                  return false;
                }
              })
            .show();
        }
      });
    }
  }
  
  @Override
  protected void onStart() {
    super.onStart();
    App.eventBus.register(this);
    registerReceiver(connectivityChangeReceiver,
      new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    
    //performPendingGeofenceTask();
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    mTelephonyManager.listen(signalStrengthStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    
    ((AsapTextView)findViewById(R.id.tv_vehicle_client_name))
      .setText(TextSecurePreferences.getClientName(getBaseContext()));
    ((AsapTextView)findViewById(R.id.tv_vehicle_registration_number))
      .setText(TextSecurePreferences.getVehicleRegistrationNumber(getBaseContext()));
  }
  
  @Override
  protected void onPause() {
    super.onPause();
    mTelephonyManager.listen(signalStrengthStateListener, PhoneStateListener.LISTEN_NONE);
  }
  
  @Override
  protected void onStop() {
    super.onStop();
    
    unregisterReceiver(connectivityChangeReceiver);
    App.eventBus.unregister(this);
  }
  
  private void handleGetAllTasks(ResultOfAction resultOfAction) {
    if (resultOfAction == null) {
      mBtnGetAllTasks.setEnabled(true);
      return;
    }
    
    try {
      if (resultOfAction.getIsSuccess() && !resultOfAction.getIsException()) {
        if (resultOfAction.getAllTask() != null && resultOfAction.getAllTask().size() > 0) {
          for (int i = 0; i < resultOfAction.getAllTask().size(); i++) {
            
            TaskItem taskItem = resultOfAction.getAllTask().get(i);
            if (taskItem.getMandantId() == null) continue;
            int mandantId = taskItem.getMandantId();
            
            if (taskItem.getTaskId() == null) continue;
            int taskId = taskItem.getTaskId();
            
            mMainViewModel.getNotifyByMandantTaskId(mandantId, taskId)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .subscribe(new DisposableSingleObserver<Notify>() {
                @Override
                public void onSuccess(Notify notify) {
                  
                  CommItem commItem = new CommItem();
                  commItem = App.getInstance().gsonUtc.fromJson(notify.getData(), CommItem.class);
                  if (commItem != null) {
                    commItem.setTaskItem(taskItem);
                    
                    notify.setRead(false);
                    notify.setTaskDueFinish(taskItem.getTaskDueDateFinish());
                    notify.setData(App.getInstance().gsonUtc.toJson(commItem));
                    notify.setModifiedAt(AppUtils.getCurrentDateTime());
  
                    if (taskItem.getTaskStatus().equals(TaskStatus.PENDING)) {
                      notify.setStatus(0);
                    } else if (taskItem.getTaskStatus().equals(TaskStatus.RUNNING)) {
                      notify.setStatus(50);
                    } else if (taskItem.getTaskStatus().equals(TaskStatus.CMR)) {
                      notify.setStatus(90);
                    } else if (taskItem.getTaskStatus().equals(TaskStatus.FINISHED)) {
                      notify.setStatus(100);
                    } else if (taskItem.getTaskStatus().equals(TaskStatus.BREAK)) {
                      notify.setStatus(51);
                    }
                    
                    mMainViewModel.update(notify);
                  }
                }
  
                @Override
                public void onError(Throwable e) {
                  
                  CommItem commItem = new CommItem();
                  commItem.setTaskItem(taskItem);
                  
                  // Nicht vorhanden:
                  Notify notify = new Notify();
                  notify.setMandantId(mandantId);
                  notify.setTaskId(taskId);
                  notify.setRead(false);
                  notify.setTaskDueFinish(taskItem.getTaskDueDateFinish());
                  notify.setOrderNo(taskItem.getOrderNo());
                  notify.setCreatedAt(AppUtils.getCurrentDateTime());
                  notify.setModifiedAt(AppUtils.getCurrentDateTime());
                  notify.setData(App.getInstance().gsonUtc.toJson(commItem));
                  
                  if (taskItem.getTaskStatus().equals(TaskStatus.PENDING)) {
                    notify.setStatus(0);
                  } else if (taskItem.getTaskStatus().equals(TaskStatus.RUNNING)) {
                    notify.setStatus(50);
                  } else if (taskItem.getTaskStatus().equals(TaskStatus.CMR)) {
                    notify.setStatus(90);
                  } else if (taskItem.getTaskStatus().equals(TaskStatus.FINISHED)) {
                    notify.setStatus(100);
                  } else if (taskItem.getTaskStatus().equals(TaskStatus.BREAK)) {
                    notify.setStatus(51);
                  }
                  
                  mMainViewModel.insert(notify);
                }
              });
          }
          // Aktualisiert:
          mBtnGetAllTasks.setEnabled(true);
          messageBox_Ok(
            getApplicationContext().getResources()
              .getString(R.string.action_update),
            getApplicationContext().getResources()
              .getString(R.string.action_update_message));
        } else {
          // Kein Update vorhanden:
          mBtnGetAllTasks.setEnabled(true);
          messageBox_Ok(
            getApplicationContext().getResources()
              .getString(R.string.action_update),
            getApplicationContext().getResources()
              .getString(R.string.action_update_message));
        }
      } else if (resultOfAction.getIsException()) {
        // Exception from REST-API
        mBtnGetAllTasks.setEnabled(true);
        messageBox_Ok(getApplicationContext().getResources().getString(R.string.action_warning_notice),
          getApplicationContext().getResources().getString(R.string.action_exception_on_rest_api));
        addLog(LogLevel.FATAL, LogType.API, "GetAllTasks", resultOfAction.getText());
      } else {
        // Unknown Error
        mBtnGetAllTasks.setEnabled(true);
        addLog(LogLevel.FATAL, LogType.API, "GetAllTasks", "Unknown Error!");
      }
    } catch (Exception e) {
      mBtnGetAllTasks.setEnabled(true);
      addLog(LogLevel.FATAL, LogType.API, "GetAllTasks", e.getMessage());
    }
  }
  
  private void handleAccessToken() {
    App.getInstance().apiManager.provideAuthClient().newCall(App.getInstance().apiManager.provideAuthRequest()).enqueue(new okhttp3.Callback() {
      @Override
      public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
      }
  
      @Override
      public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
        if (response.isSuccessful()) {
          try {
            String jsonData = response.body().string().toString();
            JSONObject jobject = new JSONObject(jsonData);
            String access_token = jobject.getString("access_token");
            if (!TextUtils.isEmpty(access_token)) {
              TextSecurePreferences.setAccessToken(getApplicationContext(), access_token);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    });
  }
  /*
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
  */
  /*
  public void addGeofencesButtonHandler(View view) {
    addGeofences();
  }
  */
  /*
  @SuppressWarnings("MissingPermission")
  private void addGeofences() {
    mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
      .addOnCompleteListener(this);
  }
  */
  /*
  public void removeGeofencesButtonHandler(View view) {
    removeGeofences();
  }
  */
  /*
  @SuppressWarnings("MissingPermission")
  private void removeGeofences() {
    mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
  }
  */
  /*
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
  */
  /*
  private void performPendingGeofenceTask() {
    if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
      addGeofences();
    } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
      removeGeofences();
    }
  }
  */
  private void loadMainFragment(Fragment fragment) {
    
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.popBackStack(BACK_STACK_ROOT_TAG,
      FragmentManager.POP_BACK_STACK_INCLUSIVE);
    
    // Add the new fragment:
    fragmentManager.beginTransaction()
      .replace(R.id.main_container, fragment, "main")
      .addToBackStack(BACK_STACK_ROOT_TAG)
      .commit();
  }
  
  private void loadFragment(PageEvent pageEvent, Fragment fragment) {
    
    Bundle bundle = new Bundle();
    
    switch (pageEvent.getPageItem().pageItem) {
      case PageItemDescriptor.PAGE_TASK:
      case PageItemDescriptor.PAGE_CAMERA:
      case PageItemDescriptor.PAGE_DOCUMENT:
        bundle.putInt("oid", pageEvent.getNotify().getId());
        fragment.setArguments(bundle);
        break;
        
      case PageItemDescriptor.PAGE_MAP:
        if (mCommItem != null) mCommItem = null;
        String json = pageEvent.getNotify().getData();
        mCommItem = App.getInstance().gson.fromJson(json, CommItem.class);
  
        if (mCommItem.getTaskItem().getAddress().getLongitude() == null)
          return;
        if (mCommItem.getTaskItem().getAddress().getLatitude() == null)
          return;
        
        bundle.putDouble("longitude", mCommItem.getTaskItem().getAddress().getLongitude());
        bundle.putDouble("latitude", mCommItem.getTaskItem().getAddress().getLatitude());
        bundle.putString("name", mCommItem.getTaskItem().getKundenName());
        fragment.setArguments(bundle);
        break;
    }

    getSupportFragmentManager()
      .beginTransaction()
      .replace(R.id.main_container, fragment)
      .addToBackStack(null)
      .commit();
  }

  private void initFirstTimeRun() {
    Log.i(TAG, "initFirstTimeRun() called!");
  
    if (!TextSecurePreferences.isDeviceFirstTimeRun()) {
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
     
      TextSecurePreferences.setDeviceFirstTimeRun(true);
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
            TextSecurePreferences.setDevicePermissionsGranted(true);
            /*
            ServiceWorker serviceWorker = new ServiceWorker(getApplicationContext());
            Intent mServiceWorkerIntent = new Intent(getApplicationContext(), serviceWorker.getClass());
            if (!isMyServiceRunning(serviceWorker.getClass())) {
              Log.i(TAG, "******* START SERVICE WORKER *******");
              startService(mServiceWorkerIntent);
            } else {
              Log.i(TAG, "******* SERVICE WORKER IS ALREADY RUNNING *******");
            }
            */
            initFirstTimeRun();
          } else {
            Log.d(TAG, "!!!areAllPermissionsGranted() called!");
            TextSecurePreferences.setDevicePermissionsGranted(false);
            showSettingsDialog();
          }
          
          // check for permanent denial of any permission.
          if (report.isAnyPermissionPermanentlyDenied()) {
            Log.d(TAG, "isAnyPermissionPermanentlyDenied() called!");
            TextSecurePreferences.setDevicePermissionsGranted(false);
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
  
  private void addLog(LogLevel level, LogType type, String title, String message) {
    LogItem item = new LogItem();
    item.setLevel(level);
    item.setType(type);
    item.setTitle(title);
    item.setMessage(message);
    item.setCreatedAt(new Date());
    mMainViewModel.insert(item);
  }
  
  private void initializeLogify() {
    // Initialize the Logify Alert client.
    LogifyAlert client = LogifyAlert.getInstance();
    client.setApiKey("5B357B2806714B8598C6127F537CD389");
    client.setContext(this.getApplicationContext());
    client.startExceptionsHandling();
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
  
  private class SignalStrengthStateListener extends PhoneStateListener {
    
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
      super.onSignalStrengthsChanged(signalStrength);
      
      int level = 0;
      
      if (isAirplaneModeOn(getApplicationContext())) {
        MessageDialog.build(MainActivity.this)
          .setStyle(DialogSettings.STYLE.STYLE_IOS)
          .setTheme(DialogSettings.THEME.LIGHT)
          .setTitle("Warnung")
          .setMessage("Bei Flugzeugmodus ist die Funktion der ABONA Driver App eingeschränkt!")
          .setOkButton(getApplicationContext().getResources().getString(R.string.action_ok),
            new OnDialogButtonClickListener() {
              @Override
              public boolean onClick(BaseDialog baseDialog, View v) {
                ((AppCompatImageButton)findViewById(R.id.connectivity))
                  .setColorFilter(getApplicationContext().getResources()
                    .getColor(R.color.clrAbona));
                return false;
              }
            })
          .show();
        return;
      }
      
      List<CellInfo> infos = null;
      try {
        infos = mTelephonyManager.getAllCellInfo();
      } catch (SecurityException e) {
        Log.e(TAG, e.toString());
      }
      
      if (infos == null) {
        return;
      }
      
      if (!mConnected) {
        ((AppCompatImageButton)findViewById(R.id.connectivity))
          .setColorFilter(getApplicationContext().getResources()
            .getColor(R.color.clrAbona));
        return;
      }
      
      for (final CellInfo info : infos) {
        if (info instanceof CellInfoWcdma) {
          final CellSignalStrengthWcdma wcdma = ((CellInfoWcdma) info).getCellSignalStrength();
          
          if (level < wcdma.getLevel()) {
            level = wcdma.getLevel();
          }
        } else if (info instanceof CellInfoGsm) {
          final CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
          
          if (level < gsm.getLevel()) {
            level = gsm.getLevel();
          }
        } else if (info instanceof CellInfoCdma) {
          final CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
          
          if (level < cdma.getLevel()) {
            level = cdma.getLevel();
          }
        } else if (info instanceof CellInfoLte) {
          final CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
          
          if (level < lte.getLevel()) {
            level = lte.getLevel();
          }
        }
      }
      
      if (level <= 1) {
        ((AppCompatImageButton)findViewById(R.id.connectivity)).setColorFilter(Color.parseColor("#d35400"));
      } else {
        ((AppCompatImageButton)findViewById(R.id.connectivity)).setColorFilter(Color.parseColor("#009432"));
      }
    }
  }
  
  private static boolean isAirplaneModeOn(Context context) {
    return Settings.Global.getInt(context.getContentResolver(),
      Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
  }
  
  private void messageBox_Ok(String title, String message) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        MessageDialog.build((AppCompatActivity) MainActivity.this)
          .setStyle(DialogSettings.STYLE.STYLE_IOS)
          .setTheme(DialogSettings.THEME.LIGHT)
          .setTitle(title)
          .setMessage(message)
          .setOkButton(getApplicationContext().getResources().getString(R.string.action_ok),
            new OnDialogButtonClickListener() {
              @Override
              public boolean onClick(BaseDialog baseDialog, View v) {
                return false;
              }
            })
          .show();
      }
    });
  }
}
