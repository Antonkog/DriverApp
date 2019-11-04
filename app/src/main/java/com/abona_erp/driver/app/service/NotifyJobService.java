package com.abona_erp.driver.app.service;

import android.os.Bundle;
import android.util.LayoutDirection;
import android.util.Log;

import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.repository.DriverRepository;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DateConverter;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NotifyJobService extends JobService {
  
  private static final String TAG = NotifyJobService.class.getSimpleName();
  
  //private final Executor executor = Executors.newFixedThreadPool(2);
  //private NotifyDao notifyDao = DriverRepository.getNotifyDatabase(this).notifyDao();
  
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
    DriverRepository repository = new DriverRepository(getApplication());
    repository.insert(notifyObj);
  }
  
  private Notify getNotifyObjectFromBundle(Bundle bundle) {
    String rawJson = bundle.getString("data");
  
    Notify notify = new Notify();
    notify.setData(rawJson);
    notify.setRead(false);
    notify.setCreatedAt(AppUtils.getCurrentDateTime());
    notify.setModifiedAt(AppUtils.getCurrentDateTime());
    
    try {
      JSONObject jsonRoot = new JSONObject(rawJson);
      JSONObject jsonTaskItem = jsonRoot.getJSONObject("TaskItem");
      notify.setStatus(jsonTaskItem.getInt("Status"));
      notify.setTaskDueFinish(DateConverter.fromTimestamp(jsonTaskItem.getString("TaskDueDateFinish")));

      synchronized (this) {
        DriverRepository repository = new DriverRepository(getApplication());

        LastActivity lastActivity = new LastActivity();
        lastActivity.setStatusName("NEW");
        lastActivity.setOrderNo(jsonTaskItem.getInt("OrderNo"));
        lastActivity.setCreatedAt(AppUtils.getCurrentDateTime());
        lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
        repository.insert(lastActivity);
      }
    } catch (JSONException ignore) {
    }
    
    return notify;
  }
}
