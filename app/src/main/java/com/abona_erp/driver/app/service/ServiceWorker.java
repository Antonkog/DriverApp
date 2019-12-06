package com.abona_erp.driver.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.dao.LastActivityDAO;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.model.ActivityItem;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ConfirmationItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.DataType;
import com.abona_erp.driver.app.data.model.DeviceProfileItem;
import com.abona_erp.driver.app.data.model.Header;
import com.abona_erp.driver.app.data.model.LastActivityDetails;
import com.abona_erp.driver.app.data.model.ResultOfAction;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.manager.ApiManager;
import com.abona_erp.driver.app.receiver.NetworkStateReceiver;
import com.abona_erp.driver.app.ui.event.DeviceRegistratedEvent;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DateConverter;
import com.abona_erp.driver.app.util.TextSecurePreferences;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceWorker extends Service
  implements NetworkStateReceiver.NetworkStateReceiverListener {
  
  private static final String TAG = ServiceWorker.class.getSimpleName();
  
  Context mContext;
  private static boolean fConnected = true;
  
  private NetworkStateReceiver mNetworkStateReceiver;
 
  public ServiceWorker() {
    //super();
    //Log.d(TAG, "ServiceWorker() 1 called!");
    //mContext = ContextUtils.getApplicationContext();
  }
  
  public ServiceWorker(Context ctx) {
    super();
    Log.d(TAG, "ServiceWorker() 2 called!");
    mContext = ctx;
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
  }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
  
    Log.i(TAG, "onStartCommand() called!");
    
    try {
      unregisterNetworkBroadcastReceiver(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
    startTimer();
    startNetworkBroadcastReceiver(this);
    
    return START_STICKY;
  }
  
  @Override
  public void onDestroy() {
  /*
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction("com.abona_erp.driver.RestartWorkerService");
    intentFilter.setPriority(100);
    sendBroadcast(new Intent());
   */
    
    //super.onDestroy();
    Log.i(TAG, "onDestroy() called!");
  
    Intent broadcastIntent = new Intent("restart0");
    sendBroadcast(broadcastIntent);
    stopTimerTask();
  }

  private Timer mTimer;
  private TimerTask mTimerTask;
  
  public void startTimer() {
    // set a new Timer:
    mTimer = new Timer();
    
    // initialize the TimerTask's job:
    initializeTimerTask();
    
    // schedule the timer, to wake up every 30 second.
    mTimer.schedule(mTimerTask, 1000, 10000);
  }
  
  public void initializeTimerTask() {
    mTimerTask = new TimerTask() {
      @Override
      public void run() {
        Log.i(TAG, "******* BACKGROUND SERVICE WORKER RUNNING *******");
        
        if (!TextSecurePreferences.isDevicePermissionsGranted(getApplicationContext()))
          return;
        
        if (!fConnected)
          return;
        
        // FIRST DEVICE REGISTRATION:
        if (TextSecurePreferences.isDeviceFirstTimeRun(getApplicationContext())) {
          if (!TextSecurePreferences.isDeviceRegistrated(getApplicationContext())) {
            Log.i(TAG, "******* PREPARE DEVICE REGISTRATION *******");
            
            DriverDatabase db = DriverDatabase.getDatabase();
            DeviceProfileDAO dao = db.deviceProfileDAO();
            List<DeviceProfile> deviceProfiles = dao.getDeviceProfiles();
            if (deviceProfiles.size() > 0) {
              CommItem commItem = new CommItem();
              Header header = new Header();
              header.setDataType(DataType.DEVICE_PROFILE);
              commItem.setHeader(header);
              
              DeviceProfileItem deviceProfileItem = new DeviceProfileItem();
              deviceProfileItem.setInstanceId(deviceProfiles.get(0).getInstanceId());
              deviceProfileItem.setDeviceId(deviceProfiles.get(0).getDeviceId());
              deviceProfileItem.setModel(deviceProfiles.get(0).getDeviceModel());
              deviceProfileItem.setManufacturer(deviceProfiles.get(0).getDeviceManufacturer());
              deviceProfileItem.setCreatedDate(DateConverter.fromTimestamp(deviceProfiles.get(0).getCreatedAt()));
              deviceProfileItem.setUpdatedDate(DateConverter.fromTimestamp(deviceProfiles.get(0).getModifiedAt()));
              deviceProfileItem.setLanguageCode(deviceProfiles.get(0).getLanguageCode());
              deviceProfileItem.setVersionCode(deviceProfiles.get(0).getVersionCode());
              deviceProfileItem.setVersionName(deviceProfiles.get(0).getVersionName());
              commItem.setDeviceProfileItem(deviceProfileItem);

              Call<ResultOfAction> call = App.apiManager.getFCMApi().deviceProfile(commItem);
              call.enqueue(new Callback<ResultOfAction>() {
                @Override
                public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
                  if (response.isSuccessful()) {
                    if (response.body() != null) {
                      Log.d(TAG, "******* DEVICE REGISTRATION WAS SUCCESSFULLY!!!");
                      TextSecurePreferences.setDeviceRegistrated(getApplicationContext(), true);
                      App.eventBus.post(new DeviceRegistratedEvent());
                    }
                  } else {
                    
                    // error case:
                    switch (response.code()) {
                      case 401:
                        handleAccessToken();
                        break;
                    }
                  }
                }
  
                @Override
                public void onFailure(Call<ResultOfAction> call, Throwable t) {
                  Log.d(TAG, "******* ERROR ON DEVICE REGISTRATION!!!");
                }
              });
            }
          } else if (TextSecurePreferences.getFcmTokenUpdate(getApplicationContext())) {
            // FCM TOKEN UPDATE:
            Log.i(TAG, "******* PREPARE DEVICE TOKEN UPDATE *******");
  
            DriverDatabase db = DriverDatabase.getDatabase();
            DeviceProfileDAO dao = db.deviceProfileDAO();
            List<DeviceProfile> deviceProfiles = dao.getDeviceProfiles();
            if (deviceProfiles.size() > 0) {
              CommItem commItem = new CommItem();
              Header header = new Header();
              header.setDataType(DataType.DEVICE_PROFILE);
              commItem.setHeader(header);
  
              DeviceProfileItem deviceProfileItem = new DeviceProfileItem();
              deviceProfileItem.setInstanceId(deviceProfiles.get(0).getInstanceId());
              deviceProfileItem.setDeviceId(deviceProfiles.get(0).getDeviceId());
              commItem.setDeviceProfileItem(deviceProfileItem);
  
              Call<ResultOfAction> call = App.apiManager.getFCMApi().deviceProfile(commItem);
              call.enqueue(new Callback<ResultOfAction>() {
                @Override
                public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
                  if (response.isSuccessful()) {
                    if (response.body() != null) {
                      Log.d(TAG, "******* FCM DEVICE TOKEN UPDATED!!!");
                      TextSecurePreferences.setFcmTokenUpdate(getApplicationContext(), false);
                    }
                  } else  {
  
                    // error case:
                    switch (response.code()) {
                      case 401:
                        handleAccessToken();
                        break;
                    }
                  }
                }
    
                @Override
                public void onFailure(Call<ResultOfAction> call, Throwable t) {
                  Log.d(TAG, "******* ERROR ON DEVICE REGISTRATION!!!");
                }
              });
            }
          } else {
            
            // JOBS ABARBEITEN:
            DriverDatabase db = DriverDatabase.getDatabase();
            OfflineConfirmationDAO dao = db.offlineConfirmationDAO();
            List<OfflineConfirmation> offlineConfirmations = dao.getAllOfflineConfirmations();
            if (offlineConfirmations.size() > 0) {
              Log.i(TAG, "******* NOCH ZU BEARBEITEN......: " + offlineConfirmations.size() + " JOBS");
              Log.i(TAG, "******* ID......................: " + offlineConfirmations.get(0).getId());
              
              NotifyDao notifyDAO = db.notifyDao();
              LastActivityDAO lastActivityDAO = db.lastActivityDAO();
              for (int i = 0; i < offlineConfirmations.size(); i++) {
                final int j = i;
                notifyDAO.loadNotifyById(offlineConfirmations.get(i).getNotifyId()).observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.io())
                  .subscribe(new DisposableSingleObserver<Notify>() {
                    @Override
                    public void onSuccess(Notify notify) {
                      Log.i(TAG, "******* NOTIFY OID......: " + notify.getId());
  
                      CommItem commItemDB = App.getGson().fromJson(notify.getData(), CommItem.class);
                      CommItem commItemReq = new CommItem();
  
                      // SET HEADER:
                      Header header = new Header();
                      if (offlineConfirmations.get(j).getConfirmType() == ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal()) {
                        header.setDataType(DataType.ACTIVITY);
                      } else {
                        header.setDataType(DataType.CONFIRMATION);
                      }
                      header.setTimestampSenderUTC(commItemDB.getHeader().getTimestampSenderUTC());
                      commItemReq.setHeader(header);
                      
                      if (offlineConfirmations.get(j).getConfirmType() == ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal()) {
                        // SET ACTIVITY ITEM CHANGE:
                        ActivityItem activityItem = new ActivityItem();
                        activityItem.setTaskId(commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(j).getActivityId()).getTaskId());
                        activityItem.setMandantId(commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(j).getActivityId()).getMandantId());
                        activityItem.setActivityId(commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(j).getActivityId()).getActivityId());
                        activityItem.setName(commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(j).getActivityId()).getName());
                        activityItem.setDescription(commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(j).getActivityId()).getDescription());
                        if (commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(j).getActivityId()).getStatus().ordinal() == 0) {
                          activityItem.setStatus(ActivityStatus.PENDING);
                        } else if (commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(j).getActivityId()).getStatus().ordinal() == 1) {
                          activityItem.setStatus(ActivityStatus.RUNNING);
                          try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            String _format = sdf.format(commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(j).getActivityId()).getStarted());
                            Date started = sdf.parse(_format);
                            activityItem.setStarted(started);
                            
                            String _endFormat = sdf.format(commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(j).getActivityId()).getFinished());
                            Date finished = sdf.parse(_endFormat);
                            activityItem.setFinished(finished);
                          } catch (ParseException e) {
                            e.printStackTrace();
                          }
                        } else if (commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(j).getActivityId()).getStatus().ordinal() == 2) {
                          activityItem.setStatus(ActivityStatus.FINISHED);
                          try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            String _format = sdf.format(commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(j).getActivityId()).getStarted());
                            Date started = sdf.parse(_format);
                            activityItem.setStarted(started);
    
                            String _endFormat = sdf.format(commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(j).getActivityId()).getFinished());
                            Date finished = sdf.parse(_endFormat);
                            activityItem.setFinished(finished);
                          } catch (ParseException e) {
                            e.printStackTrace();
                          }
                        }
                        activityItem.setSequence(commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(j).getActivityId()).getSequence());
                        commItemReq.setActivityItem(activityItem);
                        
                        Call<ResultOfAction> call = App.apiManager.getActivityApi().activityChange(commItemReq);
                        call.enqueue(new Callback<ResultOfAction>() {
                          @Override
                          public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
                            if (response.isSuccessful()) {
                              if (response.body() != null && response.body().getIsSuccess()) {
                                AsyncTask.execute(new Runnable() {
                                  @Override
                                  public void run() {
                                    dao.delete(offlineConfirmations.get(j));
  
                                    lastActivityDAO.getLastActivityByTaskClientId(commItemDB.getTaskItem().getTaskId(), commItemDB.getTaskItem().getMandantId())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .subscribeOn(Schedulers.io())
                                      .subscribe(new DisposableSingleObserver<LastActivity>() {
                                        @Override
                                        public void onSuccess(LastActivity lastActivity) {
                                          
                                          lastActivity.setStatusType(2);
                                          lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
        
                                          ArrayList<String> _list = lastActivity.getDetailList();
        
                                          SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                                            Locale.getDefault());
                                          LastActivityDetails _detail = new LastActivityDetails();
                                          _detail.setDescription("CHANGED");
                                          _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                                          _list.add(App.getGson().toJson(_detail));
                                          lastActivity.setDetailList(_list);
        
                                          AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                              lastActivityDAO.update(lastActivity);
                                            }
                                          });
                                        }
      
                                        @Override
                                        public void onError(Throwable e) {
                                          // TODO:
                                        }
                                      });
                                  }
                                });
                              } else {
  
                                if (response.body().getCommItem() != null) {
                                  notify.setData(App.getGson().toJson(response.body().getCommItem()));
                                  notify.setRead(false);
                                  if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
                                    notify.setStatus(0);
                                  } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
                                    notify.setStatus(50);
                                  } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
                                    notify.setStatus(90);
                                  } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
                                    notify.setStatus(100);
                                  } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.BREAK)) {
                                    notify.setStatus(51);
                                  }
                                  notify.setTaskDueFinish(response.body().getCommItem().getTaskItem().getTaskDueDateFinish());
    
                                  AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                      notifyDAO.updateNotify(notify);
                                    }
                                  });
  
                                  // TODO: LAST ACTIVITY
                                  /*
                                  lastActivityDAO.getLastActivityByTaskClientId(commItemDB.getTaskItem().getTaskId(), commItemDB.getTaskItem().getMandantId())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new DisposableSingleObserver<LastActivity>() {
                                      @Override
                                      public void onSuccess(LastActivity lastActivity) {
                                        lastActivity.setStatusType(6);
                                        lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
        
                                        ArrayList<String> _list = lastActivity.getDetailList();
        
                                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                                          Locale.getDefault());
                                        LastActivityDetails _detail = new LastActivityDetails();
                                        _detail.setDescription("ACTIVITY CHANGED FROM ABONA");
                                        _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                                        _list.add(App.getGson().toJson(_detail));
                                        lastActivity.setDetailList(_list);
        
                                        AsyncTask.execute(new Runnable() {
                                          @Override
                                          public void run() {
                                            lastActivityDAO.update(lastActivity);
                                          }
                                        });
                                      }
      
                                      @Override
                                      public void onError(Throwable e) {
                                        // TODO:
                                      }
                                    });*/
                                } else {
                                  AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                      dao.delete(offlineConfirmations.get(j));
                                    }
                                  });
                                }
                              }
                            } else if (response.code() == 401) {
  
                              // error case:
                              switch (response.code()) {
                                case 401:
                                  handleAccessToken();
                                  break;
                              }
                            }
                          }
  
                          @Override
                          public void onFailure(Call<ResultOfAction> call, Throwable t) {
    
                          }
                        });
                      } else {
                        // SET CONFIRMATION ITEM:
                        ConfirmationItem confirmationItem = new ConfirmationItem();
                        if (offlineConfirmations.get(j).getConfirmType() == ConfirmationType.TASK_CONFIRMED_BY_DEVICE.ordinal()) {
                          confirmationItem.setConfirmationType(ConfirmationType.TASK_CONFIRMED_BY_DEVICE);
                        } else if (offlineConfirmations.get(j).getConfirmType() == ConfirmationType.TASK_CONFIRMED_BY_USER.ordinal()) {
                          confirmationItem.setConfirmationType(ConfirmationType.TASK_CONFIRMED_BY_USER);
                        }
  
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                          Locale.getDefault());
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date date = DateConverter.fromTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                        confirmationItem.setTimeStampConfirmationUTC(date);
                        confirmationItem.setMandantId(commItemDB.getTaskItem().getMandantId());
                        confirmationItem.setTaskId(commItemDB.getTaskItem().getTaskId());
                        confirmationItem.setTaskChangeId(commItemDB.getTaskItem().getTaskChangeId());
                        commItemReq.setConfirmationItem(confirmationItem);
  /*
                        MediaType mediaType = MediaType.parse("application/json");
                        RequestBody body = RequestBody.create(mediaType, App.getGson().toJson(commItemReq));
                        Request request = new Request.Builder()
                          .url("https://213.144.11.162:5000/api/confirmation/confirm")
                          .post(body)
                          .addHeader("Content-Type", "application/json")
                          .addHeader("Accept-Encoding", "gzip, deflate")
                          .addHeader("Connection", "keep-alive")
                          .addHeader("cache-control", "no-cache")
                          .addHeader("Authorization", "bearer " + TextSecurePreferences.getAccessToken(getApplicationContext()))
                          .build();
  
                        AsyncTask.execute(new Runnable() {
                          @Override
                          public void run() {
                            getOkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
        
                              @Override
                              public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                                if (response.isSuccessful()) {
                                  try {
                                    String jsonData = response.body().string().toString();
                                    JSONObject jobject = new JSONObject(jsonData);
              
                                    if (jobject.getBoolean("isSuccess")) {
                                      AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                          dao.delete(offlineConfirmations.get(j));
  
                                          lastActivityDAO.getLastActivityByTaskClientId(commItemDB.getTaskItem().getTaskId(), commItemDB.getTaskItem().getMandantId())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.io())
                                            .subscribe(new DisposableSingleObserver<LastActivity>() {
                                              @Override
                                              public void onSuccess(LastActivity lastActivity) {
        
                                                //Date timestamp = new Date();
                                                lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
        
                                                ArrayList<String> _list = lastActivity.getDetailList();
        
                                                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                                                  Locale.getDefault());
                                                LastActivityDetails _detail = new LastActivityDetails();
                                                if (offlineConfirmations.get(j).getConfirmType() == ConfirmationType.TASK_CONFIRMED_BY_DEVICE.ordinal()) {
                                                  _detail.setDescription("CONFIRMED BY DEVICE");
                                                  lastActivity.setConfirmStatus(1);
                                                } else if (offlineConfirmations.get(j).getConfirmType() == ConfirmationType.TASK_CONFIRMED_BY_USER.ordinal()) {
                                                  _detail.setDescription("CONFIRMED BY USER");
                                                  lastActivity.setConfirmStatus(2);
                                                }
                                                _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                                                _list.add(App.getGson().toJson(_detail));
                                                lastActivity.setDetailList(_list);
        
                                                AsyncTask.execute(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                    lastActivityDAO.update(lastActivity);
                                                  }
                                                });
                                              }
      
                                              @Override
                                              public void onError(Throwable e) {
                                                // TODO:
                                              }
                                            });
                                        }
                                      });
                                    } else {
                                    
                                    }
              
                                  } catch (NullPointerException e) {
                                    e.printStackTrace();
                                  } catch (JSONException e) {
                                    e.printStackTrace();
                                  }
                                } else {
            
                                  switch (response.code()) {
                                    case 401:
                                      handleAccessToken();
                                      break;
                                  }
                                }
                              }
        
                              @Override
                              public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
          
                              }
                            });
                          }
                        });
                        */
                        /*
                        MediaType mediaType = MediaType.parse("application/json");
                        RequestBody body = RequestBody.create(mediaType, App.getGson().toJson(commItemReq));
                        Request request = new Request.Builder()
                          .url("https://213.144.11.162:5000/api/confirmation/confirm")
                          .post(body)
                          .addHeader("Content-Type", "application/json")
                          .addHeader("Accept-Encoding", "gzip, deflate")
                          .addHeader("Connection", "keep-alive")
                          .addHeader("cache-control", "no-cache")
                          .addHeader("Authorization", "bearer " + TextSecurePreferences.getAccessToken(getApplicationContext()))
                          .build();
                        AsyncTask.execute(new Runnable() {
                          @Override
                          public void run() {
                            getOkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
  
                              @Override
                              public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                                if (response.isSuccessful()) {
                                  try {
                                    String jsonData = response.body().string().toString();
                                    JSONObject jobject = new JSONObject(jsonData);
                                    if (jobject.getBoolean("isSuccess")) {
                                      Log.i(TAG, "********************* TRUE TRUE TRUE");
                                    } else {
                                      Log.i(TAG, "********************* FALSE FALSE FALSE");
                                    }
                                    
                                  } catch (NullPointerException e) {
                                    e.printStackTrace();
                                  } catch (JSONException e) {
                                    e.printStackTrace();
                                  }
                                }
                              }
  
                              @Override
                              public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
    
                              }
                            });
                          }
                        });
                        */
                        
  
                        final Call<ResultOfAction> call = App.apiManager.getConfirmApi().confirm(commItemReq);
                        call.enqueue(new Callback<ResultOfAction>() {
                          @Override
                          public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
                            if (response.isSuccessful()) {
                              if (response.body() != null) {
                                
                                ResultOfAction resultOfAction = response.body();
                                
                                if (response.body().getIsSuccess()) {
                                  Log.i(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                                } else {
                                  Log.i(TAG, resultOfAction.getIsSuccess() ? "true" : "false");
                                }
                                
                                AsyncTask.execute(new Runnable() {
                                  @Override
                                  public void run() {
                                    dao.delete(offlineConfirmations.get(j));
              
                                    lastActivityDAO.getLastActivityByTaskClientId(commItemDB.getTaskItem().getTaskId(), commItemDB.getTaskItem().getMandantId())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .subscribeOn(Schedulers.io())
                                      .subscribe(new DisposableSingleObserver<LastActivity>() {
                                        @Override
                                        public void onSuccess(LastActivity lastActivity) {
                    
                                          //Date timestamp = new Date();
                                          lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
                    
                                          ArrayList<String> _list = lastActivity.getDetailList();
                    
                                          SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                                            Locale.getDefault());
                                          LastActivityDetails _detail = new LastActivityDetails();
                                          if (offlineConfirmations.get(j).getConfirmType() == ConfirmationType.TASK_CONFIRMED_BY_DEVICE.ordinal()) {
                                            _detail.setDescription("CONFIRMED BY DEVICE");
                                            lastActivity.setConfirmStatus(1);
                                          } else if (offlineConfirmations.get(j).getConfirmType() == ConfirmationType.TASK_CONFIRMED_BY_USER.ordinal()) {
                                            _detail.setDescription("CONFIRMED BY USER");
                                            lastActivity.setConfirmStatus(2);
                                          }
                                          _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                                          _list.add(App.getGson().toJson(_detail));
                                          lastActivity.setDetailList(_list);
                    
                                          AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                              lastActivityDAO.update(lastActivity);
                                            }
                                          });
                                        }
                  
                                        @Override
                                        public void onError(Throwable e) {
                                          // TODO:
                                        }
                                      });
                                  }
                                });
                              } else {
                                if (response.body().getCommItem() != null) {
                                  notify.setData(App.getGson().toJson(response.body().getCommItem()));
                                  notify.setRead(false);
                                  if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
                                    notify.setStatus(0);
                                  } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
                                    notify.setStatus(50);
                                  } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
                                    notify.setStatus(90);
                                  } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
                                    notify.setStatus(100);
                                  } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.BREAK)) {
                                    notify.setStatus(51);
                                  }
                                  notify.setTaskDueFinish(response.body().getCommItem().getTaskItem().getTaskDueDateFinish());
            
                                  AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                      notifyDAO.updateNotify(notify);
                                    }
                                  });
            
                                  // TODO: LAST ACTIVITY
                                  /*
                                  lastActivityDAO.getLastActivityByTaskClientId(commItemDB.getTaskItem().getTaskId(), commItemDB.getTaskItem().getMandantId())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new DisposableSingleObserver<LastActivity>() {
                                      @Override
                                      public void onSuccess(LastActivity lastActivity) {
                                        lastActivity.setStatusType(5);
                                        lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
  
                                        ArrayList<String> _list = lastActivity.getDetailList();
  
                                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                                          Locale.getDefault());
                                        LastActivityDetails _detail = new LastActivityDetails();
                                        _detail.setDescription("TASK CHANGED FROM ABONA");
                                        _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                                        _list.add(App.getGson().toJson(_detail));
                                        lastActivity.setDetailList(_list);
  
                                        AsyncTask.execute(new Runnable() {
                                          @Override
                                          public void run() {
                                            lastActivityDAO.update(lastActivity);
                                          }
                                        });
                                      }
  
                                      @Override
                                      public void onError(Throwable e) {
                                        // TODO:
                                      }
                                    });*/
                                } else {
                                  AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                      dao.delete(offlineConfirmations.get(j));
                                    }
                                  });
                                }
                              }
                            } else {
                              
                              // error case:
                              switch (response.code()) {
                                case 401:
                                  handleAccessToken();
                                  break;
                              }
                            }
                          }
    
                          @Override
                          public void onFailure(Call<ResultOfAction> call, Throwable t) {
                            // TODO:
                          }
                        });
                      }
                    }
  
                    @Override
                    public void onError(Throwable e) {
                      AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                          dao.delete(offlineConfirmations.get(j));
                        }
                      });
                    }
                  });
              }
              /*
              if (offlineConfirmations.get(0).getConfirmType() == ConfirmationType.TASK_CONFIRMED_BY_DEVICE.ordinal()) {
                Log.i(TAG, "******* CONFIRMATION BY DEVICE..: ");
  
                NotifyDao notifyDAO = db.notifyDao();
                LastActivityDAO lastActivityDAO = db.lastActivityDAO();
                notifyDAO.loadNotifyById(offlineConfirmations.get(0).getNotifyId()).observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.io())
                  .subscribe(new DisposableSingleObserver<Notify>() {
                    @Override
                    public void onSuccess(Notify notify) {
                      Log.i(TAG, "******* NOTIFY OID......: " + notify.getId());
                      
                      CommItem commItemDB = App.getGson().fromJson(notify.getData(), CommItem.class);
                      CommItem commItemReq = new CommItem();
                      
                      // SET HEADER:
                      Header header = new Header();
                      header.setDataType(DataType.CONFIRMATION);
                      header.setTimestampSenderUTC(commItemDB.getHeader().getTimestampSenderUTC());
                      commItemReq.setHeader(header);
                      
                      // SET CONFIRMATION ITEM:
                      ConfirmationItem confirmationItem = new ConfirmationItem();
                      confirmationItem.setConfirmationType(ConfirmationType.TASK_CONFIRMED_BY_DEVICE);
                      Date date = DateConverter.fromTimestamp(new Date().toString());
                      confirmationItem.setTimeStampConfirmationUTC(date);
                      confirmationItem.setMandantId(commItemDB.getTaskItem().getMandantId());
                      confirmationItem.setTaskId(commItemDB.getTaskItem().getTaskId());
                      confirmationItem.setTaskChangeId(commItemDB.getTaskItem().getTaskChangeId());
                      commItemReq.setConfirmationItem(confirmationItem);
  
                      final Call<ResultOfAction> call = App.apiManager.getConfirmApi().confirm(commItemReq);
                      call.enqueue(new Callback<ResultOfAction>() {
                        @Override
                        public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
                          if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getIsSuccess()) {
                              AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                  dao.delete(offlineConfirmations.get(0));
                                  
                                  lastActivityDAO.getLastActivityByTaskClientId(commItemDB.getTaskItem().getTaskId(), commItemDB.getTaskItem().getMandantId())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new DisposableSingleObserver<LastActivity>() {
                                      @Override
                                      public void onSuccess(LastActivity lastActivity) {
                                        lastActivity.setConfirmStatus(1);
                                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                                          Locale.getDefault());
                                        Date timestamp = new Date();
                                        lastActivity.setModifiedAt(sdf.format(timestamp));
  
                                        ArrayList<String> _list = lastActivity.getDetailList();
  
                                        LastActivityDetails _detail = new LastActivityDetails();
                                        _detail.setDescription("CONFIRMED BY DEVICE");
                                        _detail.setTimestamp(sdf.format(timestamp));
                                        _list.add(App.getGson().toJson(_detail));
                                        lastActivity.setDetailList(_list);
                                        
                                        AsyncTask.execute(new Runnable() {
                                          @Override
                                          public void run() {
                                            lastActivityDAO.update(lastActivity);
                                          }
                                        });
                                      }
  
                                      @Override
                                      public void onError(Throwable e) {
    
                                      }
                                    });
                                }
                              });
                            } else {
                              if (response.body().getCommItem() != null) {
                                notify.setData(App.getGson().toJson(response.body().getCommItem()));
                                notify.setRead(false);
                                if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
                                  notify.setStatus(0);
                                } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
                                  notify.setStatus(50);
                                } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
                                  notify.setStatus(90);
                                } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
                                  notify.setStatus(100);
                                } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.BREAK)) {
                                  notify.setStatus(51);
                                }
                                notify.setTaskDueFinish(response.body().getCommItem().getTaskItem().getTaskDueDateFinish());
                                
                                AsyncTask.execute(new Runnable() {
                                  @Override
                                  public void run() {
                                    notifyDAO.updateNotify(notify);
                                  }
                                });
                              } else {
                                AsyncTask.execute(new Runnable() {
                                  @Override
                                  public void run() {
                                    dao.delete(offlineConfirmations.get(0));
                                  }
                                });
                              }
                            }
                          }
                        }
    
                        @Override
                        public void onFailure(Call<ResultOfAction> call, Throwable t) {
                        }
                      });
                    }
  
                    @Override
                    public void onError(Throwable e) {
                      AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                          dao.delete(offlineConfirmations.get(0));
                        }
                      });
                    }
                  });
              } else if (offlineConfirmations.get(0).getConfirmType() == ConfirmationType.TASK_CONFIRMED_BY_USER.ordinal()) {
                Log.i(TAG, "******* CONFIRMATION BY USER..: ");
                
                NotifyDao notifyDao = db.notifyDao();
                LastActivityDAO lastActivityDAO = db.lastActivityDAO();
                notifyDao.loadNotifyById(offlineConfirmations.get(0).getNotifyId()).observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.io())
                  .subscribe(new DisposableSingleObserver<Notify>() {
                    @Override
                    public void onSuccess(Notify notify) {
                      Log.i(TAG, "******* NOTIFY OID......: " + notify.getId());
  
                      CommItem commItemDB = App.getGson().fromJson(notify.getData(), CommItem.class);
                      CommItem commItemReq = new CommItem();
  
                      // SET HEADER:
                      Header header = new Header();
                      header.setDataType(DataType.CONFIRMATION);
                      header.setTimestampSenderUTC(commItemDB.getHeader().getTimestampSenderUTC());
                      commItemReq.setHeader(header);
  
                      // SET CONFIRMATION ITEM:
                      ConfirmationItem confirmationItem = new ConfirmationItem();
                      confirmationItem.setConfirmationType(ConfirmationType.TASK_CONFIRMED_BY_USER);
                      Date date = DateConverter.fromTimestamp(new Date().toString());
                      confirmationItem.setTimeStampConfirmationUTC(date);
                      confirmationItem.setMandantId(commItemDB.getTaskItem().getMandantId());
                      confirmationItem.setTaskId(commItemDB.getTaskItem().getTaskId());
                      confirmationItem.setTaskChangeId(commItemDB.getTaskItem().getTaskChangeId());
                      commItemReq.setConfirmationItem(confirmationItem);
  
                      final Call<ResultOfAction> call = App.apiManager.getConfirmApi().confirm(commItemReq);
                      call.enqueue(new Callback<ResultOfAction>() {
                        @Override
                        public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
                          if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getIsSuccess()) {
                              AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                  dao.delete(offlineConfirmations.get(0));
  
                                  lastActivityDAO.getLastActivityByTaskClientId(commItemDB.getTaskItem().getTaskId(), commItemDB.getTaskItem().getMandantId())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new DisposableSingleObserver<LastActivity>() {
                                      @Override
                                      public void onSuccess(LastActivity lastActivity) {
                                        lastActivity.setConfirmStatus(2);
                                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                                          Locale.getDefault());
                                        Date timestamp = new Date();
                                        lastActivity.setModifiedAt(sdf.format(timestamp));
        
                                        ArrayList<String> _list = lastActivity.getDetailList();
        
                                        LastActivityDetails _detail = new LastActivityDetails();
                                        _detail.setDescription("CONFIRMED BY USER");
                                        _detail.setTimestamp(sdf.format(timestamp));
                                        _list.add(App.getGson().toJson(_detail));
                                        lastActivity.setDetailList(_list);
        
                                        AsyncTask.execute(new Runnable() {
                                          @Override
                                          public void run() {
                                            lastActivityDAO.update(lastActivity);
                                          }
                                        });
                                      }
      
                                      @Override
                                      public void onError(Throwable e) {
        
                                      }
                                    });
                                }
                              });
                            } else {
                              if (response.body().getCommItem() != null) {
                                notify.setData(App.getGson().toJson(response.body().getCommItem()));
                                notify.setRead(false);
                                if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
                                  notify.setStatus(0);
                                } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
                                  notify.setStatus(50);
                                } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
                                  notify.setStatus(90);
                                } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
                                  notify.setStatus(100);
                                } else if (response.body().getCommItem().getTaskItem().getTaskStatus().equals(TaskStatus.BREAK)) {
                                  notify.setStatus(51);
                                }
                                notify.setTaskDueFinish(response.body().getCommItem().getTaskItem().getTaskDueDateFinish());
    
                                AsyncTask.execute(new Runnable() {
                                  @Override
                                  public void run() {
                                    notifyDao.updateNotify(notify);
                                  }
                                });
                              } else {
                                AsyncTask.execute(new Runnable() {
                                  @Override
                                  public void run() {
                                    dao.delete(offlineConfirmations.get(0));
                                  }
                                });
                              }
                            }
                          }
                        }
    
                        @Override
                        public void onFailure(Call<ResultOfAction> call, Throwable t) {
                        }
                      });
                    }
  
                    @Override
                    public void onError(Throwable e) {
                      AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                          dao.delete(offlineConfirmations.get(0));
                        }
                      });
                    }
                  });
              } else if (offlineConfirmations.get(0).getConfirmType() == ConfirmationType.ACTIVITY_CONFIRMED_BY_DEVICE.ordinal()) {
                Log.i(TAG, "******* CONFIRMATION ACTIVITY BY DEVICE..: ");
                AsyncTask.execute(new Runnable() {
                  @Override
                  public void run() {
                    dao.delete(offlineConfirmations.get(0));
                  }
                });
              }*/
            }
          }
        }
      }
    };
  }
  
  public void stopTimerTask() {
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
    }
  }
  
  @Override
  public void onTaskRemoved(Intent rootIntent) {
    Intent intent = new Intent(getApplicationContext(), this.getClass());
    intent.setPackage(getPackageName());
  
    PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),
      1, intent, PendingIntent.FLAG_ONE_SHOT);
    AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    alarmService.set(
      AlarmManager.ELAPSED_REALTIME,
      SystemClock.elapsedRealtime() + 1000,
      pendingIntent
    );
    
    super.onTaskRemoved(rootIntent);
  }
  
  @Override
  public void onLowMemory() {
    super.onLowMemory();
    Log.i(TAG, "onLowMemory() called!");
  }
  
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
  
  private void startNetworkBroadcastReceiver(Context ctx) {
    mNetworkStateReceiver = new NetworkStateReceiver();
    mNetworkStateReceiver.addListener((NetworkStateReceiver.NetworkStateReceiverListener)ctx);
    registerNetworkBroadcastReceiver(ctx);
  }
  
  /**
   * Register the NetworkStateReceiver with your activity.
   * @param ctx
   */
  public void registerNetworkBroadcastReceiver(Context ctx) {
    ctx.registerReceiver(mNetworkStateReceiver,
      new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
  }
  
  /**
   * Unregister the NetworkStateReceiver with your Service.
   * @param ctx
   */
  public void unregisterNetworkBroadcastReceiver(Context ctx) {
    ctx.unregisterReceiver(mNetworkStateReceiver);
  }
  
  @Override
  public void networkAvailable() {
    Log.i(TAG, "networkAvailable()");
    fConnected = true;
  }
  
  @Override
  public void networkUnavailable() {
    Log.i(TAG, "networkUnavailable()");
    fConnected = false;
  }
  
  private void handleConfirmation(CommItem commItem) {
    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create(mediaType, App.getGson().toJson(commItem));
    Request request = new Request.Builder()
      .url("https://213.144.11.162:5000/api/confirmation/confirm")
      .post(body)
      .addHeader("Content-Type", "application/json")
      .addHeader("Accept-Encoding", "gzip, deflate")
      .addHeader("Connection", "keep-alive")
      .addHeader("cache-control", "no-cache")
      .addHeader("Authorization", "bearer " + TextSecurePreferences.getAccessToken(getApplicationContext()))
      .build();
  
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        getOkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
        
          @Override
          public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
              try {
                String jsonData = response.body().string().toString();
                JSONObject jobject = new JSONObject(jsonData);
                
                if (jobject.getBoolean("isSuccess")) {
                  AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
    
                    }
                  });
                } else {
                
                }
                
              } catch (NullPointerException e) {
                e.printStackTrace();
              } catch (JSONException e) {
                e.printStackTrace();
              }
            } else {
              
              switch (response.code()) {
                case 401:
                  handleAccessToken();
                  break;
              }
            }
          }
        
          @Override
          public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
          
          }
        });
      }
    });
  }
  
  private void handleAccessToken() {
    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    RequestBody body = RequestBody.create(mediaType, "grant_type=password&username=manyvehicles%40abona-erp.com&password=1234qwerQWER%2C.-");
  
    Request request = new Request.Builder()
      .url("https://213.144.11.162:5000/authentication")
      .post(body)
      .addHeader("Content-Type", "application/x-www-form-urlencoded")
      .addHeader("Accept-Encoding", "gzip, deflate")
      .addHeader("Content-Length", "84")
      .addHeader("Connection", "keep-alive")
      .addHeader("cache-control", "no-cache")
      .build();
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        
        getOkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
          @Override
          public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
    
          }
  
          @Override
          public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
              try {
                String jsonData = response.body().string().toString();
                JSONObject jobject = new JSONObject(jsonData);
                //Log.i(TAG, "ACCESS_TOKEN " + jobject.getString("access_token"));
                String access_token = jobject.getString("access_token");
                if (!TextUtils.isEmpty(access_token)) {
                  TextSecurePreferences.setAccessToken(getApplicationContext(), access_token);
                }
              } catch (NullPointerException e) {
                e.printStackTrace();
              } catch (JSONException e) {
                e.printStackTrace();
              }
            }
          }
        });
  /*
        try (okhttp3.Response response = getOkHttpClient().newCall(request).execute()) {
          ResponseBody body = response.body();
          Log.i(TAG, body.toString());
        } catch (IOException e) {
          e.printStackTrace();
        }*/
      }
    });
  }
  
  OkHttpClient okHttpClient = null;
  private OkHttpClient getOkHttpClient() {
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    
    if (okHttpClient == null) {
      synchronized (ServiceWorker.class) {
        if (okHttpClient == null) {
          okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .hostnameVerifier(new HostnameVerifier() {
              @Override
              public boolean verify(String s, SSLSession sslSession) {
                return true;
              }
            })
            .sslSocketFactory(getSslSocket())
            .addInterceptor(logging)
            .build();
        }
      }
    }
    return okHttpClient;
  }
  
  private SSLSocketFactory getSslSocket() {
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      X509TrustManager tm = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        
        }
        
        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[0];
        }
      };
      sslContext.init(null, new TrustManager[]{tm}, null);
      return sslContext.getSocketFactory();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
