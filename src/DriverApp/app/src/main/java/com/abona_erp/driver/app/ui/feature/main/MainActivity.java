package com.abona_erp.driver.app.ui.feature.main;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.BuildConfig;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.converters.LogLevel;
import com.abona_erp.driver.app.data.converters.LogType;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LogItem;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.model.AppFileInterchangeItem;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.ResultOfAction;
import com.abona_erp.driver.app.data.model.TaskItem;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.receiver.NetworkChangeReceiver;
import com.abona_erp.driver.app.service.BackgroundServiceWorker;
import com.abona_erp.driver.app.service.ForegroundAlarmService;
import com.abona_erp.driver.app.ui.base.BaseActivity;
import com.abona_erp.driver.app.ui.event.ConnectivityEvent;
import com.abona_erp.driver.app.ui.event.DocumentEvent;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.event.ProtocolEvent;
import com.abona_erp.driver.app.ui.event.TaskStatusEvent;
import com.abona_erp.driver.app.ui.event.VehicleRegistrationEvent;
import com.abona_erp.driver.app.ui.feature.login.LoginActivity;
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
import com.abona_erp.driver.app.util.RingtoneUtils;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.Util;
import com.abona_erp.driver.core.base.ContextUtils;
import com.devexpress.logify.alert.android.LogifyAlert;
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

import org.greenrobot.eventbus.EventBus;
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
import java.util.TimeZone;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity /*implements OnCompleteListener<Void>*/ {

  @Inject
  public NotificationManager notificationManager;
   @Inject
  public AlarmManager alarmManager;

  private static final String TAG = MainActivity.class.getSimpleName();
  
  public static final int REQUEST_APP_SETTINGS = 321;
  
  public static final String BACK_STACK_ROOT_TAG = "root_fragment";

  private CommItem mCommItem;
  private View header;
  private ProgressBar progressBar;
  private AsapTextView mVehicleRegistrationNumber;
  private AsapTextView mVehicleClientName;
  private AppCompatImageView getAllTaskImage;


  private NetworkChangeReceiver networkChangeReceiver;

  // VIEW MODEL:
  private MainViewModel mMainViewModel;
  
  private enum PendingGeofenceTask {
    ADD, REMOVE, NONE
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
  protected void onDestroy() {
    super.onDestroy();
    if(EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    unRegisterNetworkChangeReceiver();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    injectDependency();
    if (TextSecurePreferences.enableLoginPage()) {
      Intent intent = new Intent(this, LoginActivity.class);
      startActivity(intent);
      finish();
      return;
    }
    registerNetworkChangeReceiver();
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // LOGIFY
    initializeLogify();
  
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // GET A NEW OR EXISTING VIEWMODEL FROM THE VIEWMODELPROVIDER.
    //
    mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

    // ---------------------------------------------------------------------------------------------
    // - Permission Request:
    // - Update FCM Token:
    //
    requestDriverPermission();
    if (!AppUtils.isNetworkConnected(getBaseContext())) {
      findViewById(R.id.connectivity_image).setVisibility(View.VISIBLE);
      Util.showDialog(this, getApplicationContext().getResources()
                      .getString(R.string.action_warning_notice),
              getApplicationContext().getResources()
                      .getString(R.string.no_internet));
    } else{
      findViewById(R.id.connectivity_image).setVisibility(View.GONE);
    }
    if(!EventBus.getDefault().isRegistered(this)) App.eventBus.register(this);

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

    ((AsapTextView)findViewById(R.id.tv_vehicle_client_name))
            .setText(TextSecurePreferences.getClientName(getBaseContext()));
    ((AsapTextView)findViewById(R.id.tv_vehicle_registration_number))
            .setText(TextSecurePreferences.getVehicleRegistrationNumber(getBaseContext()));

    header = findViewById(R.id.header_container);// to hide it when using settings fragment

    ImageView settingsImage  = findViewById(R.id.settings_image);

    settingsImage.setOnClickListener(v->{
      loadFragment(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_SETTINGS),
              null), SettingsFragment.newInstance());
      hideMainActivityItems();
    });

    getAllTaskImage = findViewById(R.id.refresh_image);
    progressBar = findViewById(R.id.progressBar);
    getAllTaskImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        progressBar.setVisibility(View.VISIBLE);
        getAllTaskImage.setEnabled(false);

        Call<ResultOfAction> call = App.getInstance().apiManager.getTaskApi().getAllTasks(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
        call.enqueue(new Callback<ResultOfAction>() {
          @Override
          public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
            getAllTaskImage.setEnabled(true);
            progressBar.setVisibility(View.GONE);
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
            }
          }
  
          @Override
          public void onFailure(Call<ResultOfAction> call, Throwable t) {
            // TODO: Wahrscheinlich kein Internet!
            getAllTaskImage.setEnabled(true);
            progressBar.setVisibility(View.GONE);
          }
        });
      }
    });

    
    mMainViewModel.getNotReadNotificationCount().observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        if (integer == null)
          return;
        int value = integer;
        if (value <= 0) {
          findViewById(R.id.badge_notification).setVisibility(View.GONE);
          ((ImageView) findViewById(R.id.badge_notification_image))
            .setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications_outline, null));
        } else {
          findViewById(R.id.badge_notification).setVisibility(View.VISIBLE);
          ((ImageView) findViewById(R.id.badge_notification_image))
            .setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications, null));
          if (value <= 99) {
            ((TextView)findViewById(R.id.badge_notification)).setText(String.valueOf(integer.intValue()));
          } else {
            ((TextView) findViewById(R.id.badge_notification)).setText("99+");
          }
        }
      }
    });
    
    mMainViewModel.getAllOfflineConfirmations().observe(this, new Observer<List<OfflineConfirmation>>() {
      @Override
      public void onChanged(List<OfflineConfirmation> offlineConfirmations) {
        int count = offlineConfirmations.size();
        if (count > 0) {
          findViewById(R.id.badge_process).setVisibility(View.VISIBLE);
          if (count <= 99) {
            ((TextView)findViewById(R.id.badge_process)).setText(String.valueOf(count));
          } else {
            ((TextView)findViewById(R.id.badge_process)).setText("99+");
          }
        } else {
          findViewById(R.id.badge_process).setVisibility(View.GONE);
        }
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

  @Override
  public void injectDependency() {
    getActivityComponent().inject(this);
  }

  private void registerNetworkChangeReceiver() {
    if(networkChangeReceiver == null)
    networkChangeReceiver = new NetworkChangeReceiver();
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    registerReceiver(networkChangeReceiver, intentFilter);
  }

  private void unRegisterNetworkChangeReceiver() {
    if(networkChangeReceiver!=null)
    try {
      unregisterReceiver(networkChangeReceiver);
    } catch (IllegalArgumentException e){
      Log.e(TAG, " Receiver not registered: com.abona_erp.driver.app.receiver.NetworkChangeReceiver");
    }
  }

  private void hideMainActivityItems(){
    header.setVisibility(View.GONE);
  }

  private void showMainActivityItems(){
    header.setVisibility(View.VISIBLE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    
    if (requestCode == REQUEST_APP_SETTINGS) {
      Log.i(TAG, "onActivityResult() called! - REQUEST_APP_SETTINGS");
      
      if (!hasPermissions(Manifest.permission.READ_PHONE_STATE)) {
        Log.i(TAG, "onActivityResult() called! - No READ_PHONE_STATE permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        Util.showPermissionErrorMessageAndFinish(this, getResources().getString(R.string.need_permissions), "No READ_PHONE_STATE permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.ACCESS_FINE_LOCATION)) {
        Log.i(TAG, "onActivityResult() called! - No ACCESS_FINE_LOCATION permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        Util.showPermissionErrorMessageAndFinish(this,getResources().getString(R.string.need_permissions), "No ACCESS_FINE_LOCATION permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.CAMERA)) {
        Log.i(TAG, "onActivityResult() called! - No CAMERA permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        Util.showPermissionErrorMessageAndFinish(this,getResources().getString(R.string.need_permissions), "No CAMERA permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.RECORD_AUDIO)) {
        Log.i(TAG, "onActivityResult() called! - No RECORD_AUDIO permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        Util.showPermissionErrorMessageAndFinish(this,getResources().getString(R.string.need_permissions), "No RECORD_AUDIO permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        Log.i(TAG, "onActivityResult() called! - No WRITE_EXTERNAL_STORAGE permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        Util.showPermissionErrorMessageAndFinish(this,getResources().getString(R.string.need_permissions), "No WRITE_EXTERNAL_STORAGE permission...");
        return;
      }
      if (!hasPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)) {
        Log.i(TAG, "onActivityResult() called! - No READ_EXTERNAL_STORAGE permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        Util.showPermissionErrorMessageAndFinish(this,getResources().getString(R.string.need_permissions), "No READ_EXTERNAL_STORAGE permission...");
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

  @Subscribe
  public void onMessageEvent(ConnectivityEvent event) {
      if(!event.isConnected()){
        findViewById(R.id.connectivity_image).setVisibility(View.VISIBLE);
          Util.showDialog(this, getApplicationContext().getResources()
                          .getString(R.string.action_warning_notice),
                  getApplicationContext().getResources()
                          .getString(R.string.no_internet));
      } else {
        findViewById(R.id.connectivity_image).setVisibility(View.GONE);
        if (Util.isAirplaneModeOn(getApplicationContext())) {
         Util.showAirplaneDialog(this);
        }
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
          showMainActivityItems();
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
        showOkDialog("Not Found", "Task existiert nicht mehr!");
        break;
        
      case PageItemDescriptor.PAGE_DEVICE_REGISTRATED:
        loadMainFragment(MainFragment.newInstance());
        
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString deviceID = new SpannableString(DeviceUtils.getUniqueIMEI(getBaseContext()));
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        deviceID.setSpan(boldSpan, 0 , deviceID.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        builder.append("Device registration was successfully!\n\nRegistration Number\n");
        builder.append(deviceID);
        
        showOkDialog("Successful Registrated", builder.toString());
        break;
        
      case PageItemDescriptor.PAGE_NEW_DOCUMENTS:
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Util.showDocumentDialog(MainActivity.this, event.getNotify());
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
        
//        if (event.isDeleteAll()) {
//          mMainPieView.setPercentage(0);
//        }
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
                    /*
                    StringBuilder sb = new StringBuilder();
                    sb.append(getApplicationContext().getResources().getString(R.string.new_document_message));
                    sb.append("\n" + getApplicationContext().getResources().getString(R.string.order_no));
                    sb.append(": " + AppUtils.parseOrderNo(event.getOrderNo()));
                    
                    showOkDialog(getApplicationContext().getResources().getString(R.string.new_document), sb.toString());
                    */
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

  
  private void handleGetAllTasks(ResultOfAction resultOfAction) {
    if (resultOfAction == null) {
      getAllTaskImage.setEnabled(true);
      return;
    }

    try {
      if (resultOfAction.getIsSuccess() && !resultOfAction.getIsException()) {
        if (resultOfAction.getAllTask() != null && resultOfAction.getAllTask().size() > 0) {
          Intent serviceIntent = new Intent(this, ForegroundAlarmService.class);
          startService(serviceIntent);

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
                  commItem = App.getInstance().gson.fromJson(notify.getData(), CommItem.class);
                  if (commItem != null) {
                    commItem.setTaskItem(taskItem);
                    
                    notify.setRead(false);
                    notify.setTaskDueFinish(taskItem.getTaskDueDateFinish());
                    notify.setData(App.getInstance().gson.toJson(commItem));
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
          showOkDialog(
            getApplicationContext().getResources()
              .getString(R.string.action_update),
            getApplicationContext().getResources()
              .getString(R.string.action_update_message));
        } else {
          // Kein Update vorhanden:
          showOkDialog(
            getApplicationContext().getResources()
              .getString(R.string.action_update),
            getApplicationContext().getResources()
              .getString(R.string.action_update_message));
        }
      } else if (resultOfAction.getIsException()) {
        // Exception from REST-API
        showOkDialog(getApplicationContext().getResources().getString(R.string.action_warning_notice),
          getApplicationContext().getResources().getString(R.string.action_exception_on_rest_api));
        addLog(LogLevel.FATAL, LogType.API, "GetAllTasks", resultOfAction.getText());
      } else {
        // Unknown Error
        addLog(LogLevel.FATAL, LogType.API, "GetAllTasks", "Unknown Error!");
      }
    } catch (Exception e) {
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
            String jsonData = response.body().string();
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
      deviceProfile.setDeviceSerial(DeviceUtils.getUniqueIMEI(getBaseContext()));
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
            Util.showSettingsDialog(MainActivity.this);
          }
          
          // check for permanent denial of any permission.
          if (report.isAnyPermissionPermanentlyDenied()) {
            Log.d(TAG, "isAnyPermissionPermanentlyDenied() called!");
            TextSecurePreferences.setDevicePermissionsGranted(false);
            Util.showSettingsDialog(MainActivity.this);
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

  
  private boolean hasPermissions(@NonNull String... permissions) {
    for (String permission : permissions) {
      if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {
        return false;
      }
    }
    return true;
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


  
  private void showOkDialog(String title, String message) {
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
