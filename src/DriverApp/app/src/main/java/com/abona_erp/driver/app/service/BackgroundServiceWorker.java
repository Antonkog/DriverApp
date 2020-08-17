package com.abona_erp.driver.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

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
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.dao.OfflineDelayReasonDAO;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.entity.OfflineDelayReasonEntity;
import com.abona_erp.driver.app.data.model.ActivityItem;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ConfirmationItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.DataType;
import com.abona_erp.driver.app.data.model.DelayReasonItem;
import com.abona_erp.driver.app.data.model.DelaySource;
import com.abona_erp.driver.app.data.model.DeviceProfileItem;
import com.abona_erp.driver.app.data.model.Header;
import com.abona_erp.driver.app.data.model.LastActivityDetails;
import com.abona_erp.driver.app.data.model.ResultOfAction;
import com.abona_erp.driver.app.data.model.TaskItem;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.data.remote.client.UnsafeOkHttpClient;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.LogEvent;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.event.RegistrationErrorEvent;
import com.abona_erp.driver.app.ui.event.RegistrationFinishedEvent;
import com.abona_erp.driver.app.ui.event.RegistrationStartEvent;
import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DelayReasonUtil;
import com.abona_erp.driver.app.util.DeviceUtils;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.core.base.ContextUtils;
import com.abona_erp.driver.core.util.MiscUtil;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

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

public class BackgroundServiceWorker extends Service {
  
  private static final String TAG = MiscUtil.getTag(BackgroundServiceWorker.class);
  
  private Context mContext;
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
  
  public BackgroundServiceWorker() {
  }
  
  public BackgroundServiceWorker(Context appContext) {
    super();
    this.mContext = appContext;
  }
  
  public class DelayReasonRunner implements Runnable {
    @Override
    public void run() {
    
    }
  }
  
  private volatile static int delay = 3000;
  public volatile static boolean allowRequest = true;
  public class Runner implements Runnable {
    @Override
    public void run() {
      Log.i(TAG, ">>>>>>> BACKGROUND SERVICE LISTENING... >>>>>>>");
  
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
          
          if (!isDevicePermissionGranted()) {
            Log.i(TAG, "******* DEVICE PERMISSION IS NOT GRANTED!!! *******");
            allowRequest = true;
            mHandler.postDelayed(this, delay);
            return;
          }
  
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
          
          if (TextSecurePreferences.isUpdateLangCode()) {
            updateLangCode();
            mHandler.postDelayed(this, delay);
            return;
          } else if (TextSecurePreferences.isUpdateAllTasks()) {
            EventBus.getDefault().post(new LogEvent(getString(R.string.log_update_schedule), LogType.SERVER_TO_APP, LogLevel.INFO, getString(R.string.log_title_get_tasks_bg), 0));
            updateGetAllTasks();
            mHandler.postDelayed(this, delay);
            return;
          } else if (TextSecurePreferences.isUpdateDelayReason()) {
            DelayReasonUtil.getDelayReasonsFromService(TextSecurePreferences.getMandantID());
            mHandler.postDelayed(this, delay);
            return;
          }
          
          AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
              List<OfflineConfirmation> offlineConfirmations =
                mOfflineConfirmationDAO.getAllOfflineConfirmations();
  
              List<OfflineDelayReasonEntity> offlineDelayReasonEntities =
                mOfflineDelayReasonDAO.getAllOfflineDelayReasons();
              
              if (!allowRequest && (offlineConfirmations.size() > 0 || offlineDelayReasonEntities.size() > 0)) {
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
  }
  
  private void handleDelayReasonJob() {
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
    
        List<OfflineDelayReasonEntity> offlineDelayReasonEntities = mOfflineDelayReasonDAO.getAllOfflineDelayReasons();
        if (offlineDelayReasonEntities.size() > 0) {
        
          CommItem reqItem = new CommItem();
          Header header = new Header();
          header.setTimestampSenderUTC(new Date());
          header.setDataType(DataType.DELAY_REASONS);
          header.setDeviceId(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
          reqItem.setHeader(header);
  
          DelayReasonItem delayReasonItem = new DelayReasonItem();
          delayReasonItem.setWaitingReasongId(offlineDelayReasonEntities.get(0).getWaitingReasonId());
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
          reqItem.setDelayReasonItems(items);
          
          Call<ResultOfAction> call = App.getInstance().apiManager
            .getDelayReasonApi().setDelayReasons(reqItem);
          call.enqueue(new Callback<ResultOfAction>() {
            @Override
            public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
    
              if (response.body() == null) return;
              if (response.body().getIsSuccess() && !response.body().getIsException()) {
                
                ResultOfAction resultOfAction = response.body();
                if (resultOfAction == null) return;
                if (resultOfAction.getCommItem() == null) return;
  
                mNotifyDAO.loadNotifyById(offlineDelayReasonEntities.get(0).getNotifyId())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.io())
                  .subscribe(new DisposableSingleObserver<Notify>() {
                    @Override
                    public void onSuccess(Notify notify) {
    
                      notify.setData(App.getInstance().gson.toJson(resultOfAction.getCommItem()));
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
    
                    }
                  });
              }
            }
  
            @Override
            public void onFailure(Call<ResultOfAction> call, Throwable t) {
    
            }
          });
        }
      }
    });
  }
  
  private void handleConfirmationJob() {
  
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
  
        List<OfflineConfirmation> offlineConfirmations = mOfflineConfirmationDAO.getAllOfflineConfirmations();
        if (offlineConfirmations.size() > 0) {
          Log.i(TAG, ">>>>>>> NOCH ZU BEARBEITEN......: " + offlineConfirmations.size() + " JOBS");
          Log.i(TAG, ">>>>>>> ID......................: " + offlineConfirmations.get(0).getId());
  
          mNotifyDAO.loadNotifyById(offlineConfirmations.get(0).getNotifyId())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new DisposableSingleObserver<Notify>() {
              @Override
              public void onSuccess(Notify notify) {
                Log.i(TAG, ">>>>>>> NOTIFY OID......: " + notify.getId());
  
                CommItem commItemDB = App.getInstance().gson.fromJson(notify.getData(), CommItem.class);
                CommItem commItemReq = new CommItem();
  
                // SET HEADER:
                Header header = new Header();
                if (offlineConfirmations.get(0).getConfirmType() == ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal()) {
                  header.setDataType(DataType.ACTIVITY);
                } else {
                  header.setDataType(DataType.CONFIRMATION);
                }
                header.setDeviceId(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
                header.setTimestampSenderUTC(/*commItemDB.getHeader().getTimestampSenderUTC()*/new Date());
                commItemReq.setHeader(header);
  
                if (offlineConfirmations.get(0).getConfirmType() == ConfirmationType.ACTIVITY_CONFIRMED_BY_USER.ordinal()) {
                  
                  ActivityItem _currActivity = commItemDB.getTaskItem().getActivities().get(offlineConfirmations.get(0).getActivityId());
                  
                  // SET ACTIVITY ITEM CHANGE:
                  ActivityItem activityItem = new ActivityItem();
                  activityItem.setDeviceId(DeviceUtils.getUniqueIMEI(getApplicationContext()));
                  activityItem.setTaskId(_currActivity.getTaskId());
                  activityItem.setMandantId(_currActivity.getMandantId());
                  activityItem.setActivityId(_currActivity.getActivityId());
                  activityItem.setName(_currActivity.getName());
                  activityItem.setDescription(_currActivity.getDescription());
    
                  if (_currActivity.getStatus().ordinal() == 0) {
                    activityItem.setStatus(ActivityStatus.PENDING);
                  } else if (_currActivity.getStatus().ordinal() == 1) {
                    activityItem.setStatus(ActivityStatus.RUNNING);
                    activityItem.setStarted(_currActivity.getStarted());
                    activityItem.setFinished(_currActivity.getFinished());
                  } else if (_currActivity.getStatus().ordinal() == 2) {
                    activityItem.setStatus(ActivityStatus.FINISHED);
                    activityItem.setStarted(_currActivity.getStarted());
                    activityItem.setFinished(_currActivity.getFinished());
                  }
                  activityItem.setSequence(_currActivity.getSequence());
                  commItemReq.setActivityItem(activityItem);
    
                  Call<ResultOfAction> call = App.getInstance().apiManager.getActivityApi().activityChange(commItemReq);
                  call.enqueue(new Callback<ResultOfAction>() {
                    @Override
                    public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {

                      EventBus.getDefault().post(new LogEvent(getBaseContext().getString(R.string.log_activity_status_change),
                              LogType.SERVER_TO_APP, LogLevel.INFO, getBaseContext().getString(R.string.log_title_activity),
                              activityItem.getTaskId()));

                      allowRequest = true;
                      if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getIsSuccess()) {
                          
                          if (response.body().getCommItem().getPercentItem() != null) {
                            if (response.body().getCommItem().getPercentItem().getTotalPercentFinished() != null && response.body().getCommItem().getPercentItem().getTotalPercentFinished() >= 0) {
                              TextSecurePreferences.setTaskPercentage(ContextUtils.getApplicationContext(), (int)Math.round(response.body().getCommItem().getPercentItem().getTotalPercentFinished()));
                            }
                          }

                          AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                              mOfflineConfirmationDAO.delete(offlineConfirmations.get(0));
                
                              mLastActivityDAO.getLastActivityByTaskClientId(commItemDB.getTaskItem().getTaskId(), commItemDB.getTaskItem().getMandantId())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new DisposableSingleObserver<LastActivity>() {
                                  @Override
                                  public void onSuccess(LastActivity lastActivity) {
                                    updateLastActivity(mLastActivityDAO, lastActivity, 2, "CHANGED", -1);
                                  }
                    
                                  @Override
                                  public void onError(Throwable e) {
                                    // TODO:
                                  }
                                });
                            }
                          });
                        } else {
            
                          if (response.body().getIsException())
                            return;
  
                          if (response.body().getCommItem() != null && response.body().getCommItem().getPercentItem() != null) {
                            if (response.body().getCommItem().getPercentItem().getTotalPercentFinished() != null && response.body().getCommItem().getPercentItem().getTotalPercentFinished() >= 0) {
                              TextSecurePreferences.setTaskPercentage(ContextUtils.getApplicationContext(), (int)Math.round(response.body().getCommItem().getPercentItem().getTotalPercentFinished()));
                            }
                          }
            
                          if (response.body().getCommItem() != null) {
                            notify.setData(App.getInstance().gson.toJson(response.body().getCommItem()));
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
                                mNotifyDAO.updateNotify(notify);
                                mOfflineConfirmationDAO.delete(offlineConfirmations.get(0));
                                //AppUtils.playNotificationTone();
                              }
                            });
              
                            // TODO: LAST ACTIVITY
              
                            mLastActivityDAO.getLastActivityByTaskClientId(commItemDB.getTaskItem().getTaskId(), commItemDB.getTaskItem().getMandantId())
                              .observeOn(AndroidSchedulers.mainThread())
                              .subscribeOn(Schedulers.io())
                              .subscribe(new DisposableSingleObserver<LastActivity>() {
                                @Override
                                public void onSuccess(LastActivity lastActivity) {
                                  updateLastActivity(mLastActivityDAO, lastActivity, 6, "CHANGED BY ABONA", -1);
                                }
                  
                                @Override
                                public void onError(Throwable e) {
                                  // TODO:
                                }
                              });
                          } else {
                            AsyncTask.execute(new Runnable() {
                              @Override
                              public void run() {
                                mOfflineConfirmationDAO.delete(offlineConfirmations.get(0));
                              }
                            });
                          }
                        }
  
                        DelayReasonUtil.getDelayReasonsFromService(notify.getMandantId());
                      } else if (response.code() == 401) {

                            EventBus.getDefault().post(new LogEvent(getBaseContext().getString(R.string.log_token_error),
                                    LogType.SERVER_TO_APP, LogLevel.ERROR, getBaseContext().getString(R.string.log_title_activity),
                                    activityItem.getTaskId()));

                            handleAccessToken();
                      }
                    }
      
                    @Override
                    public void onFailure(Call<ResultOfAction> call, Throwable t) {
                      EventBus.getDefault().post(new LogEvent(getBaseContext().getString(R.string.log_activity_sent_error),
                              LogType.SERVER_TO_APP, LogLevel.ERROR, getBaseContext().getString(R.string.log_title_activity),
                              activityItem.getTaskId()));
                      allowRequest = true;
                      Log.e(TAG, t.getMessage());
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
    
                  //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                  //  Locale.getDefault());
                  //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                  //Date date = DateConverter.fromTimestamp(sdf.format(new Date()));
                  //Date date = DateConverter.fromTimestamp(App.getSdfUtc().format(new Date()));
                  confirmationItem.setTimeStampConfirmationUTC(/*date*/new Date());
                  confirmationItem.setMandantId(commItemDB.getTaskItem().getMandantId());
                  confirmationItem.setTaskId(commItemDB.getTaskItem().getTaskId());
                  confirmationItem.setTaskChangeId(commItemDB.getTaskItem().getTaskChangeId());
                  commItemReq.setConfirmationItem(confirmationItem);
    
                  final Call<ResultOfAction> call = App.getInstance().apiManager.getConfirmApi().confirm(commItemReq);
                  call.enqueue(new Callback<ResultOfAction>() {
                    @Override
                    public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
                      allowRequest = true;
                      if (response.isSuccessful()) {
          
                        if (response.body().getIsException())
                        {
                          EventBus.getDefault().post(new LogEvent(getBaseContext().getString(R.string.log_confirm_error),
                                  LogType.SERVER_TO_APP, LogLevel.INFO, getBaseContext().getString(R.string.log_title_open_confirm),
                                  confirmationItem.getTaskId()));
                          return;
                        }


                        if (response.body().getIsSuccess()) {
                          if (response.body().getCommItem() != null && response.body().getCommItem().getPercentItem() != null) {
                            if (response.body().getCommItem().getPercentItem().getTotalPercentFinished() != null && response.body().getCommItem().getPercentItem().getTotalPercentFinished() >= 0) {
                              TextSecurePreferences.setTaskPercentage(ContextUtils.getApplicationContext(), (int)Math.round(response.body().getCommItem().getPercentItem().getTotalPercentFinished()));
                            }
                          }


                          EventBus.getDefault().post(new LogEvent(getBaseContext().getString(R.string.log_confirm_send),
                                  LogType.SERVER_TO_APP, LogLevel.INFO, getBaseContext().getString(R.string.log_title_open_confirm),
                                  confirmationItem.getTaskId()));

                          AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                              mOfflineConfirmationDAO.delete(offlineConfirmations.get(0));
                
                              mLastActivityDAO.getLastActivityByTaskClientId(commItemDB.getTaskItem().getTaskId(), commItemDB.getTaskItem().getMandantId())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new DisposableSingleObserver<LastActivity>() {
                                  @Override
                                  public void onSuccess(LastActivity lastActivity) {
                                    if (offlineConfirmations.get(0).getConfirmType() == ConfirmationType.TASK_CONFIRMED_BY_DEVICE.ordinal()) {
                                      updateLastActivity(mLastActivityDAO, lastActivity, -1, "CONFIRMED BY DEVICE", 1);
                                      notify.setConfirmationStatus(1);
                                      AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                          mNotifyDAO.updateNotify(notify);
                                        }
                                      });
                                    } else if (offlineConfirmations.get(0).getConfirmType() == ConfirmationType.TASK_CONFIRMED_BY_USER.ordinal()) {
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
                                    // TODO:
                                  }
                                });
                            }
                          });
                        } else {
  
                          if (response.body() != null && response.body().getCommItem() != null && response.body().getCommItem().getPercentItem() != null) {
                            if (response.body().getCommItem().getPercentItem().getTotalPercentFinished() != null && response.body().getCommItem().getPercentItem().getTotalPercentFinished() >= 0) {
                              TextSecurePreferences.setTaskPercentage(ContextUtils.getApplicationContext(), (int)Math.round(response.body().getCommItem().getPercentItem().getTotalPercentFinished()));
                            }
                          }
            
                          if (response.body().getCommItem() != null) {
              
                            notify.setData(App.getInstance().gson.toJson(response.body().getCommItem()));
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
                                mNotifyDAO.updateNotify(notify);
                                //AppUtils.playNotificationTone();
                              }
                            });
              
                            mLastActivityDAO.getLastActivityByTaskClientId(commItemDB.getTaskItem().getTaskId(), commItemDB.getTaskItem().getMandantId())
                              .observeOn(AndroidSchedulers.mainThread())
                              .subscribeOn(Schedulers.io())
                              .subscribe(new DisposableSingleObserver<LastActivity>() {
                                @Override
                                public void onSuccess(LastActivity lastActivity) {
                                  updateLastActivity(mLastActivityDAO, lastActivity, 5, "CHANGED BY ABONA", -1);
                                }
                  
                                @Override
                                public void onError(Throwable e) {
                                  // TODO:
                                }
                              });
                          } else {
                            AsyncTask.execute(new Runnable() {
                              @Override
                              public void run() {
                                mOfflineConfirmationDAO.delete(offlineConfirmations.get(0));
                              }
                            });
                          }
                        }
          
                      } else {
          
                        // error case:
                        switch (response.code()) {
                          case 401:
                          {
                            EventBus.getDefault().post(new LogEvent(getBaseContext().getString(R.string.log_token_error),
                                    LogType.SERVER_TO_APP, LogLevel.INFO, getBaseContext().getString(R.string.log_title_open_confirm),
                                    confirmationItem.getTaskId()));
                            handleAccessToken();
                          }
                            break;
                        }
                      }
                    }
      
                    @Override
                    public void onFailure(Call<ResultOfAction> call, Throwable t) {
                      allowRequest = true;
                    }
                  });
                }
              }
  
              @Override
              public void onError(Throwable e) {
                AsyncTask.execute(new Runnable() {
                  @Override
                  public void run() {
                    mOfflineConfirmationDAO.delete(offlineConfirmations.get(0));
                    allowRequest = true;
                  }
                });
              }
            });
          
        } else {
          Log.i(TAG, ">>>>>>> NO JOB DO WORK");
        }
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
  
  private void updateGetAllTasks() {
    
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        Call<ResultOfAction> call = App.getInstance().apiManager.getTaskApi().getAllTasks(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
        call.enqueue(new Callback<ResultOfAction>() {
          @Override
          public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
            if (response.isSuccessful() && response.body() != null) {
              if(response.body().getAllTask().size() > 0)
              EventBus.getDefault().post(new LogEvent(getString(R.string.log_tasks_come), LogType.SERVER_TO_APP, LogLevel.INFO, getString(R.string.log_title_get_tasks_bg), 0));
              handleGetAllTasks(response.body());
            } else {
              
              switch (response.code()) {
                case 401: {
                  handleAccessToken();
                  EventBus.getDefault().post(new LogEvent(getString(R.string.log_token_error), LogType.SERVER_TO_APP, LogLevel.INFO, getString(R.string.log_title_get_tasks_bg), 0));
                } break;
              }
            }
          }
  
          @Override
          public void onFailure(Call<ResultOfAction> call, Throwable t) {
            EventBus.getDefault().post(new LogEvent(getString(R.string.log_tasks_error), LogType.SERVER_TO_APP, LogLevel.INFO, getString(R.string.log_title_get_tasks_bg), 0));
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
        deviceProfileItem.setInstanceId(deviceProfiles.get(0).getInstanceId());
        deviceProfileItem.setDeviceId(deviceProfiles.get(0).getDeviceId());
        deviceProfileItem.setModel(deviceProfiles.get(0).getDeviceModel());
        deviceProfileItem.setManufacturer(deviceProfiles.get(0).getDeviceManufacturer());
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
  
  private boolean isDeviceRegistrated() {
    if (TextSecurePreferences.isDeviceRegistrated()) {
      return true;
    } else {
      
      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
          List<DeviceProfile> deviceProfiles = mDeviceProfileDAO.getDeviceProfiles();
  
          if (deviceProfiles.size() > 0) {
            Log.i(TAG, ">>>>>>> PREPARE DEVICE REGISTRATION");
            
            App.eventBus.post(new RegistrationStartEvent());
  
            CommItem commItem = new CommItem();
            Header header = new Header();
            header.setDataType(DataType.DEVICE_PROFILE);
            header.setDeviceId(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
            commItem.setHeader(header);
  
            DeviceProfileItem deviceProfileItem = new DeviceProfileItem();
            deviceProfileItem.setInstanceId(deviceProfiles.get(0).getInstanceId());
            deviceProfileItem.setDeviceId(deviceProfiles.get(0).getDeviceId());
            deviceProfileItem.setModel(deviceProfiles.get(0).getDeviceModel());
            deviceProfileItem.setManufacturer(deviceProfiles.get(0).getDeviceManufacturer());
            deviceProfileItem.setCreatedDate(DateConverter.fromTimestamp(deviceProfiles.get(0).getCreatedAt()));
            deviceProfileItem.setUpdatedDate(DateConverter.fromTimestamp(deviceProfiles.get(0).getModifiedAt()));
            deviceProfileItem.setLanguageCode(deviceProfiles.get(0).getLanguageCode().replace("_", "-"));
            deviceProfileItem.setVersionCode(deviceProfiles.get(0).getVersionCode());
            deviceProfileItem.setVersionName(deviceProfiles.get(0).getVersionName());
            commItem.setDeviceProfileItem(deviceProfileItem);
  
            Call<ResultOfAction> call = App.getInstance().apiManager.getFCMApi().deviceProfile(commItem);
            call.enqueue(new Callback<ResultOfAction>() {
              @Override
              public void onResponse(Call<ResultOfAction> call, Response<ResultOfAction> response) {
                allowRequest = true;
                if (response.isSuccessful()) {
                  if (response.body() != null && response.body().getIsSuccess() && !response.body().getIsException()) {
                    Log.d(TAG, ">>>>>>> DEVICE REGISTRATION WAS SUCCESSFULLY!!!");
                    TextSecurePreferences.setDeviceRegistrated(true);
                    App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_DEVICE_REGISTRATED), null));
                    App.eventBus.post(new RegistrationFinishedEvent());
                  } else {
                    App.eventBus.post(new RegistrationErrorEvent());
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
                Log.d(TAG, ">>>>>>> ERROR ON DEVICE REGISTRATION!!!");
                allowRequest = true;
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
  
  @Override
  public void onLowMemory() {
    Log.i(TAG, "onLowMemory() called!");
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
}
