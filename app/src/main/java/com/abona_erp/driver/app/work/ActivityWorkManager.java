package com.abona_erp.driver.app.work;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.data.model.ActivityItem;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.Data;
import com.abona_erp.driver.app.data.model.DataType;
import com.abona_erp.driver.app.data.model.Header;
import com.abona_erp.driver.app.data.remote.response.PostResponse;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityWorkManager extends Worker {
  
  private static final String TAG = ActivityWorkManager.class.getSimpleName();
  
  public ActivityWorkManager(
    @NonNull Context context,
    @NonNull WorkerParameters workerParameters
  ) {
    super(context, workerParameters);
  }
  
  private void process() {
    
    int mHeaderType = getInputData().getInt("header_type", 0);
    int mMandantId = getInputData().getInt("mandant_id", -1);
    int mTaskId = getInputData().getInt("task_id", -1);
    int mActivityId = getInputData().getInt("activity_id", -1);
    String mName = getInputData().getString("name");
    String mDescription;
    if (getInputData().getString("description") != null) {
      mDescription = getInputData().getString("description");
    }
    String mCreated = getInputData().getString("started");
    String mFinished = getInputData().getString("finished");
    int mStatus = getInputData().getInt("status", 0);
    int mSequence = getInputData().getInt("sequence", 0);

    Data mData = new Data();
    Header header = new Header();
    if (mHeaderType == 0)
      header.setDataType(DataType.ACTIVITY);
    else if (mHeaderType == 1)
      header.setDataType(DataType.UNDO_ACTIVITY);
    mData.setHeader(header);
    
    ActivityItem activityItem = new ActivityItem();
    activityItem.setMandantId(mMandantId);
    activityItem.setTaskId(mTaskId);
    activityItem.setActivityId(mActivityId);
    activityItem.setName(mName);
    if (mStatus == 0) // PENDING
      activityItem.setStatus(ActivityStatus.PENDING);
    else if (mStatus == 1) { //RUNNING
      try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date started = sdf.parse(mCreated);
        activityItem.setStarted(started);
        
        Date finished = sdf.parse(mFinished);
        activityItem.setFinished(finished);
      } catch (ParseException e) {
        Log.e(TAG, e.getMessage());
      }
      activityItem.setStatus(ActivityStatus.RUNNING);
    }
    else if (mStatus == 2) {
      try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date started = sdf.parse(mCreated);
        activityItem.setStarted(started);
    
        Date finished = sdf.parse(mFinished);
        activityItem.setFinished(finished);
      } catch (ParseException e) {
        Log.e(TAG, e.getMessage());
      }
      activityItem.setStatus(ActivityStatus.FINISHED);
    }
    activityItem.setSequence(mSequence);
    
    mData.setActivityItem(activityItem);
  
    
    Call<PostResponse> call = App.apiManager.getActivityApi().activityChange(mData);
    call.enqueue(new Callback<PostResponse>() {
      @Override
      public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
        if (response.isSuccessful()) {
          Log.d(TAG, "==============================================================================");
          Log.d(TAG, "Communicate with REST-API successfully!");
          Log.d(TAG, "==============================================================================");
        }
      }
  
      @Override
      public void onFailure(Call<PostResponse> call, Throwable t) {
        Log.d(TAG, "==============================================================================");
        Log.e(TAG, "Error on communication!");
        Log.d(TAG, "==============================================================================");
        Result.retry();
      }
    });
  }
  
  @Override
  public Result doWork() {
    Log.d(TAG, "==============================================================================");
    Log.d(TAG, "doWork() started.");
    Log.d(TAG, "==============================================================================");
  
    if (getRunAttemptCount() > 3) {
      return Result.failure();
    }
  
    try {
      process();
    } catch (Exception e) {
      e.printStackTrace();
      return Result.retry();
    }
    return Result.success();
  }
}
