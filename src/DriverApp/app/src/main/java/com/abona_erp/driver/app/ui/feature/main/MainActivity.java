package com.abona_erp.driver.app.ui.feature.main;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
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
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.model.AppFileInterchangeItem;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.ResultOfAction;
import com.abona_erp.driver.app.data.model.TaskItem;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.data.remote.ApiService;
import com.abona_erp.driver.app.receiver.LocaleChangeReceiver;
import com.abona_erp.driver.app.receiver.NetworkChangeReceiver;
import com.abona_erp.driver.app.service.BackgroundServiceWorker;
import com.abona_erp.driver.app.service.ForegroundAlarmService;
import com.abona_erp.driver.app.ui.base.BaseActivity;
import com.abona_erp.driver.app.ui.event.ConnectivityEvent;
import com.abona_erp.driver.app.ui.event.DocumentEvent;
import com.abona_erp.driver.app.ui.event.HistoryClick;
import com.abona_erp.driver.app.ui.event.LogEvent;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.event.ProtocolEvent;
import com.abona_erp.driver.app.ui.event.VehicleRegistrationEvent;
import com.abona_erp.driver.app.ui.feature.login.LoginActivity;
import com.abona_erp.driver.app.ui.feature.main.fragment.DetailFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.MainFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.about.SoftwareAboutFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.document_viewer.DocumentViewerFragment;
import com.abona_erp.driver.app.ui.feature.main.fragment.history.HistoryFragment;
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
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.MessageDialog;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

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
import retrofit2.HttpException;
import retrofit2.Response;

import static com.abona_erp.driver.app.data.converters.LogType.FCM;

public class MainActivity extends BaseActivity /*implements OnCompleteListener<Void>*/ {

  private static final String TAG = MainActivity.class.getSimpleName();
  
  public static final int REQUEST_APP_SETTINGS = 321;
  
  public static final String BACK_STACK_ROOT_TAG = "root_fragment";
  private Handler h = new Handler();
  @Inject
  ApiService apiService;

  // Below flag used to check activity visible or not?
  private boolean isActivityVisible;

  @Override
  public void onResume() {
    super.onResume();
    isActivityVisible = true;
  }

  public void onPause() {
    super.onPause();
    isActivityVisible = false;
  }


  private CommItem mCommItem;
  private View header;
  private ProgressBar progressBar;
  private AsapTextView mVehicleRegistrationNumber;
  private AsapTextView mVehicleClientName;
  private AppCompatImageView getAllTaskImage;


  private NetworkChangeReceiver networkChangeReceiver;
  private LocaleChangeReceiver localeChangeReceiver;

  // VIEW MODEL:
  private MainViewModel mMainViewModel;
  
  private enum PendingGeofenceTask {
    ADD, REMOVE, NONE
  }

  private DialogInterface.OnClickListener positiveDialogListener =  new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
      dialog.dismiss();
      openSettings(MainActivity.this);
    }
  };

  private DialogInterface.OnClickListener negativeDialogListener =  new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
      dialog.dismiss();
      MainActivity.this.finish();
    }
  };


  public void startBackgroundWorkerService() {
    Intent serviceIntent = new Intent(this, BackgroundServiceWorker.class);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(serviceIntent);
    } else {
      startService(serviceIntent);
    }
  }

  private void startAlarmService() {
    Intent serviceIntent = new Intent(this, ForegroundAlarmService.class);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(serviceIntent);
    } else {
      startService(serviceIntent);
    }
  }

  @Override
  public void onBackPressed() {
    if(getSupportFragmentManager().getBackStackEntryCount() > 1 )
      App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
    else {
      Util.askNeedExit(MainActivity.this);
    }
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    if(EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    unRegisterNetworkChangeReceiver();
    unLocaleReceiver();
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
    registerLocaleReceiver();
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // LOGIFY, AppCenter
    initializeLogify();
    initializeAppCenter();
  
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
      loadSettingsFragment();
      hideMainActivityItems();
    });

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      changeDeviceIdNewApi();
    }
    getAllTaskImage = findViewById(R.id.refresh_image);
    progressBar = findViewById(R.id.progressBar);
    getAllTaskImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mMainViewModel.addLog(getResources().getString(R.string.log_update_pressed), LogType.APP_TO_SERVER, LogLevel.INFO, getResources().getString(R.string.log_title_get_tasks));
        onUpdateClick();
      }
    });


    observeNotifications();
    observeConfirmations();


    if (!TextSecurePreferences.isDeviceRegistrated() || TextSecurePreferences.isRegistrationStarted()) {
      loadMainFragment(DeviceNotRegistratedFragment.newInstance());
    } else {
      loadMainFragment(MainFragment.newInstance());
    }
//order matter: first loading main fragment
    if(getIntent()!= null && getIntent().getExtras()!= null && getIntent().getExtras().getBoolean(Constants.EXTRAS_START_SETTINGS) == true){
      loadSettingsFragment();
      hideMainActivityItems();
    }
    startBackgroundWorkerService();
  }

  private void observeConfirmations() {
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
  }

  private void observeNotifications() {
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
  }

  private void addFirebaseListener() {
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
            Log.d(TAG,"Firebase registration Token=" + task.getResult().getToken());
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
  }


  private void onUpdateClick() {
    progressBar.setVisibility(View.VISIBLE);
    getAllTaskImage.setEnabled(false);

    Call<ResultOfAction> call = App.getInstance().apiManager.getTaskApi().getAllTasks(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
    call.enqueue(new Callback<ResultOfAction>() {
      @Override
      public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
        getAllTaskImage.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        if (response.isSuccessful() && response.body() != null) {

          mMainViewModel.addLog(getResources().getString(R.string.log_tasks_come), LogType.SERVER_TO_APP, LogLevel.INFO, getResources().getString(R.string.log_title_get_tasks));

          handleGetAllTasks(response.body());
        } else {

          switch (response.code()) {
            case 401:
              mMainViewModel.addLog(getResources().getString(R.string.log_token_error), LogType.SERVER_TO_APP, LogLevel.ERROR, getResources().getString(R.string.log_title_get_tasks));

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

        mMainViewModel.addLog(getResources().getString(R.string.log_tasks_error), LogType.SERVER_TO_APP, LogLevel.ERROR, getResources().getString(R.string.log_title_get_tasks));

        getAllTaskImage.setEnabled(true);
        progressBar.setVisibility(View.GONE);
      }
    });
  }

  private int tryCount = 3;
  /**
   * this method changes ID on Abona Server
   * made because DeviceUtils.getUniqueIMEI() was wrong,
   * based on MAC that is same on android 10 devices.
   */
  @RequiresApi(Build.VERSION_CODES.Q)
  private void changeDeviceIdNewApi() {
    compositeDisposable.add(
            mMainViewModel.getDeviceProfile().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(databaseProfile -> {
                              if(databaseProfile.getDeviceId()== null || !databaseProfile.getDeviceId().equals(getAndroidQnewID())) {
                                Log.d(TAG, " migrating deviceID :  old : " + databaseProfile.getDeviceId() + " new  : " + getAndroidQnewID());
                                apiService.migrateDeviceId(databaseProfile.getDeviceId(), getAndroidQnewID())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(result -> {
                                          if (result.getIsSuccess()) {
                                            databaseProfile.setDeviceId(getAndroidQnewID());
                                            databaseProfile.setDeviceSerial(DeviceUtils.getSerial());
                                            mMainViewModel.update(databaseProfile); //that is old implementation to update room table
                                            mMainViewModel.deleteOldTables();
                                            Log.d(TAG, " success, id updatedToNew: " +  result.toString());
                                            h.postDelayed(() -> {
                                              onUpdateClick();
                                            }, 1000);
                                          }else {
                                            Log.d(TAG, result.toString());
                                          }
                                        }, error -> {
                                          if (error instanceof HttpException) {
                                            if (((HttpException) error).code() == 401 && tryCount > 0) {
                                              handleAccessToken();
                                              Log.d(TAG, "tryCount: " + tryCount + " got auth error, scheduling new update  " + error.toString());
                                              h.postDelayed(() -> {
                                                tryCount--;
                                                changeDeviceIdNewApi();
                                              }, Constants.REPEAT_TIME_MIGRATION);
                                            }
                                          }else {
                                            Log.e(TAG, " error when calling migrate ID, " + error.toString());
                                          }
                                        });
                              }
                    },
                    throwable -> Log.e(TAG, "Unable to getDeviceFrom db", throwable)));
  }

  @NotNull
  private String getAndroidQnewID() {
    return DeviceUtils.getUniqueID(getBaseContext());
  }

  private void registerLocaleReceiver() {
    if(localeChangeReceiver == null){
      localeChangeReceiver  = new LocaleChangeReceiver();
    }
    IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
    registerReceiver(localeChangeReceiver, filter);
  }

  private void unLocaleReceiver() {
    if(localeChangeReceiver!=null)
      try {
        unregisterReceiver(localeChangeReceiver);
      } catch (IllegalArgumentException e){
        Log.e(TAG, " Receiver not registered: com.abona_erp.driver.app.receiver.localeChangeReceiver");
      }
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
        Util.showPermissionErrorMessageAndFinish(this, getResources().getString(R.string.need_permissions), "No READ_PHONE_STATE permission...", positiveDialogListener);
        return;
      }
      if (!hasPermissions(Manifest.permission.ACCESS_FINE_LOCATION)) {
        Log.i(TAG, "onActivityResult() called! - No ACCESS_FINE_LOCATION permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        Util.showPermissionErrorMessageAndFinish(this,getResources().getString(R.string.need_permissions), "No ACCESS_FINE_LOCATION permission...", positiveDialogListener);
        return;
      }
      if (!hasPermissions(Manifest.permission.CAMERA)) {
        Log.i(TAG, "onActivityResult() called! - No CAMERA permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        Util.showPermissionErrorMessageAndFinish(this,getResources().getString(R.string.need_permissions), "No CAMERA permission...", positiveDialogListener);
        return;
      }
      if (!hasPermissions(Manifest.permission.RECORD_AUDIO)) {
        Log.i(TAG, "onActivityResult() called! - No RECORD_AUDIO permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        Util.showPermissionErrorMessageAndFinish(this,getResources().getString(R.string.need_permissions), "No RECORD_AUDIO permission...", positiveDialogListener);
        return;
      }
      if (!hasPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        Log.i(TAG, "onActivityResult() called! - No WRITE_EXTERNAL_STORAGE permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        Util.showPermissionErrorMessageAndFinish(this,getResources().getString(R.string.need_permissions), "No WRITE_EXTERNAL_STORAGE permission...", positiveDialogListener);
        return;
      }
      if (!hasPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)) {
        Log.i(TAG, "onActivityResult() called! - No READ_EXTERNAL_STORAGE permission...");
        TextSecurePreferences.setDevicePermissionsGranted(false);
        Util.showPermissionErrorMessageAndFinish(this,getResources().getString(R.string.need_permissions), "No READ_EXTERNAL_STORAGE permission...", positiveDialogListener);
        return;
      }
    }
    
    TextSecurePreferences.setDevicePermissionsGranted(true);

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
  public void onMessageEvent(LogEvent event) {
    if(event.getLog().getType() == FCM) // hide History fragment for now, log only this events
    mMainViewModel.addLog(event.getLog());
  }


  @Subscribe
  public void onMessageEvent(HistoryClick historyClick) {
    loadHistoryFragment();
  }

  private void showHistoryClickError(Throwable error) {
    Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
//                mMainViewModel.addLog(getString(R.string.log_document_got_links), LogType.SERVER_TO_APP, LogLevel.INFO, getString(R.string.log_title_docs), event.getOrderNo());


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
          startAlarmService();
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

        mMainViewModel.addLog(getResources().getString(R.string.action_exception_on_rest_api), LogType.SERVER_TO_APP, LogLevel.ERROR, getResources().getString(R.string.log_title_get_tasks));
      } else {
        // Unknown Error
        mMainViewModel.addLog(getResources().getString(R.string.log_message_unknown_error), LogType.SERVER_TO_APP, LogLevel.ERROR, getResources().getString(R.string.log_title_get_tasks));
      }
    } catch (Exception e) {
      mMainViewModel.addLog(e.getMessage(), LogType.SERVER_TO_APP, LogLevel.ERROR, getResources().getString(R.string.log_title_get_tasks));
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

  private void loadSettingsFragment() {
    loadFragment(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_SETTINGS),
            null), new SettingsFragment());
  }
   private void loadHistoryFragment() {
    loadFragment(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_HISTORY),
            null), new HistoryFragment());
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
      deviceProfile.setDeviceSerial(DeviceUtils.getSerial());
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

      addFirebaseListener();

      TextSecurePreferences.setDeviceFirstTimeRun(true);
    }
  }

  private void requestDriverPermission() {
    if(!hasPermissions(Constants.permissions)){
      ActivityCompat.requestPermissions(this, Constants.permissions, Constants.REQUEST_PERMISSIONS_KEY);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == Constants.REQUEST_PERMISSIONS_KEY){
      if(hasPermissions(Constants.permissions)) {
        TextSecurePreferences.setDevicePermissionsGranted(true);
        initFirstTimeRun();
      } else {
        TextSecurePreferences.setDevicePermissionsGranted(false);
        Util.showSettingsDialog(MainActivity.this, positiveDialogListener, negativeDialogListener);
      }
    }
  }



  /**
   * Navigating User to App Settings.
   */
  private static void openSettings(MainActivity mainActivity) {
    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", mainActivity.getPackageName(), null);
    intent.setData(uri);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            | Intent.FLAG_ACTIVITY_NO_HISTORY
            | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    mainActivity.startActivityForResult(intent, MainActivity.REQUEST_APP_SETTINGS);
  }

  private boolean hasPermissions(@NonNull String... permissions) {
    for (String permission : permissions) {
      if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {
        return false;
      }
    }
    return true;
  }

  
  private void initializeLogify() {
    // Initialize the Logify Alert client.
    LogifyAlert client = LogifyAlert.getInstance();
    client.setApiKey("5B357B2806714B8598C6127F537CD389");
    client.setContext(this.getApplicationContext());
    client.startExceptionsHandling();
  }
  
  private void initializeAppCenter() {
    // Initialize the AppCenter.
    AppCenter.start(getApplication(), "317a6cfb-0f3e-4810-bbc1-13dfa263c2eb",
      Analytics.class, Crashes.class);
    Analytics.setEnabled(true);
    Analytics.trackEvent("App started");
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
