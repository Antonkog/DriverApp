package com.abona_erp.driver.app.service;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.ConfirmationItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.Data;
import com.abona_erp.driver.app.data.model.DataType;
import com.abona_erp.driver.app.data.model.Header;
import com.abona_erp.driver.app.data.model.TaskItem;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.data.repository.DriverRepository;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DateConverter;
import com.abona_erp.driver.app.util.concurrent.MainUiThread;
import com.abona_erp.driver.app.util.concurrent.ThreadExecutor;
import com.abona_erp.driver.core.base.ContextUtils;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.Callable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends JobService implements MediaPlayer.OnPreparedListener {
  
  private static final String TAG = NotificationService.class.getSimpleName();
  
  boolean isWorking = false;
  boolean jobCancelled = false;
  
  Data mData;
  DriverRepository mRepository;
  
  Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
  private MediaPlayer mMediaPlayer;
  
  MainUiThread mainUiThread;
  ThreadExecutor mThreadExecutor;
  
  public NotificationService() {
    mData = new Data();
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
      mData = new Data();
      mData = App.getGson().fromJson(raw, Data.class);
      
      if (mData.getTaskItem().getMandantId() != null && mData.getTaskItem().getTaskId() != null) {
        mRepository.getNotifyByMandantTaskId(mData.getTaskItem().getMandantId(), mData.getTaskItem().getTaskId()).observeOn(AndroidSchedulers.mainThread())
          .subscribeOn(Schedulers.io())
          .subscribe(new DisposableSingleObserver<Notify>() {
            @Override
            public void onSuccess(Notify notify) {
              Log.d(TAG, "***** VORHANDEN - UPDATEN *****");
  
              notify.setData(raw);
              notify.setRead(false);
              if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
                notify.setStatus(0);
              } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
                notify.setStatus(50);
              } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
                notify.setStatus(90);
              } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
                notify.setStatus(100);
              } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.BREAK)) {
                notify.setStatus(51);
              }
              notify.setTaskDueFinish(mData.getTaskItem().getTaskDueDateFinish());
              notify.setOrderNo(mData.getTaskItem().getOrderNo());
              notify.setModifiedAt(AppUtils.getCurrentDateTime());
  
              mRepository.update(notify);
  
              LastActivity lastActivity = new LastActivity();
              lastActivity.setStatusType(2);
              lastActivity.setMandantOid(mData.getTaskItem().getMandantId());
              lastActivity.setTaskOid(mData.getTaskItem().getTaskId());
              lastActivity.setOrderNo(mData.getTaskItem().getOrderNo());
              lastActivity.setCreatedAt(AppUtils.getCurrentDateTime());
              lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
              mRepository.insert(lastActivity);
  
              Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
              Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
              r.play();
            }
  
            @Override
            public void onError(Throwable e) {
              Log.d(TAG, "***** NICHT VORHANDEN - INSERT *****");
              
              Notify notify = new Notify();
              notify.setMandantId(mData.getTaskItem().getMandantId());
              notify.setTaskId(mData.getTaskItem().getTaskId());
              notify.setData(raw);
              notify.setRead(false);
              if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
                notify.setStatus(0);
              } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
                notify.setStatus(50);
              } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.CMR)) {
                notify.setStatus(90);
              } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.FINISHED)) {
                notify.setStatus(100);
              } else if (mData.getTaskItem().getTaskStatus().equals(TaskStatus.BREAK)) {
                notify.setStatus(51);
              }
              notify.setTaskDueFinish(mData.getTaskItem().getTaskDueDateFinish());
              notify.setOrderNo(mData.getTaskItem().getOrderNo());
              notify.setCreatedAt(AppUtils.getCurrentDateTime());
              notify.setModifiedAt(AppUtils.getCurrentDateTime());
              
              Data confirmData = new Data();
              Header confirmHeader = new Header();
              confirmHeader.setDataType(DataType.CONFIRMATION);
              confirmHeader.setTimestampSenderUTC(mData.getHeader().getTimestampSenderUTC());
              confirmData.setHeader(confirmHeader);
              ConfirmationItem confirmationItem = new ConfirmationItem();
              confirmationItem.setConfirmationType(ConfirmationType.TASK_CONFIRMED_BY_DEVICE);
              Date date = DateConverter.fromTimestamp(new Date().toString());
              confirmationItem.setTimeStampConfirmationUTC(date);
              confirmationItem.setMandantId(mData.getTaskItem().getMandantId());
              confirmationItem.setTaskId(mData.getTaskItem().getTaskId());
              //confirmationItem.setTaskItem(mData.getTaskItem());
              confirmationItem.setTaskChangeId(mData.getTaskItem().getTaskChangeId());
              confirmData.setConfirmationItem(confirmationItem);
              
              Call<Data> call = App.apiManager.getConfirmApi().confirm(confirmData);
              call.enqueue(new Callback<Data>() {
                @Override
                public void onResponse(Call<Data> call, Response<Data> response) {
                  if (response.isSuccessful()) {
                    Log.d(TAG, "SUCCESSFUL");
                    Log.d(TAG, response.toString());
                  } else {
                    Log.w(TAG, "ERROR in onResponse");
                  }
                }
  
                @Override
                public void onFailure(Call<Data> call, Throwable t) {
                  Log.w(TAG, t.getMessage());
                }
              });
  
              mRepository.insert(notify);
  
              startRingtone(notification);
            }
          });
      } else {
        Log.w(TAG, "Keine gültige Task Item");
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
      mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
