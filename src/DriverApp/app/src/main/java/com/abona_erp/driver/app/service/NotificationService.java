package com.abona_erp.driver.app.service;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.DataType;
import com.abona_erp.driver.app.data.model.LastActivityDetails;
import com.abona_erp.driver.app.data.model.TaskActionType;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.data.repository.DriverRepository;
import com.abona_erp.driver.app.ui.event.DocumentEvent;
import com.abona_erp.driver.app.ui.event.ProfileEvent;
import com.abona_erp.driver.app.ui.event.TaskStatusEvent;
import com.abona_erp.driver.app.ui.event.VehicleRegistrationEvent;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.concurrent.MainUiThread;
import com.abona_erp.driver.app.util.concurrent.ThreadExecutor;
import com.abona_erp.driver.core.base.ContextUtils;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class NotificationService extends JobService implements MediaPlayer.OnPreparedListener {
  
  private static final String TAG = NotificationService.class.getSimpleName();
  
  boolean isWorking = false;
  boolean jobCancelled = false;
  
  CommItem mCommItem;
  DriverRepository mRepository;
  
  Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
  private MediaPlayer mMediaPlayer;
  
  MainUiThread mainUiThread;
  ThreadExecutor mThreadExecutor;
  
  public NotificationService() {
    mCommItem = new CommItem();
    mRepository = new DriverRepository(getApplication());
    
    mThreadExecutor = ThreadExecutor.getInstance();
    mainUiThread = MainUiThread.getInstance();
    
    mMediaPlayer = new MediaPlayer();
    setRingtonePlayer();
  }
  
  @Override
  public boolean onStartJob(JobParameters jobParameters) {
    Log.d(TAG, "***** onStartJob() - JOB STARTED *****");
    
    isWorking = true;
    startWorkOnNewThread(jobParameters);
    
    return false;
  }
  
  private void startWorkOnNewThread(final JobParameters jobParameters) {
    if (mThreadExecutor == null)
      return;
    
    mThreadExecutor.addCallable(new Callable() {
      @Override
      public Object call() throws Exception {
        try {
          // check if thread is interrupted before lengthy operation.
          if (Thread.interrupted())
            throw new InterruptedException();
          
          doWork(jobParameters);
          
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return null;
      }
    });
  }
  
  private void doWork(JobParameters jobParameters) {
    Log.d(TAG, "--------------------------------------------------------------");
    
    if (jobParameters == null)
      return;
    try {
      String raw = jobParameters.getExtras().getString("data");
      if (raw == null)
        return;
      Log.i(TAG, raw);
      mCommItem = new CommItem();
      mCommItem = App.getInstance().gsonUtc.fromJson(raw, CommItem.class);
      
      // CHECK VEHICLE REGISTRATION NUMBER:
      if (mCommItem.getHeader().getDataType().equals(DataType.VEHICLE)) {
        VehicleRegistrationEvent event = new VehicleRegistrationEvent();
        if (mCommItem.getVehicleItem() != null) {
          if (mCommItem.getVehicleItem().getRegistrationNumber() != null) {
            TextSecurePreferences.setVehicleRegistrationNumber(getApplicationContext(),
              mCommItem.getVehicleItem().getRegistrationNumber());
          } else {
            TextSecurePreferences.setVehicleRegistrationNumber(getApplicationContext(),
              getApplicationContext().getResources().getString(R.string.registration_number));
            
            // RESET ALL ITEMS:
            event.setDeleteAll(true);
            mRepository.deleteAllNotify();
            AsyncTask.execute(new Runnable() {
              @Override
              public void run() {
                mRepository.deleteAllLastActivities();
                DriverDatabase db = DriverDatabase.getDatabase();
                OfflineConfirmationDAO dao = db.offlineConfirmationDAO();
                dao.deleteAll();
              }
            });
          }
          if (mCommItem.getVehicleItem().getClientName() != null) {
            TextSecurePreferences.setClientName(getApplicationContext(),
              mCommItem.getVehicleItem().getClientName());
          } else {
            TextSecurePreferences.setClientName(getApplicationContext(), "");
          }
          
          // Drivers
          if (mCommItem.getVehicleItem().getDrivers() != null) {
            if (mCommItem.getVehicleItem().getDrivers().size() > 0) {
              if (mCommItem.getVehicleItem().getDrivers().get(0).getImageUrl() != null) {
                // First Driver
                App.eventBus.post(new ProfileEvent(mCommItem.getVehicleItem().getDrivers().get(0).getImageUrl()));
              }
            }
          }
        } else {
          TextSecurePreferences.setVehicleRegistrationNumber(getApplicationContext(),
            getApplicationContext().getResources().getString(R.string.registration_number));
          TextSecurePreferences.setClientName(getApplicationContext(), "");
        }
        
        App.eventBus.post(event);
        startRingtone(notification);
        return;
      } else if (mCommItem.getHeader().getDataType().equals(DataType.DOCUMENT)) {
        App.eventBus.post(new DocumentEvent(mCommItem.getDocumentItem().getMandantId(), mCommItem.getDocumentItem().getOrderNo()));
        return;
      }
      
      if (mCommItem.getPercentItem() != null) {
        if (mCommItem.getPercentItem().getTotalPercentFinished() != null && mCommItem.getPercentItem().getTotalPercentFinished() >= 0) {
          TextSecurePreferences.setTaskPercentage(ContextUtils.getApplicationContext(), (int)Math.round(mCommItem.getPercentItem().getTotalPercentFinished()));
          App.eventBus.post(new TaskStatusEvent((int)Math.round(mCommItem.getPercentItem().getTotalPercentFinished())));
        }
      }
      
      if (mCommItem.getTaskItem().getMandantId() != null && mCommItem.getTaskItem().getTaskId() != null) {
        mRepository.getNotifyByMandantTaskId(mCommItem.getTaskItem().getMandantId(), mCommItem.getTaskItem().getTaskId()).observeOn(AndroidSchedulers.mainThread())
          .subscribeOn(Schedulers.io())
          .subscribe(new DisposableSingleObserver<Notify>() {
            @Override
            public void onSuccess(Notify notify) {
              Log.d(TAG, "***** VORHANDEN - UPDATEN *****");
  
              notify.setData(raw);
              notify.setRead(false);
              if (mCommItem.getPercentItem() != null) {
                if (mCommItem.getPercentItem().getPercentFinished() != null && mCommItem.getPercentItem().getPercentFinished() >= 0) {
                  notify.setPercentFinished((int)Math.round(mCommItem.getPercentItem().getPercentFinished()));
                }
              }
              notify.setStatus(mCommItem.getTaskItem().getTaskStatus());
              notify.setTaskDueFinish(mCommItem.getTaskItem().getTaskDueDateFinish());
              notify.setOrderNo(mCommItem.getTaskItem().getOrderNo());
              notify.setModifiedAt(AppUtils.getCurrentDateTime());
  
              mRepository.update(notify);
              startRingtone(notification);
              
              mRepository.getLastActivityByTaskClientId(mCommItem.getTaskItem().getTaskId(), mCommItem.getTaskItem().getMandantId()).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableSingleObserver<LastActivity>() {
                  @Override
                  public void onSuccess(LastActivity lastActivity) {
                    lastActivity.setCustomer(mCommItem.getTaskItem().getKundenName());
                    lastActivity.setOrderNo(AppUtils.parseOrderNo(mCommItem.getTaskItem().getOrderNo()));
                    lastActivity.setStatusType(LastActivity.UPDATE);
                    lastActivity.setConfirmStatus(0);
                    lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
  
                    if (mCommItem.getTaskItem().getActionType().equals(TaskActionType.PICK_UP)) {
                      lastActivity.setTaskActionType(0);
                    } else if (mCommItem.getTaskItem().getActionType().equals(TaskActionType.DROP_OFF)) {
                      lastActivity.setTaskActionType(1);
                    } else if (mCommItem.getTaskItem().getActionType().equals(TaskActionType.TRACTOR_SWAP)) {
                      lastActivity.setTaskActionType(3);
                    } else if (mCommItem.getTaskItem().getActionType().equals(TaskActionType.GENERAL)) {
                      lastActivity.setTaskActionType(2);
                    } else if (mCommItem.getTaskItem().getActionType().equals(TaskActionType.DELAY)) {
                      lastActivity.setTaskActionType(4);
                    } else if (mCommItem.getTaskItem().getActionType().equals(TaskActionType.UNKNOWN)) {
                      lastActivity.setTaskActionType(100);
                    }
                    
                    /*
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                      Locale.getDefault());
                    Date timestamp = new Date();
                    lastActivity.setModifiedAt(sdf.format(timestamp));
                     */
                    ArrayList<String> _list = lastActivity.getDetailList();
  
                    LastActivityDetails _detail = new LastActivityDetails();
                    _detail.setDescription("UPDATE");
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                      Locale.getDefault());
                    _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
                    _list.add(App.getInstance().gson.toJson(_detail));
                    lastActivity.setDetailList(_list);
                    if (mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING) || mCommItem.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
                      lastActivity.setVisible(true);
                    } else {
                      lastActivity.setVisible(false);
                    }
                    mRepository.update(lastActivity);
  
                    DriverDatabase db = DriverDatabase.getDatabase();
                    OfflineConfirmationDAO dao = db.offlineConfirmationDAO();
                    
                    OfflineConfirmation offlineConfirmation = new OfflineConfirmation();
                    offlineConfirmation.setNotifyId(notify.getId());
                    offlineConfirmation.setConfirmType(ConfirmationType.TASK_CONFIRMED_BY_DEVICE.ordinal());
                    AsyncTask.execute(new Runnable() {
                      @Override
                      public void run() {
                        dao.insert(offlineConfirmation);
                      }
                    });
                  }
  
                  @Override
                  public void onError(Throwable e) {
    
                  }
                });
            }
  
            @Override
            public void onError(Throwable e) {
              Log.i(TAG, "******* TASK NICHT VORHANDEN - HINZUFÜGEN *******");
              
              Notify notify = new Notify();
              notify.setMandantId(mCommItem.getTaskItem().getMandantId());
              notify.setTaskId(mCommItem.getTaskItem().getTaskId());
              if (mCommItem.getPercentItem() != null) {
                if (mCommItem.getPercentItem().getPercentFinished() != null && mCommItem.getPercentItem().getPercentFinished() >= 0) {
                  notify.setPercentFinished((int)Math.round(mCommItem.getPercentItem().getPercentFinished()));
                }
              }
              notify.setData(raw);
              notify.setRead(false);
              notify.setStatus(mCommItem.getTaskItem().getTaskStatus());
              notify.setTaskDueFinish(mCommItem.getTaskItem().getTaskDueDateFinish());
              notify.setOrderNo(mCommItem.getTaskItem().getOrderNo());
              notify.setCreatedAt(AppUtils.getCurrentDateTime());
              notify.setModifiedAt(AppUtils.getCurrentDateTime());
              mRepository.insert(notify);
              startRingtone(notification);
            }
          });
      } else {
        Log.w(TAG, "Keine gültiger Task Item");
      }
    } catch (NullPointerException e) {
      Log.w(TAG, e.getMessage());
    }
  }
  
  // Called if the job was cancelled before being finished.
  @Override
  public boolean onStopJob(JobParameters jobParameters) {
    Log.d(TAG, "***** onStopJob() - Job cancelled before being completed.");
    jobCancelled = true;
    boolean needsReschedule = isWorking;
    jobFinished(jobParameters, needsReschedule);
    return needsReschedule;
  }
  
  private void setRingtonePlayer() {
    mMediaPlayer.setWakeMode(ContextUtils.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build());
    } else {
      mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
    }
    mMediaPlayer.setOnPreparedListener(this);
  }
  
  private void startRingtone(Uri uri) {
    try {
      mMediaPlayer.reset();
      mMediaPlayer.setDataSource(ContextUtils.getApplicationContext(), uri);
      mMediaPlayer.prepareAsync();
      mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
          mMediaPlayer.stop();
          mMediaPlayer.release();
          mMediaPlayer = null;
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void onPrepared(MediaPlayer mp) {
    mp.start();
  }
}
