package com.abona_erp.driver.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.converters.DateConverter;
import com.abona_erp.driver.app.data.converters.LogLevel;
import com.abona_erp.driver.app.data.converters.LogType;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.dao.LastActivityDAO;
import com.abona_erp.driver.app.data.dao.LogDAO;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.dao.OfflineDelayReasonDAO;
import com.abona_erp.driver.app.data.entity.ActionType;
import com.abona_erp.driver.app.data.entity.ChangeHistoryState;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.LogItem;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.entity.OfflineDelayReasonEntity;
import com.abona_erp.driver.app.data.model.ActivityDelayItem;
import com.abona_erp.driver.app.data.model.ActivityItem;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ConfirmationItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.DMSDocumentType;
import com.abona_erp.driver.app.data.model.DataType;
import com.abona_erp.driver.app.data.model.DelayReasonItem;
import com.abona_erp.driver.app.data.model.DelaySource;
import com.abona_erp.driver.app.data.model.DeviceProfileItem;
import com.abona_erp.driver.app.data.model.Header;
import com.abona_erp.driver.app.data.model.LastActivityDetails;
import com.abona_erp.driver.app.data.model.ResultOfAction;
import com.abona_erp.driver.app.data.model.SpecialActivities;
import com.abona_erp.driver.app.data.model.SpecialActivityResult;
import com.abona_erp.driver.app.data.model.SpecialFunction;
import com.abona_erp.driver.app.data.model.SpecialFunctionOperationType;
import com.abona_erp.driver.app.data.model.TaskItem;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.data.model.UploadItem;
import com.abona_erp.driver.app.data.model.UploadResult;
import com.abona_erp.driver.app.data.remote.NetworkUtil;
import com.abona_erp.driver.app.data.remote.client.UnsafeOkHttpClient;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.ChangeHistoryEvent;
import com.abona_erp.driver.app.ui.event.DocumentEvent;
import com.abona_erp.driver.app.ui.event.GetAllTaskEvent;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.event.PatchEvent;
import com.abona_erp.driver.app.ui.event.ProgressBarEvent;
import com.abona_erp.driver.app.ui.event.RegistrationEvent;
import com.abona_erp.driver.app.ui.event.RestApiErrorEvent;
import com.abona_erp.driver.app.ui.event.UploadAllDocsEvent;
import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DeviceUtils;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.UtilCommon;
import com.abona_erp.driver.core.base.ContextUtils;
import com.abona_erp.driver.core.util.MiscUtil;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class BackgroundServiceWorker extends Service {
  
  private static final String TAG = MiscUtil.getTag(BackgroundServiceWorker.class);

  private Handler mHandler;
  private Handler mDelayReasonHandler;
  private Runner  mRunner;
  private DelayReasonRunner mDelayReasonRunner;
  
  private DriverDatabase mDB = DriverDatabase.getDatabase();
  private DeviceProfileDAO mDeviceProfileDAO = mDB.deviceProfileDAO();
  private OfflineConfirmationDAO mOfflineConfirmationDAO = mDB.offlineConfirmationDAO();
  private NotifyDao mNotifyDAO = mDB.notifyDao();
  private LastActivityDAO mLastActivityDAO = mDB.lastActivityDAO();
  private OfflineDelayReasonDAO mOfflineDelayReasonDAO = mDB.offlineDelayReasonDAO();
  private LogDAO mLogDao = mDB.logDAO();
  
  public BackgroundServiceWorker() {
  }
  
  public class DelayReasonRunner implements Runnable {
    @Override
    public void run() {
    
    }
  }
  
  private volatile static int delay = 7000;
  public volatile static boolean allowRequest = true;
  public volatile static boolean registrationRequest = false;
  public volatile static boolean requestIsRunning = false;
  public volatile static int requestCounter = 0;
  public class Runner implements Runnable {
    @Override
    public void run() {
      Log.i(TAG, ">>>>>>> BACKGROUND SERVICE LISTENING... >>>>>>>");
      
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
  /*
          TextSecurePreferences.setPatch00_State(0);
          TextSecurePreferences.setPatch00_RandomNumber(60);
          TextSecurePreferences.setPatch00_Completed(false);
          */
          
  
  /*
          if (!isPatch00_Completed()) {
            Log.i(TAG, "******* PATCH IS RUNNING... **********");
            allowRequest = true;
            mHandler.postDelayed(this, 10000);
            return;
          }
          
   */
          
          if (!isDevicePermissionGranted()) {
            Log.i(TAG, "******* DEVICE PERMISSION IS NOT GRANTED!!! *******");
            allowRequest = true;
            mHandler.postDelayed(this, delay);
            return;
          }
          
          //if (TextSecurePreferences.isStopService())
          //  return;
          
          if (!isDeviceRegistrated()) {
            allowRequest = true;
            mHandler.postDelayed(this, delay);
            return;
          }
          
          if (!isDeviceUpdateToken()) {
            allowRequest = true;
            mHandler.postDelayed(this, delay);
            return;
          }
          /*
          if (!isUpdateDevice()) {
            allowRequest = true;
            mHandler.postDelayed(this, delay);
            return;
          }
          */
          if (!TextUtils.isEmpty(TextSecurePreferences.getEndpoint())) {
            String endpoint = TextSecurePreferences.getEndpoint();
            String ip = "";
            if (endpoint.contains("https://")) {
              ip = endpoint.substring(8, endpoint.lastIndexOf(':'));
              if (!pingServer(ip)) {
                Log.e("BWS", "Server nicht erreichbar...");
                mHandler.postDelayed(this, delay);
                return;
              }
            }
          }
          
          if (TextSecurePreferences.isUpdateLangCode()) {
            updateLangCode();
            mHandler.postDelayed(this, delay);
            return;
          } /*else if (TextSecurePreferences.isUpdateAllTasks()) {
            //EventBus.getDefault().post(new LogEvent(getString(R.string.log_update_schedule), LogType.SERVER_TO_APP, LogLevel.INFO, getString(R.string.log_title_get_tasks_bg), 0));
            updateGetAllTasks();
            mHandler.postDelayed(this, delay);
            return;
          } *//*else if (TextSecurePreferences.isUpdateDelayReason()) {
            DelayReasonUtil.getDelayReasonsFromService(TextSecurePreferences.getMandantID());
            mHandler.postDelayed(this, delay);
            return;
          }*/
          
          AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
              List<OfflineConfirmation> offlineConfirmations =
                mOfflineConfirmationDAO.getAllOfflineConfirmations();
  
              List<OfflineDelayReasonEntity> offlineDelayReasonEntities =
                mOfflineDelayReasonDAO.getAllOfflineDelayReasons();
              
              if (!allowRequest && (offlineConfirmations.size() > 0 || offlineDelayReasonEntities.size() > 0)) {
                /*
                for (int n = 0; n < offlineConfirmations.size(); n++) {
                  Log.i("TABLE ", "Id: " + offlineConfirmations.get(n).getId()
                      + " NotifyId: " + offlineConfirmations.get(n).getNotifyId()
                      + " ActivityId: " + offlineConfirmations.get(n).getActivityId()
                      + " ConfirmType: " + offlineConfirmations.get(n).getConfirmType()
                      + " UploadFlag: " + offlineConfirmations.get(n).getUploadFlag());
                }
                */
                allowRequest = true;
              }
            }
          });
          
          if (allowRequest) {
            allowRequest = false;
            Log.i(TAG, "allowRequest " + allowRequest);
          } else {
            Log.i(TAG, "allowRequest " + allowRequest);
            mHandler.postDelayed(this, delay);
            return;
          }
  
          handleJobs();
  
          mHandler.postDelayed(this, delay);
        }
      });
    }
  }
  
  private void handleJobs() {
  
    handleConfirmationJob();
    handleDelayReasonJob();
    handleUploadJob();
  }

  private void handleDelayReasonJob() {
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
    
        List<OfflineDelayReasonEntity> offlineDelayReasonEntities = mOfflineDelayReasonDAO.getAllOfflineDelayReasons();
        if (offlineDelayReasonEntities.size() > 0) {
          
          Log.i(">>>>>>>>>>", "Delay Reasons vorhanden, wird abgearbeitet..." + offlineDelayReasonEntities.get(0).getInProgress());
          if (offlineDelayReasonEntities.get(0).getInProgress() == 0) {
            
            OfflineDelayReasonEntity entity = offlineDelayReasonEntities.get(0);
            entity.setInProgress(1);
            AsyncTask.execute(new Runnable() {
              @Override
              public void run() {
                mOfflineDelayReasonDAO.update(entity);
              }
            });
            
            CommItem reqItem = new CommItem();
            Header header = new Header();
            header.setTimestampSenderUTC(new Date());
            header.setDataType(DataType.DELAY_REASONS);
            header.setDeviceId(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
            reqItem.setHeader(header);
  
            DelayReasonItem delayReasonItem = new DelayReasonItem();
            delayReasonItem.setWaitingReasongId(offlineDelayReasonEntities.get(0).getWaitingReasonId());
            delayReasonItem.setWaitingReasonAppId(offlineDelayReasonEntities.get(0).getWaitingReasonAppId());
            delayReasonItem.setActivityId(offlineDelayReasonEntities.get(0).getActivityId());
            delayReasonItem.setMandantId(offlineDelayReasonEntities.get(0).getMandantId());
            delayReasonItem.setTaskId(offlineDelayReasonEntities.get(0).getTaskId());
            delayReasonItem.setTimestampUtc(offlineDelayReasonEntities.get(0).getTimestamp());
            delayReasonItem.setDelayInMinutes(offlineDelayReasonEntities.get(0).getDelayInMinutes());
            if (offlineDelayReasonEntities.get(0).getDelaySource() == 0) {
              delayReasonItem.setDelaySource(DelaySource.NA);
            } else if (offlineDelayReasonEntities.get(0).getDelaySource() == 1) {
              delayReasonItem.setDelaySource(DelaySource.DISPATCHER);
            } else if (offlineDelayReasonEntities.get(0).getDelaySource() == 2) {
              delayReasonItem.setDelaySource(DelaySource.CUSTOMER);
            } else if (offlineDelayReasonEntities.get(0).getDelaySource() == 3) {
              delayReasonItem.setDelaySource(DelaySource.DRIVER);
            }
            delayReasonItem.setComment(offlineDelayReasonEntities.get(0).getComment());
  
            List<DelayReasonItem> items = new ArrayList<>();
            items.add(delayReasonItem);
            //reqItem.setDelayReasonItems(items);
  
            ActivityDelayItem activityDelayItem = new ActivityDelayItem();
            activityDelayItem.setMandantId(offlineDelayReasonEntities.get(0).getMandantId());
            activityDelayItem.setTaskId(offlineDelayReasonEntities.get(0).getTaskId());
            activityDelayItem.setActivityId(offlineDelayReasonEntities.get(0).getActivityId());
            activityDelayItem.setDelayReasonItems(items);
            reqItem.setActivityDelayItem(activityDelayItem);
  
            Call<ResultOfAction> call = App.getInstance().apiManager
              .getDelayReasonApi().setDelayReasons(reqItem);
            call.enqueue(new Callback<ResultOfAction>() {
              @Override
              public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
                // Log.i(TAG, response.body().getCommItem().toString());
      
                if (response.body() == null) {
                  AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                      mOfflineDelayReasonDAO.delete(offlineDelayReasonEntities.get(0));
                    }
                  });
                  return;
                }
                if (response.body().getIsSuccess() && !response.body().getIsException()) {
        
                  ResultOfAction resultOfAction = response.body();
                  if (resultOfAction == null) return;
                  
                  mNotifyDAO.loadNotifyById(offlineDelayReasonEntities.get(0).getNotifyId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DisposableSingleObserver<Notify>() {
                      @Override
                      public void onSuccess(Notify notify) {
              
                        CommItem commItem = App.getInstance().gson.fromJson(notify.getData(), CommItem.class);
                        //commItem.setDelayReasonItems(resultOfAction.getDelayReasonItems());
                        List<ActivityItem> activities = commItem.getTaskItem().getActivities();
                        for (int i = 0; i < activities.size(); i++) {
                          if (activities.get(i).getActivityId() == resultOfAction.getActivityDelayItem().getActivityId()) {
                            activities.get(i).setDelayReasonItems(resultOfAction.getActivityDelayItem().getDelayReasonItems());
                          }
                        }
                        notify.setData(App.getInstance().gson.toJson(commItem));
                        AsyncTask.execute(new Runnable() {
                          @Override
                          public void run() {
                            mNotifyDAO .updateNotify(notify);
                            mOfflineDelayReasonDAO.delete(offlineDelayReasonEntities.get(0));
                          }
                        });
                      }
            
                      @Override
                      public void onError(Throwable e) {
                        AsyncTask.execute(new Runnable() {
                          @Override
                          public void run() {
                            mOfflineDelayReasonDAO.delete(offlineDelayReasonEntities.get(0));
                          }
                        });
                      }
                    });
                } else {
                  // Zurücksetzen.
                  
                  OfflineDelayReasonEntity entity = offlineDelayReasonEntities.get(0);
                  entity.setInProgress(0);
                  AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                      //mOfflineDelayReasonDAO.update(entity);
                      mOfflineDelayReasonDAO.delete(entity);
                    }
                  });
                }
              }
    
              @Override
              public void onFailure(Call<ResultOfAction> call, Throwable t) {
                // Zurücksetzen.
                OfflineDelayReasonEntity entity = offlineDelayReasonEntities.get(0);
                entity.setInProgress(0);
                AsyncTask.execute(new Runnable() {
                  @Override
                  public void run() {
                    mOfflineDelayReasonDAO.update(entity);
                  }
                });
              }
            });
          }
        }
      }
    });
  }
  
  private void handleConfirmationJob() {
    
    if (!NetworkUtil.isConnected(getApplicationContext())) {
      addLog(LogLevel.WARNING, LogType.APP_TO_SERVER, "BWS", "No Internet Connection!");
      return;
    }
  
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
  
        List<OfflineConfirmation> offlineConfirmations = mOfflineConfirmationDAO.getAllOfflineConfirmations();
        if (offlineConfirmations.size() > 0) {
          Log.i(TAG, ">>>>>>> NOCH ZU BEARBEITEN......: " + offlineConfirmations.size() + " JOBS");
          Log.i(TAG, ">>>>>>> ID......................: " + offlineConfirmations.get(0).getId());
          
          if (offlineConfirmations.get(0).getUploadFlag() == 1) {
            addLog(LogLevel.INFO, LogType.APP_TO_SERVER, "WAITING", "Warte, Bild wird hochgeladen...");
            return;
          }
  
          if (requestCounter > 20) {
    
            AsyncTask.execute(new Runnable() {
              @Override
              public void run() {
                mOfflineConfirmationDAO.delete(offlineConfirmations.get(0));
                requestIsRunning = false;
                requestCounter = 0;
              }
            });
          }
  
          if (requestIsRunning) {
            Log.i(TAG, ">>>>>>>>>> WAITING FOR RESPONSE...");
            return;
          }
  
          mNotifyDAO.loadNotifyById(offlineConfirmations.get(0).getNotifyId())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new DisposableSingleObserver<Notify>() {
              @Override
              public void onSuccess(Notify notify) {
                Log.i(TAG, ">>>>>>> NOTIFY OID......: " + notify.getId());
  
                requestIsRunning = true;
                requestCounter++;
  
                CommItem commItemDB = App.getInstance().gson.fromJson(notify.getData(), CommItem.class);
                CommItem commItemReq = new CommItem();
  
                StringBuilder builder1 = new StringBuilder();
                builder1.append("Handler Next Job - Task ID: ");
                builder1.append(String.valueOf(notify.getTaskId()));
                builder1.append(" - Order No: ");
                builder1.append(String.valueOf(notify.getOrderNo()));
                builder1.append(" - Mandant: ");
                builder1.append(String.valueOf(notify.getMandantId()));
                addLog(LogLevel.INFO, LogType.APP_TO_SERVER, "BWS", builder1.toString());
                
                // SET HEADER:
                Header header = new Header();
                if (offlineConfirmations.get(0).getConfirmType() == ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal()) {
                  header.setDataType(DataType.ACTIVITY);
                } else {
                  header.setDataType(DataType.CONFIRMATION);
                }
                header.setDeviceId(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
                header.setTimestampSenderUTC(new Date());
                commItemReq.setHeader(header);
  
                if (offlineConfirmations.get(0).getConfirmType() == ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal()) {
                  
                  OfflineConfirmation apiJob = offlineConfirmations.get(0);
                  ActivityItem _currActivity = commItemDB.getTaskItem().getActivities().get(apiJob.getActivityId());
                  
                  // SET ACTIVITY ITEM CHANGE:
                  ActivityItem activityItem = new ActivityItem();
                  activityItem.setDeviceId(DeviceUtils.getUniqueIMEI(getApplicationContext()));
                  activityItem.setTaskId(_currActivity.getTaskId());
                  activityItem.setMandantId(_currActivity.getMandantId());
                  activityItem.setActivityId(_currActivity.getActivityId());
                  activityItem.setName(_currActivity.getName());
                  activityItem.setDescription(_currActivity.getDescription());
                  
                  //Date minDate = new Date(2018-1900, 1, 21);
                  
                  if (apiJob.getActivityStatus() == 1) {
                  
                    //if (_currActivity.getStarted() == null || _currActivity.getStarted().before(minDate)) {
                    //  _currActivity.setStarted(AppUtils.getCurrentDateTimeUtc());
                    //}
                    activityItem.setStarted(_currActivity.getStarted());
                    activityItem.setStatus(ActivityStatus.RUNNING);
                    /*
                    if (_currActivity.getSpecialActivities() != null) {
                      if (_currActivity.getSpecialActivities().get(0).getSpecialFunctionOperationType().equals(SpecialFunctionOperationType.ON_FINISH_OF_ACTIVITY)) {
                        activityItem.setFinished(AppUtils.getCurrentDateTimeUtc());
                      }
                    }
                    */
                    
                    if (_currActivity.getSpecialActivities() != null) {
                      int saSize = _currActivity.getSpecialActivities().size();
                      if (saSize > 0) {
                        for (int i = 0; i < saSize; i++) {
                          SpecialActivities sa = _currActivity.getSpecialActivities().get(i);
                          if (sa.getSpecialFunctionOperationType().equals(SpecialFunctionOperationType.ON_START_OF_ACTIVITY)) {
                            if (sa.getSpecialFunction().equals(SpecialFunction.STANDARD) || sa.getSpecialFunction().equals(SpecialFunction.SCAN_BARCODE))
                              continue;
                            
                            if (sa.getSpecialActivityResults() != null) {
                              int resultOfSize = sa.getSpecialActivityResults().size();
                              if (resultOfSize > 0) {
                                for (int j = 0; j < resultOfSize; j++) {
    
                                  SpecialActivityResult sar = sa.getSpecialActivityResults().get(j);
                                  if (sar.getSpecialFunctionFinished() != null)
                                    continue;
    
                                  //_currActivity.getSpecialActivities().get(i).getSpecialActivityResults().get(j).setSpecialFunctionFinished(new Date());
                                  commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(0).getActivityId()).getSpecialActivities().get(i).getSpecialActivityResults().get(j).setSpecialFunctionFinished(new Date());
                                  updateTask(notify, commItemDB);
                                  uploadSpecialFunctionImages(sar.getResultString1(), String.valueOf(_currActivity.getMandantId()), String.valueOf(notify.getOrderNo()), String.valueOf(notify.getTaskId()), sa.getSpecialFunction());
                                }
                              }
                            }
                            activityItem.setSpecialActivities(_currActivity.getSpecialActivities());
                          }
                        }
                      }
                      
                    }
                  } else if (apiJob.getActivityStatus() == 2) {
                    //if (_currActivity.getFinished() == null || _currActivity.getFinished().before(minDate)) {
                    //  _currActivity.setFinished(AppUtils.getCurrentDateTimeUtc());
                    //}
                    activityItem.setStarted(_currActivity.getStarted());
                    activityItem.setFinished(_currActivity.getFinished());
                    activityItem.setStatus(ActivityStatus.FINISHED);
  
                    if (_currActivity.getSpecialActivities() != null) {
                      int saSize = _currActivity.getSpecialActivities().size();
                      if (saSize > 0) {
                        for (int i = 0; i < saSize; i++) {
                          SpecialActivities sa = _currActivity.getSpecialActivities().get(i);
                          if (sa.getSpecialFunctionOperationType().equals(SpecialFunctionOperationType.ON_FINISH_OF_ACTIVITY)) {
                            if (sa.getSpecialFunction().equals(SpecialFunction.STANDARD) || sa.getSpecialFunction().equals(SpecialFunction.SCAN_BARCODE))
                              continue;
          
                            if (sa.getSpecialActivityResults() != null) {
                              int resultOfSize = sa.getSpecialActivityResults().size();
                              if (resultOfSize > 0) {
                                for (int j = 0; j < resultOfSize; j++) {
                
                                  SpecialActivityResult sar = sa.getSpecialActivityResults().get(j);
                                  if (sar.getSpecialFunctionFinished() != null)
                                    continue;
                
                                  //_currActivity.getSpecialActivities().get(i).getSpecialActivityResults().get(j).setSpecialFunctionFinished(new Date());
                                  commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(0).getActivityId()).getSpecialActivities().get(i).getSpecialActivityResults().get(j).setSpecialFunctionFinished(new Date());
                                  updateTask(notify, commItemDB);
                                  uploadSpecialFunctionImages(sar.getResultString1(), String.valueOf(_currActivity.getMandantId()), String.valueOf(notify.getOrderNo()), String.valueOf(notify.getTaskId()), sa.getSpecialFunction());
                                }
                              }
                            }
                            activityItem.setSpecialActivities(_currActivity.getSpecialActivities());
                          }
                        }
                      }
                      
                    }
                    
                  } else {
                    //activityItem.setStatus(ActivityStatus.PENDING);
                  }
    /*
                  if (_currActivity.getStatus().ordinal() == 0) {
                    activityItem.setStatus(ActivityStatus.PENDING);
                  } else if (_currActivity.getStatus().ordinal() == 1) {
                    activityItem.setStatus(ActivityStatus.RUNNING);
                    //activityItem.setStarted(_currActivity.getStarted());
                    //activityItem.setFinished(_currActivity.getFinished());
                  } else if (_currActivity.getStatus().ordinal() == 2) {
                    activityItem.setStatus(ActivityStatus.FINISHED);
                    //activityItem.setStarted(_currActivity.getStarted());
                    //activityItem.setFinished(_currActivity.getFinished());
                  }
                  
     */
                  activityItem.setSequence(_currActivity.getSequence());
                  /*
                  if (_currActivity.getSpecialActivities() != null) {
                    int saSize = _currActivity.getSpecialActivities().size();
                    if (saSize > 0) {
                      for (int i = 0; i < saSize; i++) {
                        SpecialActivities sa = _currActivity.getSpecialActivities().get(i);
                        if (sa.getSpecialFunction().equals(SpecialFunction.STANDARD) || sa.getSpecialFunction().equals(SpecialFunction.SCAN_BARCODE))
                          continue;
  
                        if (sa.getSpecialActivityResults() != null) {
                          int resultOfSize = sa.getSpecialActivityResults().size();
                          if (resultOfSize > 0) {
                            for (int j = 0; j < resultOfSize; j++) {
  
                              SpecialActivityResult sar = sa.getSpecialActivityResults().get(j);
                              if (sar.getSpecialFunctionFinished() != null)
                                continue;
                              
                              //_currActivity.getSpecialActivities().get(i).getSpecialActivityResults().get(j).setSpecialFunctionFinished(new Date());
                              commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(0).getActivityId()).getSpecialActivities().get(i).getSpecialActivityResults().get(j).setSpecialFunctionFinished(new Date());
                              updateTask(notify, commItemDB);
                              uploadSpecialFunctionImages(sar.getResultString1(), String.valueOf(_currActivity.getMandantId()), String.valueOf(notify.getOrderNo()), String.valueOf(notify.getTaskId()), sa.getSpecialFunction());
                            }
                          }
                        }
                      }
                    }
                    activityItem.setSpecialActivities(_currActivity.getSpecialActivities());
                  }*/
                  //commItemReq.setActivityItem(activityItem);
                  commItemReq.setActivityItem(activityItem);
    
                  Call<ResultOfAction> call = App.getInstance().apiManager.getActivityApi().activityChange(commItemReq);
                  call.enqueue(new Callback<ResultOfAction>() {
                    @Override
                    public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {

                      allowRequest = true;
                      requestIsRunning = false;
                      requestCounter = 0;
                      
                      if (response.isSuccessful()) {
                        
                        int lastID = offlineConfirmations.get(0).getId();
  
                        // Verbindung OK -> Entferne den Task.
                        deleteTask(offlineConfirmations.get(0));
                        
                        if (response.body() == null) return;
                        
                        if (response.body().getIsSuccess()) {
                          // Activity vom API akzeptiert.
                          
                          addLog(LogLevel.INFO, LogType.APP_TO_SERVER, "RESPONSE FROM API", response.body().toString());
                          
                          // LastActivity setzen.
                          lastActivity(commItemDB.getTaskItem().getTaskId(), commItemDB.getTaskItem().getMandantId(), _currActivity, lastID);
                        } else {
                          
                          // Fehlerbehandlung... -> vom API nicht akzeptiert oder fehlerzustand.
                          if (!response.body().getIsException()) {
                            
                            // isSuccess = false, Exception = false, CommItem != null -> Task Updaten.
                            if (response.body().getCommItem() != null) {
                              updateTask(notify, response.body().getCommItem());
                              
                              // Entferne alle Einträge mit dieser ID aus dem Offline-Queue.
                              // deleteAllTaskByID(notify.getId());
                              
                              addLog(LogLevel.INFO, LogType.APP_TO_SERVER, "NOT SUCCESS", response.body().getText());
                            }
                          
                          } else {
                            // Exception anzeigen.
                            showErrorMessage(response.body().getText());
                            addLog(LogLevel.ASSERT, LogType.APP_TO_SERVER, "Exception", response.body().getText());
                          }
                        }
                      } else if (response.code() == 401) {
                        handleAccessToken();
                      } else if (response.code() == 400 || response.code() == 404 || response.code() == 406 || response.code() == 412 || response.code() == 409) {
                        
                        // ERROR HANDLING:
  
                        //Gson gson = new Gson();
                        Type type = new TypeToken<ResultOfAction>() {}.getType();
                        
                        //ResultOfAction resultOfAction = gson.fromJson(response.errorBody().charStream(),type);
                        ResultOfAction resultOfAction = App.getInstance().gsonUtc.fromJson(response.errorBody().charStream(),type);
                        if (resultOfAction != null && !resultOfAction.getIsException()) {
                          if (resultOfAction.getCommItem() != null) {
                            deleteTask(offlineConfirmations.get(0));
                            updateTask(notify, resultOfAction.getCommItem());
                            addLog(LogLevel.INFO, LogType.APP_TO_SERVER, String.valueOf(response.code()) + " : NOT SUCCESS - UPDATE", resultOfAction.getText());
                          }
                        } else {
                          showErrorMessage(response.body().getText());
                          addLog(LogLevel.ASSERT, LogType.APP_TO_SERVER, "Exception", resultOfAction.getText());
                        }
                      }
                    }
      
                    @Override
                    public void onFailure(Call<ResultOfAction> call, Throwable t) {
                      // SERVER NICHT ERREICHBAR.
                      allowRequest = true;
                      requestIsRunning = false;
                      requestCounter = 0;
                      Log.e(TAG, t.getMessage());
                      addLog(LogLevel.WARNING, LogType.APP_TO_SERVER, "onFailure", "Server nicht erreichbar - 10");
                      addLog(LogLevel.WARNING, LogType.APP_TO_SERVER, "onFailure", t.getMessage());
                    }
                  });
    
    
                } else {
                  // SET CONFIRMATION ITEM:
                  ConfirmationItem confirmationItem = new ConfirmationItem();
                  if (offlineConfirmations.get(0).getConfirmType() == ConfirmationType.TASK_CONFIRMED_BY_DEVICE.ordinal()) {
                    confirmationItem.setConfirmationType(ConfirmationType.TASK_CONFIRMED_BY_DEVICE);
                  } else if (offlineConfirmations.get(0).getConfirmType() == ConfirmationType.TASK_CONFIRMED_BY_USER.ordinal()) {
                    confirmationItem.setConfirmationType(ConfirmationType.TASK_CONFIRMED_BY_USER);
                  }
                  
                  confirmationItem.setTimeStampConfirmationUTC(new Date());
                  confirmationItem.setMandantId(commItemDB.getTaskItem().getMandantId());
                  confirmationItem.setTaskId(commItemDB.getTaskItem().getTaskId());
                  confirmationItem.setTaskChangeId(commItemDB.getTaskItem().getTaskChangeId());
                  commItemReq.setConfirmationItem(confirmationItem);
    
                  final Call<ResultOfAction> call = App.getInstance().apiManager.getConfirmApi().confirm(commItemReq);
                  call.enqueue(new Callback<ResultOfAction>() {
                    @Override
                    public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
                      
                      allowRequest = true;
                      requestIsRunning = false;
                      requestCounter = 0;
                      
                      if (response.isSuccessful() && response.body() != null) {
                        
                        addLog(LogLevel.INFO, LogType.APP_TO_SERVER, "RESPONSE FROM API", response.body().toString());
  
                        // Verbindung OK -> Entferne den Task.
                        deleteTask(offlineConfirmations.get(0));

                        if (response.body().getIsSuccess()) {
                          /*
                          // LastActivity setzen.
                          AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                              
                              mLastActivityDAO.getLastActivityByTaskClientId(commItemDB.getTaskItem().getTaskId(), commItemDB.getTaskItem().getMandantId())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new DisposableSingleObserver<LastActivity>() {
                                  @Override
                                  public void onSuccess(LastActivity lastActivity) {
                                    if (offlineConfirmations.get(0).getConfirmType() == ConfirmationType.TASK_CONFIRMED_BY_DEVICE.ordinal()) {
                                      postFcmTaskConfirmed(commItemDB, offlineConfirmations.get(0).getId());
                                      updateLastActivity(mLastActivityDAO, lastActivity, -1, "CONFIRMED BY DEVICE", 1);
                                      notify.setConfirmationStatus(1);
                                      AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                          mNotifyDAO.updateNotify(notify);
                                        }
                                      });
                                    } else if (offlineConfirmations.get(0).getConfirmType() == ConfirmationType.TASK_CONFIRMED_BY_USER.ordinal()) {
                                      postFcmTaskConfirmedByUser(commItemDB, offlineConfirmations.get(0).getId());
                                      updateLastActivity(mLastActivityDAO, lastActivity, -1, "CONFIRMED BY USER", 2);
                                      notify.setConfirmationStatus(2);
                                      AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                          mNotifyDAO.updateNotify(notify);
                                        }
                                      });
                                    }
                                  }
                    
                                  @Override
                                  public void onError(Throwable e) {
                                  }
                                });
                            }
                          });*/
                        } else {
            
                          // Fehlerbehandlung... -> vom API nicht akzeptiert oder Fehlerzustand.
                          if (!response.body().getIsException()) {
                          
                            // isSuccess = false, Exception = false, CommItem != null -> Task Updaten.
                            if (response.body().getCommItem() != null) {
                              updateTask(notify, response.body().getCommItem());
  
                              // Entferne alle Einträge mit dieser ID aus dem Offline-Queue.
                              //deleteAllTaskByID(notify.getId());
                              addLog(LogLevel.WARNING, LogType.APP_TO_SERVER, "NOT SUCCESS", response.body().getText());
                            }
                          } else {
  
                            // Exception anzeigen.
                            showErrorMessage(response.body().getText());
                            addLog(LogLevel.ASSERT, LogType.APP_TO_SERVER, "Exception", response.body().getText());
                          }
                        }
          
                      } else if (response.code() == 401) {
                        handleAccessToken();
                      }
                    }
      
                    @Override
                    public void onFailure(Call<ResultOfAction> call, Throwable t) {
                      allowRequest = true;
                      requestIsRunning = false;
                      requestCounter = 0;
                    }
                  });
                }
              }
  
              @Override
              public void onError(Throwable e) {
                allowRequest = true;
                requestIsRunning = false;
                requestCounter = 0;
  
                deleteDocumentConfirmation(offlineConfirmations);
              }
            });
          
        } else {
          Log.i(TAG, ">>>>>>> NO JOB DO WORK");
        }
      }
    });
  }


  private void postDocumentSent(Notify notify, int confirmId, String message, boolean confirmed) {
    String split[] = message.split(Constants.FILE_NAME_DIVIDER);

    if(split.length > 1){
      message = split[0]+ "..." + split[split.length-1];// time and last id that is unique
    }

    ChangeHistoryEvent changeHistoryEvent = new ChangeHistoryEvent(getApplicationContext().getString(R.string.log_title_documents), message,
            LogType.APP_TO_SERVER, ActionType.DOCUMENT_UPLOAD , confirmed? ChangeHistoryState.CONFIRMED : ChangeHistoryState.TO_BE_CONFIRMED_BY_APP,
            notify.getTaskId(), 0, notify.getOrderNo(), notify.getMandantId(), confirmId);
    EventBus.getDefault().post(changeHistoryEvent);
    Log.e(TAG, " posting doc event  : " + confirmId + " confirmed " + confirmed);
  }


  private void postActivityChangeSent(ActionType type, int taskId, int activityId, int orderNumber, int mandantID, int confirmationID) {
    ChangeHistoryEvent changeHistoryEvent = new ChangeHistoryEvent(getApplicationContext().getString(R.string.log_title_activity), getApplicationContext().getString(R.string.log_activity_status_change),
            LogType.APP_TO_SERVER, type, ChangeHistoryState.CONFIRMED,
            taskId, activityId, orderNumber, mandantID, confirmationID);
    EventBus.getDefault().post(changeHistoryEvent);
  }


  private void postFcmTaskConfirmedByUser(CommItem commItemDB, int offlineConfirmId) { //we don't have activityId on task change
    EventBus.getDefault().post(new ChangeHistoryEvent(getApplicationContext().getString(R.string.log_title_fcm), getApplicationContext().getString(R.string.log_confirm_send),
            LogType.FCM, ActionType.UPDATE_TASK, ChangeHistoryState.CONFIRMED,
            commItemDB.getTaskItem().getTaskId(), 0, commItemDB.getTaskItem().getOrderNo(), commItemDB.getTaskItem().getMandantId(), offlineConfirmId));
  }

  private void postFcmTaskConfirmed(CommItem commItemDB, int offlineConfirmId) { //we don't have activityId on task change
    EventBus.getDefault().post(new ChangeHistoryEvent(getApplicationContext().getString(R.string.log_title_fcm), getApplicationContext().getString(R.string.log_task_updated_fcm),
            LogType.FCM, ActionType.UPDATE_TASK, ChangeHistoryState.TO_BE_CONFIRMED_BY_DRIVER,
            commItemDB.getTaskItem().getTaskId(), 0, commItemDB.getTaskItem().getOrderNo(), commItemDB.getTaskItem().getMandantId(), offlineConfirmId));
  }

  private void handleUploadJob() {
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        List<OfflineConfirmation> offlineConfirmations = mOfflineConfirmationDAO.getAllOfflineConfirmations();
        if (offlineConfirmations.size() > 0 && offlineConfirmations.get(0).getUploadFlag() == 1) {
          
          Log.i(TAG, ">>>>>>>>>> Prepare Upload..: " + offlineConfirmations.get(0).getId());
          addLog(LogLevel.INFO, LogType.APP_TO_SERVER, "UPLOADING", "Task ID: " + String.valueOf(offlineConfirmations.get(0).getNotifyId()));
          
          mNotifyDAO.loadNotifyByTaskId(offlineConfirmations.get(0).getNotifyId())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new DisposableSingleObserver<Notify>() {
              @Override
              public void onSuccess(Notify notify) {
  
                deleteDocumentConfirmation(offlineConfirmations);
    
                if (notify == null || notify.getPhotoUrls() == null) {
                } else {
                
                  int photoSize = notify.getPhotoUrls().size();
                  if (photoSize > 0) {
                  
                    boolean uploadFiles = false;
                    for (int i = 0; i < photoSize; i++) {
                      UploadItem uploadItem = App.getInstance().gson.fromJson(notify.getPhotoUrls().get(i), UploadItem.class);
                      if (!uploadItem.getUploaded()) {
                        uploadFiles = true;
                      }
                    }
                    if (uploadFiles) {

                      // UPLOADING FILES....BEGIN
                      if(NetworkUtil.isConnected(getApplicationContext())) {
                        AsyncTask.execute(() -> uploadDocuments(notify, photoSize));
                      }

                      // UPLOADING FILES....END
                      EventBus.getDefault().post(new UploadAllDocsEvent(notify.getTaskId())); // even if it's no connection - document already waiting to upload in offline confirmations so removing photos from preview
                    
                    }
                  }
                }
              }
  
              @Override
              public void onError(Throwable e) {
                // Entfernen:
                deleteDocumentConfirmation(offlineConfirmations);
              }
            });
        }
      }
    });
  }

  private int incrementUploadCounter() {
    int oldId = TextSecurePreferences.getUploadConfirmationCounter();
    if(oldId == Integer.MAX_VALUE -2) oldId=0; //to prevent integer overflow
    oldId++;// always increase counter to count attempts
    TextSecurePreferences.setUploadConfirmationCounter(oldId);
    return oldId;
  }

  private void uploadDocuments(Notify notify, int photoSize) {
    for (int counter = 0; counter < notify.getPhotoUrls().size(); counter++) {

      UploadItem uploadItem = App.getInstance().gson.fromJson(notify.getPhotoUrls().get(counter), UploadItem.class);
      if (uploadItem.getUploaded()) continue;

      if (uploadItem.getUri() != null && !TextUtils.isEmpty(uploadItem.getUri()) && uploadItem.getUri().length() > 0) {

        File file = new File(uploadItem.getUri());

        RequestBody requestFile = RequestBody
          .create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body =
          MultipartBody.Part.createFormData("",
            file.getName(), requestFile);

        int oldId = incrementUploadCounter();

        RequestBody mandantId = RequestBody.create(MediaType
          .parse("multipart/form-data"), String.valueOf(notify.getMandantId()));
        RequestBody orderNo = RequestBody.create(MediaType
          .parse("multipart/form-data"), String.valueOf(notify.getOrderNo()));
        RequestBody taskId = RequestBody.create(MediaType
          .parse("multipart/form-data"), String.valueOf(notify.getTaskId()));

        RequestBody driverNo = RequestBody.create(MediaType
          .parse("multipart/form-data"), String.valueOf(-1));

        String dmsType = "0";
        if (uploadItem.getDocumentType().equals(DMSDocumentType.NA)) {
          dmsType = "0";
        } else if (uploadItem.getDocumentType().equals(DMSDocumentType.POD_CMR)) {
          dmsType = "24";
        } else if (uploadItem.getDocumentType().equals(DMSDocumentType.PALLETS_NOTE)) {
          dmsType = "26";
        } else if (uploadItem.getDocumentType().equals(DMSDocumentType.SAFETY_CERTIFICATE)) {
          dmsType = "27";
        } else if (uploadItem.getDocumentType().equals(DMSDocumentType.SHIPMENT_IMAGE)) {
          dmsType = "28";
        } else if (uploadItem.getDocumentType().equals(DMSDocumentType.DAMAGED_SHIPMENT_IMAGE)) {
          dmsType = "29";
        } else if (uploadItem.getDocumentType().equals(DMSDocumentType.DAMAGED_VEHICLE_IMAGE)) {
          dmsType = "30";
        }

        RequestBody documentType = RequestBody.create(MediaType
          .parse("multipart/form-data"), dmsType);


        Call<UploadResult> call = App.getInstance().apiManager.getFileUploadApi()
          .upload(mandantId,orderNo,taskId,driverNo,documentType, body);
        try {
          postDocumentSent(notify, oldId, file.getName(), false);//"if" to avoid multiple logs on request attempts

          Response<UploadResult> response = call.execute();//Synchronous request when uploading documents, since we have no id or md5 from server and i don't want to parse file name, that is also different //C:\\aBonaUploadData\\2020\\10\\01\\20201001095841_3_202040141_9867_353687144218095588.jpg
          if (response.isSuccessful()) {//document upload

            Log.d(TAG, ">>>>>>>>>>  Upload complete: " +response.body().getFileName() + " uri" +  uploadItem.getUri());

            uploadItem.setUploaded(true);
            notify.getPhotoUrls().set(counter, App.getInstance().gson.toJson(uploadItem));
            notify.setPhotoUrls(notify.getPhotoUrls());
            mNotifyDAO.updateNotify(notify);
            //mViewModel.update(mNotify);

            if (counter >= photoSize-1) {
              // Entfernen:
              mHandler.post(()->{
                EventBus.getDefault().post(new ProgressBarEvent(false));
                EventBus.getDefault().post(new DocumentEvent(notify.getMandantId(), notify.getOrderNo())); //used to reload document's but why? if we know that it is on server
              });
            }
            postDocumentSent(notify, oldId, file.getName(), true);
          } else {
            switch (response.code()) {
              case 401:
                handleAccessToken();
                break;
              default:
                
                break;
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
          Log.e(TAG, " error while uploading photo: " + e.getMessage());
        }
      }
    }
  }

  private void deleteDocumentConfirmation(List<OfflineConfirmation> offlineConfirmations) {
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        mOfflineConfirmationDAO.delete(offlineConfirmations.get(0));
      }
    });
  }

  private boolean isDeviceUpdateToken() {
    if (TextSecurePreferences.getFcmTokenUpdate(ContextUtils.getApplicationContext())) {
      Log.i(TAG, ">>>>>>> PREPARE DEVICE TOKEN UPDATE...");
      
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
          
          List<DeviceProfile> deviceProfiles = mDeviceProfileDAO.getDeviceProfiles();
          if (deviceProfiles.size() > 0) {
  
            CommItem commItem = new CommItem();
            Header header = new Header();
            header.setDataType(DataType.DEVICE_PROFILE);
            header.setDeviceId(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
            commItem.setHeader(header);
  
            DeviceProfileItem deviceProfileItem = new DeviceProfileItem();
            deviceProfileItem.setInstanceId(deviceProfiles.get(0).getInstanceId());
            deviceProfileItem.setDeviceId(deviceProfiles.get(0).getDeviceId());
            commItem.setDeviceProfileItem(deviceProfileItem);
  
            Call<ResultOfAction> call = App.getInstance().apiManager.getFCMApi().deviceProfile(commItem);
            call.enqueue(new Callback<ResultOfAction>() {
              @Override
              public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
                allowRequest = true;
                if (response.isSuccessful()) {
                  if (response.body() != null && response.body().getIsSuccess()) {
                    Log.d(TAG, ">>>>>>> FCM DEVICE TOKEN UPDATED!!!");
                    TextSecurePreferences.setFcmTokenUpdate(getApplicationContext(), false);
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
                Log.i(TAG, ">>>>>>> ERROR ON DEVICE UPDATE TOKEN!!!");
                allowRequest = true;
              }
            });
          }
        }
      });
      
      return false;
    }
    
    return true;
  }
  
  private void updateVersionForPatch() {
  
    patchRequestIsRunning = true;
    
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
      
        List<DeviceProfile> deviceProfiles = mDeviceProfileDAO.getDeviceProfiles();
        if (deviceProfiles.size() > 0) {
        
          CommItem commItem = new CommItem();
          Header header = new Header();
          header.setDataType(DataType.DEVICE_PROFILE);
          header.setDeviceId(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
          commItem.setHeader(header);
        
          DeviceProfileItem deviceProfileItem = new DeviceProfileItem();
          //deviceProfileItem.setInstanceId(deviceProfiles.get(0).getInstanceId());
          deviceProfileItem.setDeviceId(deviceProfiles.get(0).getDeviceId());
          deviceProfileItem.setModel(Build.MODEL);
          deviceProfileItem.setManufacturer(Build.MANUFACTURER);
          try {
            PackageInfo pInfo = ContextUtils.getApplicationContext()
              .getPackageManager().getPackageInfo(ContextUtils.getApplicationContext().getPackageName(), 0);
            deviceProfileItem.setVersionCode(pInfo.versionCode);
            deviceProfileItem.setVersionName(pInfo.versionName);
          } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
          }
          //deviceProfileItem.setLanguageCode(Locale.getDefault().toString());
          commItem.setDeviceProfileItem(deviceProfileItem);
        
          Call<ResultOfAction> call = App.getInstance().apiManager.getFCMApi().deviceProfile(commItem);
          call.enqueue(new Callback<ResultOfAction>() {
            @Override
            public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
              patchRequestIsRunning = false;
              if (response.isSuccessful()) {
                if (response.body().getIsSuccess() && !response.body().getIsException()) {
                  TextSecurePreferences.setPatch00_State(_PATCH_00_STATE_GET_ALL_TASK);
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
              patchRequestIsRunning = false;
            }
          });
        }
      }
    });
  }
  
  private boolean isUpdateDevice() {
    
    if (TextSecurePreferences.getDeviceUpdate()) {
      Log.i(TAG, ">>>>>>>>>> UPDATE DEVICE...");
      
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
    
          List<DeviceProfile> deviceProfiles = mDeviceProfileDAO.getDeviceProfiles();
          if (deviceProfiles.size() > 0) {
  
            CommItem commItem = new CommItem();
            Header header = new Header();
            header.setDataType(DataType.DEVICE_PROFILE);
            header.setDeviceId(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
            commItem.setHeader(header);
  
            DeviceProfileItem deviceProfileItem = new DeviceProfileItem();
            //deviceProfileItem.setInstanceId(deviceProfiles.get(0).getInstanceId());
            deviceProfileItem.setDeviceId(deviceProfiles.get(0).getDeviceId());
            deviceProfileItem.setModel(Build.MODEL);
            deviceProfileItem.setManufacturer("BWS - VERSION CHANGE");
            try {
              PackageInfo pInfo = ContextUtils.getApplicationContext()
                .getPackageManager().getPackageInfo(ContextUtils.getApplicationContext().getPackageName(), 0);
              deviceProfileItem.setVersionCode(pInfo.versionCode);
              deviceProfileItem.setVersionName(pInfo.versionName);
            } catch (PackageManager.NameNotFoundException e) {
              e.printStackTrace();
            }
            //deviceProfileItem.setLanguageCode(Locale.getDefault().toString());
            commItem.setDeviceProfileItem(deviceProfileItem);
            
            Call<ResultOfAction> call = App.getInstance().apiManager.getFCMApi().deviceProfile(commItem);
            call.enqueue(new Callback<ResultOfAction>() {
              @Override
              public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
                allowRequest = true;
                if (response.isSuccessful()) {
                  if (response.body().getIsSuccess() && !response.body().getIsException()) {
                    TextSecurePreferences.setDeviceUpdate(false);
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
                Log.d(TAG, ">>>>>>> ERROR ON DEVICE UPDATE!!!");
                allowRequest = true;
              }
            });
          }
        }
      });
      
      return false;
    }
    
    return true;
  }
  
  private void updateGetAllTasks() {
    
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        Call<ResultOfAction> call = App.getInstance().apiManager.getTaskApi().getAllTasks(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
        call.enqueue(new Callback<ResultOfAction>() {
          @Override
          public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
            if (response.isSuccessful() && response.body() != null && response.body().getIsSuccess()) {
              if(response.body().getAllTask().size() > 0)
              //EventBus.getDefault().post(new LogEvent(getString(R.string.log_tasks_come), LogType.SERVER_TO_APP, LogLevel.INFO, getString(R.string.log_title_get_tasks_bg), 0));
              handleGetAllTasks(response.body());
            } else {
              
              switch (response.code()) {
                case 401: {
                  handleAccessToken();
                  //EventBus.getDefault().post(new LogEvent(getString(R.string.log_token_error), LogType.SERVER_TO_APP, LogLevel.INFO, getString(R.string.log_title_get_tasks_bg), 0));
                } break;
              }
            }
          }
  
          @Override
          public void onFailure(Call<ResultOfAction> call, Throwable t) {
            //EventBus.getDefault().post(new LogEvent(getString(R.string.log_tasks_error), LogType.SERVER_TO_APP, LogLevel.INFO, getString(R.string.log_title_get_tasks_bg), 0));
          }
        });
      }
    });
  }
  
  private void updateLangCode() {

    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
    
        List<DeviceProfile> deviceProfiles = mDeviceProfileDAO.getDeviceProfiles();
        
        CommItem commItem = new CommItem();
        Header header = new Header();
        header.setDataType(DataType.DEVICE_PROFILE);
        header.setTimestampSenderUTC(new Date());
        header.setDeviceId(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
        commItem.setHeader(header);
        
        DeviceProfileItem deviceProfileItem = new DeviceProfileItem();
        //deviceProfileItem.setInstanceId(deviceProfiles.get(0).getInstanceId());
        deviceProfileItem.setDeviceId(deviceProfiles.get(0).getDeviceId());
        //deviceProfileItem.setModel(deviceProfiles.get(0).getDeviceModel());
        deviceProfileItem.setManufacturer("BWS - Language Change()");
        //deviceProfileItem.setUpdatedDate(DateConverter.fromTimestamp(deviceProfiles.get(0).getModifiedAt()));
        deviceProfileItem.setLanguageCode(TextSecurePreferences.getLanguage(ContextUtils.getApplicationContext()));
        commItem.setDeviceProfileItem(deviceProfileItem);
        
        Call<ResultOfAction> call = App.getInstance().apiManager.getFCMApi().deviceProfile(commItem);
        call.enqueue(new Callback<ResultOfAction>() {
          @Override
          public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
            if (response.isSuccessful()) {
              if (response.body() != null && response.body().getIsSuccess() && !response.body().getIsException()) {
                TextSecurePreferences.setUpdateLangCode(false);
              }
            } else {
              
              // error case:
              switch (response.code()) {
                case 401: handleAccessToken(); break;
              }
            }
          }
  
          @Override
          public void onFailure(Call<ResultOfAction> call, Throwable t) {
    
          }
        });
      }
    });
  }
  
  private static final int _PATCH_00_STATE_DELETE_ALL_TABLES = 0;
  private static final int _PATCH_00_STATE_GENERATE_RANDOM_NUMBER = 1;
  private static final int _PATCH_00_STATE_WAITING = 2;
  private static final int _PATCH_00_STATE_CHECK_ENDPOINT = 4;
  private static final int _PATCH_00_STATE_SEND_NEW_VERSION = 8;
  private static final int _PATCH_00_STATE_GET_ALL_TASK = 16;
  private static final int _PATCH_00_STATE_COMPLETED = 32;
  private static boolean patchRequestIsRunning = false;
  
  private boolean isPatch00_Completed() {
    if (TextSecurePreferences.isPatch00_Completed()) {
      return true;
    } else {
      
      String title = "Erledigt";
      
      int state = TextSecurePreferences.getPatch00_State();
      switch (state) {
        case _PATCH_00_STATE_DELETE_ALL_TABLES:
          Log.i(TAG, "1. DELETE TABLES");
          title = "Datenbank leeren...";
          deleteTables();
          TextSecurePreferences.setPatch00_State(_PATCH_00_STATE_GENERATE_RANDOM_NUMBER);
          break;
          
        case _PATCH_00_STATE_GENERATE_RANDOM_NUMBER:
          Log.i(TAG, "2. RANDOM_NUMBER");
          title = "Zufallszahl generiert...";
          
          int random = ThreadLocalRandom.current().nextInt(1, 7200);
          Log.i(TAG, "RANDOM INT: " + random);
          
          if (random >= 1 && random <= 7200) {
            TextSecurePreferences.setPatch00_RandomNumber(random);
            TextSecurePreferences.setPatch00_State(_PATCH_00_STATE_WAITING);
          }
          break;
          
        case _PATCH_00_STATE_WAITING:
          Log.i(TAG, "3. WAITING");
          title = "Warten...";
          if (TextSecurePreferences.getPatch00_RandomNumber() <= 0) {
            TextSecurePreferences.setPatch00_State(_PATCH_00_STATE_CHECK_ENDPOINT);
          }
          break;
          
        case _PATCH_00_STATE_CHECK_ENDPOINT:
          title = "Hole neuen Endpoint...";
          Log.i(TAG, "4. ENDPOINT");
          if (!patchRequestIsRunning) {
            fetchEndpoint("10783");
          }
          break;
          
        case _PATCH_00_STATE_SEND_NEW_VERSION:
          Log.i(TAG, "5. NEW VERSION");
          title = "Sende App Version...";
          if (!patchRequestIsRunning) {
            updateVersionForPatch();
          }
          break;
          
        case _PATCH_00_STATE_GET_ALL_TASK:
          Log.i(TAG, "5. GET ALL TASK");
          title = "Get All Task...";
          App.eventBus.post(new GetAllTaskEvent());
          TextSecurePreferences.setPatch00_State(_PATCH_00_STATE_COMPLETED);
          TextSecurePreferences.setPatch00_Completed(true);
          break;
          
        case _PATCH_00_STATE_COMPLETED:
          break;
      }
  
      int randomNumber = TextSecurePreferences.getPatch00_RandomNumber();
      randomNumber = randomNumber - 10;
      TextSecurePreferences.setPatch00_RandomNumber(randomNumber);
      
      if (randomNumber <= 0) {
        App.eventBus.post(new PatchEvent(randomNumber, title, false));
      } else {
        App.eventBus.post(new PatchEvent(randomNumber, title, true));
      }
      
      return false;
    }
  }
  
  private boolean isDeviceRegistrated() {
    if (TextSecurePreferences.isDeviceRegistrated()) {
      return true;
    } else {
      
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
          
          if (registrationRequest) {
            Log.i(TAG, ">>>>>>>>>> WAITING OF RESPONSE...");
            return;
          }
          List<DeviceProfile> deviceProfiles = mDeviceProfileDAO.getDeviceProfiles();
  
          if (deviceProfiles.size() > 0) {
            Log.i(TAG, ">>>>>>> PREPARE DEVICE REGISTRATION");
            
            App.eventBus.post(new RegistrationEvent(RegistrationEvent.State.STARTED));
  
            CommItem commItem = new CommItem();
            Header header = new Header();
            header.setDataType(DataType.DEVICE_PROFILE);
            header.setDeviceId(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
            commItem.setHeader(header);
  
            DeviceProfileItem deviceProfileItem = new DeviceProfileItem();
            deviceProfileItem.setInstanceId(deviceProfiles.get(0).getInstanceId());
            deviceProfileItem.setDeviceId(deviceProfiles.get(0).getDeviceId());
            deviceProfileItem.setModel(deviceProfiles.get(0).getDeviceModel());
            deviceProfileItem.setManufacturer("BWS - Device Registrated()");
            deviceProfileItem.setCreatedDate(DateConverter.fromTimestamp(deviceProfiles.get(0).getCreatedAt()));
            deviceProfileItem.setUpdatedDate(DateConverter.fromTimestamp(deviceProfiles.get(0).getModifiedAt()));
            deviceProfileItem.setLanguageCode(deviceProfiles.get(0).getLanguageCode().replace("_", "-"));
            deviceProfileItem.setVersionCode(deviceProfiles.get(0).getVersionCode());
            deviceProfileItem.setVersionName(deviceProfiles.get(0).getVersionName());
            commItem.setDeviceProfileItem(deviceProfileItem);
            
            registrationRequest = true;
            Log.i(TAG, ">>>>>>>>>> REGISTRATION STARTED...");
            
            Call<ResultOfAction> call = App.getInstance().apiManager.getFCMApi().deviceProfile(commItem);
            call.enqueue(new Callback<ResultOfAction>() {
              @Override
              public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
                allowRequest = true;
                registrationRequest = false;
                if (response.isSuccessful()) {
                  if (response.body() != null && response.body().getIsSuccess() && !response.body().getIsException()) {
                    Log.d(TAG, ">>>>>>> DEVICE REGISTRATION WAS SUCCESSFULLY!!!");
                    TextSecurePreferences.setDeviceRegistrated(true);
                    TextSecurePreferences.setRegistrationStarted(false);
                    
                    App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_DEVICE_REGISTRATED), null));
                    App.eventBus.post(new RegistrationEvent(RegistrationEvent.State.FINISHED));
                  } else {
                    App.eventBus.post(new RegistrationEvent(RegistrationEvent.State.ERROR));
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
                Log.d(TAG, ">>>>>>> ERROR ON DEVICE REGISTRATION!!! - " + t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                allowRequest = true;
                registrationRequest = false;
              }
            });
          } else {
            Log.e(TAG, ">>>>>>> NO DEVICE FOR REGISTRATION - MISSING !!!");
          }
        }
      });
      
      return false;
    }
  }
  
  private boolean isDevicePermissionGranted() {
    return TextSecurePreferences.isDevicePermissionsGranted();
  }
  
  @Override
  public void onTaskRemoved(Intent rootIntent) {
    Log.i(TAG, "onTaskRemoved() called!");
    Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
    restartServiceIntent.setPackage(getPackageName());
    startService(restartServiceIntent);
    super.onTaskRemoved(rootIntent);
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
    Log.i(TAG, "onCreate() called!");
    
    mHandler = new Handler();
    mDelayReasonHandler = new Handler();
    mRunner = new Runner();
    mDelayReasonRunner = new DelayReasonRunner();
  }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if(intent.getBooleanExtra(Constants.KEY_KILL_BACKGROUND_SERVICE, false) == true){
      Log.e(TAG, "service stop");
      stopService();
    }
    Notification notification = prepareNotification(getApplicationContext().getResources().getString(R.string.alarm_check_title),
            getApplicationContext().getResources().getString(R.string.running_text)).build();
    startForeground(Constants.ALARM_CHECK_JOB_ID, notification);

    mHandler.post(mRunner);
    mDelayReasonHandler.post(mDelayReasonRunner);
    return START_REDELIVER_INTENT;
  }

  private void stopService(){
    stopForeground(true);
    stopSelf();
  }

  private NotificationCompat.Builder prepareNotification(String title, String message) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), Constants.NOTIFICATION_CHANNEL_ID)
//            .setContentIntent(getPendingIntent())
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis());

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //need to specify channel  on that api
      NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.PACKAGE_NAME,
              NotificationManager.IMPORTANCE_HIGH);
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
      builder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID);
    }
    return builder;
  }

  @Override
  public void onDestroy() {
    Log.i(TAG, "onDestroy() called!");
    super.onDestroy();
    mHandler.removeCallbacks(mRunner);
    mDelayReasonHandler.removeCallbacks(mDelayReasonRunner);
    
    Intent broadcastIntent =
      new Intent("com.abona_erp.driver.app.RestartBackgroundServiceWorker");
    sendBroadcast(broadcastIntent);
  }
  
  private void deleteTask(OfflineConfirmation offlineConfirmation) {
    if (offlineConfirmation != null) {
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
          
          mOfflineConfirmationDAO.delete(offlineConfirmation);
          Log.i("TABLE", "--------------------------------------- Lösche Eintrag " + offlineConfirmation.getId());
        }
      });
    }
  }
  
  private void deleteAllTaskByID(int id) {
    if (id > 0) {
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
  
          List<OfflineConfirmation> offlineConfirmations = mOfflineConfirmationDAO.getAllOfflineConfirmations();
          if (offlineConfirmations.size() > 0) {
            for (int i = 0; i < offlineConfirmations.size(); i++) {
              if (offlineConfirmations.get(i).getNotifyId() == id) {
                mOfflineConfirmationDAO.delete(offlineConfirmations.get(i));
              }
            }
          }
        }
      });
    }
  }
  
  private void lastActivity(int taskID, int mandantID, ActivityItem activityItem, int lastID) {
    
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
    
        mLastActivityDAO.getLastActivityByTaskClientId(taskID, mandantID)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeOn(Schedulers.io())
          .subscribe(new DisposableSingleObserver<LastActivity>() {
            
            @Override
            public void onSuccess(LastActivity lastActivity) {
              postActivityChangeSent((activityItem.getStatus() == ActivityStatus.FINISHED) ? ActionType.FINISH_ACTIVITY : ActionType.START_ACTIVITY,
                activityItem.getTaskId(), activityItem.getActivityId(), UtilCommon.parseInt(lastActivity.getOrderNo()), activityItem.getMandantId(), lastID);
              updateLastActivity(mLastActivityDAO, lastActivity, 2, "CHANGED", -1);
            }
            
            @Override
            public void onError(Throwable e) {
            }
          });
      }
    });
  }
  
  private void updateTask(Notify notify, CommItem commItem) {
    
    if (commItem == null) return;
    if (notify == null) return;
  
    notify.setData(App.getInstance().gsonUtc.toJson(commItem));
    //notify.setRead(false);
    if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
      notify.setStatus(0);
    } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
      notify.setStatus(50);
    } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
      notify.setStatus(90);
    } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
      notify.setStatus(100);
    } else if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.BREAK)) {
      notify.setStatus(51);
    }
    notify.setTaskDueFinish(commItem.getTaskItem().getTaskDueDateFinish());
  
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        mNotifyDAO.updateNotify(notify);
      }
    });
  }
  
  @Override
  public void onLowMemory() {
    Log.i(TAG, "onLowMemory() called!");
    Log.i(TAG, "Send Event to GetAllTask");
    //App.eventBus.post(new GetAllTaskEvent());
    super.onLowMemory();
  }
  
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
  
  private void updateLastActivity(LastActivityDAO dao, LastActivity lastActivity, int statusType, String description, int confirmationStatus) {

    lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
    
    ArrayList<String> _list = lastActivity.getDetailList();
    LastActivityDetails _detail = new LastActivityDetails();
    if (description != null && !TextUtils.isEmpty(description)) {
      _detail.setDescription(description);
    }
    _list.add(App.getInstance().gson.toJson(_detail));
    lastActivity.setDetailList(_list);
    
    if (confirmationStatus != -1) {
      lastActivity.setConfirmStatus(confirmationStatus);
    }
    if (statusType != -1) {
      lastActivity.setStatusType(statusType);
    }
    
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        if (dao != null) {
          dao.update(lastActivity);
        }
      }
    });
  }
  
  private void handleGetAllTasks(ResultOfAction resultOfAction) {
    
    if (resultOfAction == null) return;
    
    try {
      if (resultOfAction.getIsSuccess() && !resultOfAction.getIsException()) {
        if (resultOfAction.getAllTask() != null && resultOfAction.getAllTask().size() > 0) {
          for (int i = 0; i < resultOfAction.getAllTask().size(); i++) {
  
            TaskItem taskItem = resultOfAction.getAllTask().get(i);
            if (taskItem.getMandantId() == null) continue;
            int mandantId = taskItem.getMandantId();
            TextSecurePreferences.setMandantID(mandantId);
  
            if (taskItem.getTaskId() == null) continue;
            int taskId = taskItem.getTaskId();
            
            mNotifyDAO.loadNotifyByTaskMandantId(mandantId, taskId)
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
                    
                    mNotifyDAO.updateNotify(notify);
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
  
                  mNotifyDAO.updateNotify(notify);
                }
              });
          }
          TextSecurePreferences.setUpdateAllTasks(false);
        }
      }
    } catch (Exception e) {
      Log.d(TAG, e.getMessage());
    }
  }
  
  private void showErrorMessage(String message) {
    App.eventBus.post(new RestApiErrorEvent(message));
  }
  
  private void handleAccessToken() {
    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    RequestBody body = RequestBody.create(mediaType, "grant_type=password&username=manyvehicles%40abona-erp.com&password=1234qwerQWER%2C.-");
    
    Request request = new Request.Builder()
      .url(TextSecurePreferences.getEndpoint() + "authentication")
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
        
        getClient().newCall(request).enqueue(new okhttp3.Callback() {
          @Override
          public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
            //allowRequest = true;
          }
          
          @Override
          public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
            //allowRequest = true;
            if (response.isSuccessful()) {
              try {
                String jsonData = response.body().string().toString();
                JSONObject jobject = new JSONObject(jsonData);
                //Log.i(TAG, "ACCESS_TOKEN " + jobject.getString("access_token"));
                String access_token = jobject.getString("access_token");
                if (!TextUtils.isEmpty(access_token)) {
                  TextSecurePreferences.setAccessToken(getApplicationContext(), access_token);
                }
  
                addLog(LogLevel.INFO, LogType.APP_TO_SERVER, "ACCESS TOKEN", "GET NEW TOKEN");
              } catch (NullPointerException e) {
                e.printStackTrace();
              } catch (JSONException e) {
                e.printStackTrace();
              }
            }
          }
        });
      }
    });
  }
  
  OkHttpClient mClient = null;
  private OkHttpClient getClient() {
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    
    if (mClient == null) {
      synchronized (BackgroundServiceWorker.class) {
        if (mClient == null) {
          mClient = UnsafeOkHttpClient.getUnsafeOkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .hostnameVerifier(new HostnameVerifier() {
              @Override
              public boolean verify(String s, SSLSession sslSession) {
                return true;
              }
            })
            .addInterceptor(logging)
            .build();
        }
      }
    }
    return mClient;
  }
  
  private void deleteTables() {
    
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        mOfflineConfirmationDAO.deleteAll();
        mNotifyDAO.deleteAll();
        mOfflineDelayReasonDAO.deleteAll();
      }
    });
  }
  
  private void fetchEndpoint(String clientID) {
    
    patchRequestIsRunning = true;
    
    String url = "http://endpoint.abona-erp.com/Api/AbonaClients/GetServerURLByClientId/" + clientID + "/2";
    
    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
      
      @Override
      public void onResponse(JSONObject response) {
        patchRequestIsRunning = false;
        
        try {
          boolean active = response.getBoolean("IsActive");
          if (active) {
            String webService = response.getString("WebService");
            if (TextUtils.isEmpty(webService) || webService.equals("null")) {
              return;
            }
            TextSecurePreferences.setEndpoint(webService);
            TextSecurePreferences.setClientID(clientID);
  
            TextSecurePreferences.setLoginPageEnable(false);
            
            TextSecurePreferences.setPatch00_State(_PATCH_00_STATE_SEND_NEW_VERSION);
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }, new com.android.volley.Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        patchRequestIsRunning = false;
      }
    });
    
    // Add JsonObjectRequest to the RequestQueue:
    requestQueue.add(jsonObjectRequest);
  }
  
  private boolean uploadSpecialFunctionImages(
    String filePath,
    String mandantId,
    String orderNo,
    String taskId,
    SpecialFunction specialFunction
  ) {
    
    boolean success = false;
    
    File file = new File(filePath);
    
    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
    MultipartBody.Part body = MultipartBody.Part.createFormData("", file.getName(), requestBody);
  
    RequestBody _mandantId = RequestBody.create(MediaType.parse("multipart/form-data"), mandantId);
    RequestBody _orderNo = RequestBody.create(MediaType.parse("multipart/form-data"), orderNo);
    RequestBody _taskId = RequestBody.create(MediaType.parse("multipart/form-data"), taskId);
    RequestBody _driverNo = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(-1));
  
    String dmsType = "0";
    if (specialFunction.equals(SpecialFunction.TAKE_IMAGES_CMR)) {
      dmsType = "24";
    } else if (specialFunction.equals(SpecialFunction.TAKE_IMAGES_SHIPMENT)) {
      dmsType = "28";
    }
    RequestBody _documentType = RequestBody.create(MediaType
      .parse("multipart/form-data"), dmsType);
    
    Call<UploadResult> call = App.getInstance().apiManager.getFileUploadApi()
      .upload(_mandantId, _orderNo, _taskId, _driverNo, _documentType, body);
    call.enqueue(new Callback<UploadResult>() {
      @Override
      public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {
      
        //allowRequest = true;
        //requestIsRunning = false;
        //requestCounter = 0;
      
        if (response.isSuccessful()) {
        } else if (response.code() == 401) {
          handleAccessToken();
        }
      }
    
      @Override
      public void onFailure(Call<UploadResult> call, Throwable t) {
        //allowRequest = true;
        //requestIsRunning = false;
      }
    });
    /*
    try {
      Response<UploadResult> response = call.execute();
      if (response.isSuccessful()) {
        success = true;
      } else {
        switch (response.code()) {
          case 401: handleAccessToken(); break;
          default:
            break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    
     */
    
    return success;
  }
  
  // ------------------------------------------------------------------------
  // Logging
  private void addLog(LogLevel level, LogType type, String title, String message) {
    LogItem item = new LogItem();
    item.setLevel(level);
    item.setType(type);
    item.setTitle(title);
    item.setMessage(message);
    item.setCreatedAt(new Date());
    if (mLogDao != null) {
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
          mLogDao.insert(item);
        }
      });
    }
  }
  
  
  private boolean pingServer(String ipAddress) {
    Runtime runtime = Runtime.getRuntime();
    try {
      Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " + ipAddress);
      int mExitValue = mIpAddrProcess.waitFor();
      if (mExitValue == 0) {
        return true;
      } else {
        return false;
      }
    } catch (InterruptedException ignore) {
      ignore.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }
}
