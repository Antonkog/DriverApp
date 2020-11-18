package com.abona_erp.driver.app.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.util.DelayReasonUtil;
import com.abona_erp.driver.app.util.TextSecurePreferences;

public class DelayReasonWorker extends Worker {
  
  private static final String TAG = DelayReasonWorker.class.getSimpleName();
  
  public DelayReasonWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParameters) {
    super(appContext, workerParameters);
  }
  
  @NonNull
  @Override
  public Result doWork() {
    
    Context appContext = getApplicationContext();
  
    Log.i(TAG, "Get Delay Reason Text --------------------------------------------------");
  
    DelayReasonUtil.getDelayReasonsFromService(TextSecurePreferences.getMandantID());
    
    return Result.success();
  }
  
  @Override
  public void onStopped() {
    super.onStopped();
    Log.i(TAG, "OnStopped called for this worker.");
  }
}
