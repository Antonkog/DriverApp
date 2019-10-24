package com.abona_erp.driver.app.service;


import android.os.Bundle;
import android.util.Log;

import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.repository.NotifyRepository;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NotifyJobService extends JobService {
  
  private static final String TAG = "NotifyJobService";
  
  private final Executor executor = Executors.newFixedThreadPool(2);
  private NotifyDao notifyDao = NotifyRepository.getNotifyDatabase(this).notifyDao();
  
  @Override
  public boolean onStartJob(JobParameters jobParameters) {
    Log.d(TAG, "updating ROOM database with latest notifies");
    
    addNotifyDataToSQLiteDatabase(jobParameters.getExtras());
    return false;
  }
  
  @Override
  public boolean onStopJob(JobParameters jobParameters) {
    return false;
  }
  
  // add data to sqlite database using room.
  private void addNotifyDataToSQLiteDatabase(Bundle bundle) {
    
    final Notify notifyObj = getNotifyObjectFromBundle(bundle);
    executor.execute(new Runnable() {
      @Override
      public void run() {
        long rec = notifyDao.insertNotify(notifyObj);
        Log.d(TAG, "added record to db " + rec);
      }
    });
  }
  
  private Notify getNotifyObjectFromBundle(Bundle bundle) {
    Notify notify = new Notify();
    notify.setData(bundle.getString("data"));
    notify.setSelected(false);
    
    return notify;
  }
}
