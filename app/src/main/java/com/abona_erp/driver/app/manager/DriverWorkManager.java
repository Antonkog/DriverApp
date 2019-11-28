package com.abona_erp.driver.app.manager;
  
import android.content.Context;
import android.util.Log;
  
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.data.model.Data;
import com.abona_erp.driver.app.data.model.DataType;
import com.abona_erp.driver.app.data.model.DeviceProfileItem;
import com.abona_erp.driver.app.data.model.Header;
import com.abona_erp.driver.app.data.remote.response.PostResponse;
import com.abona_erp.driver.app.util.TextSecurePreferences;

import java.util.Date;
import java.util.Locale;
  
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverWorkManager extends Worker {
  
  private static final String TAG = DriverWorkManager.class.getSimpleName();
  
  public DriverWorkManager(
    @NonNull Context context,
    @NonNull WorkerParameters workerParameters
  ) {
    super(context, workerParameters);
  }
  
  private void process() {
    // TOKEN:
    String token = getInputData().getString("token");
    int state = getInputData().getInt("state", 0);

    Data mData = new Data();
    Header header = new Header();
    header.setDataType(DataType.DEVICE_PROFILE);
    mData.setHeader(header);
    
    DeviceProfileItem deviceProfileItem = new DeviceProfileItem();

    deviceProfileItem.setInstanceId(token);
    deviceProfileItem.setDeviceId(TextSecurePreferences.getDeviceIMEI(getApplicationContext()));
    
    switch (state) {
      case 0:
        deviceProfileItem.setModel(TextSecurePreferences.getDeviceModel(getApplicationContext()));
        deviceProfileItem.setManufacturer(TextSecurePreferences.getDeviceManufacturer(getApplicationContext()));
        deviceProfileItem.setSerial(TextSecurePreferences.getDeviceSerial(getApplicationContext()));
        Date currentDate = new Date();
        deviceProfileItem.setCreatedDate(currentDate);
        deviceProfileItem.setUpdatedDate(currentDate);
        deviceProfileItem.setLanguageCode(Locale.getDefault().toString());
        deviceProfileItem.setVersionCode(1000);
        deviceProfileItem.setVersionName("1.0.0.0");
        break;
    }
    
    mData.setDeviceProfileItem(deviceProfileItem);
    
    Call<PostResponse> call = App.apiManager.getTokenApi().deviceProfile(mData);
    call.enqueue(new Callback<PostResponse>() {
      @Override
      public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
        if (response.isSuccessful()) {
          Log.d(TAG, "***** Communicate with REST-API successfully!");
        }
      }
      
      @Override
      public void onFailure(Call<PostResponse> call, Throwable t) {
        Log.e(TAG, "***** Error on communication!");
        Result.retry();
      }
    });
  }
  
  @NonNull
  @Override
  public Result doWork() {
    Log.d(TAG, "***** doWork() started.");
    
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
