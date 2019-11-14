package com.abona_erp.driver.app.service;

import android.util.Log;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.Data;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.data.repository.DriverRepository;
import com.abona_erp.driver.app.util.AppUtils;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class NotificationService extends JobService {
  
  private static final String TAG = NotificationService.class.getSimpleName();
  
  boolean isWorking = false;
  boolean jobCancelled = false;
  
  Data mData;
  DriverRepository mRepository;
  
  public NotificationService() {
    mData = new Data();
    mRepository = new DriverRepository(getApplication());
  }
  
  @Override
  public boolean onStartJob(JobParameters jobParameters) {
    Log.d(TAG, "***** onStartJob() - JOB STARTED *****");
    
    isWorking = true;
    startWorkOnNewThread(jobParameters);
    
    return false;
  }
  
  private void startWorkOnNewThread(final JobParameters jobParameters) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        doWork(jobParameters);
      }
    }).start();
  }
  
  private void doWork(JobParameters jobParameters) {
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
  
              mRepository.insert(notify);
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
}
