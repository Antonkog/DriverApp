package com.abona_erp.driver.app.manager;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.abona_erp.driver.app.data.model.ActivityItem;
import com.abona_erp.driver.app.data.model.Data;
import com.abona_erp.driver.app.data.model.DataType;
import com.abona_erp.driver.app.data.model.DeviceProfileItem;
import com.abona_erp.driver.app.data.model.Header;
import com.abona_erp.driver.app.data.model.TaskItem;
import com.abona_erp.driver.app.util.DoubleJsonDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

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
  
    JsonDeserializer deserializer = new DoubleJsonDeserializer();
    Gson mGson = new GsonBuilder()
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      .registerTypeAdapter(double.class, deserializer)
      .registerTypeAdapter(Double.class, deserializer)
      .create();
  
    // DEFINING RETROFIT API SERVICE:
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl("http://172.30.1.38:4000/api/device/")
      .addConverterFactory(GsonConverterFactory.create(mGson))
      .build();
  
    Data mData = new Data();
    Header header = new Header();
    header.setDataType(DataType.DEVICE_PROFILE);
    mData.setHeader(header);
  
    DeviceProfileItem deviceProfileItem = new DeviceProfileItem();
  
    TelephonyManager tm = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
    try {
      deviceProfileItem.setDeviceId(tm.getDeviceId());
    } catch (SecurityException ignore){
    }
    
    switch (state) {
      case 0:
        deviceProfileItem.setModel(Build.MODEL);
        deviceProfileItem.setManufacturer(Build.MANUFACTURER);
        deviceProfileItem.setSerial(Build.SERIAL);
        deviceProfileItem.setInstanceId(token);
        Date currentDate = new Date();
        deviceProfileItem.setCreatedDate(currentDate);
        deviceProfileItem.setUpdatedDate(currentDate);
        deviceProfileItem.setLanguageCode(Locale.getDefault().toString());
        deviceProfileItem.setVersionCode(1000);
        deviceProfileItem.setVersionName("1.0.0.0");
        break;
        
      case 1:
        deviceProfileItem.setInstanceId(token);
        break;
    }
  
    mData.setDeviceProfileItem(deviceProfileItem);
    
    TokenService service = retrofit.create(TokenService.class);
    Call<PostResponse> call = service.deviceProfile(mData);
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
  
  @NonNull
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
  
  private interface TokenService {
    @POST("deviceprofile")
    Call<PostResponse> deviceProfile(@Body Data data);
  }
  
  private class PostResponse {
  
    @SerializedName("IsSuccess")
    Boolean isSuccess;
    
    @SerializedName("Text")
    String text;
    
    @SerializedName("DeviceProfileItem")
    DeviceProfileItem deviceProfileItem;
    
    @SerializedName("TaskItem")
    TaskItem taskItem;
    
    @SerializedName("ActivityItem")
    ActivityItem activityItem;
    
    public Boolean getIsSuccess() {
      return isSuccess;
    }
    
    public void setIsSuccess(Boolean isSuccess) {
      this.isSuccess = isSuccess;
    }
    
    public String getText() {
      return text;
    }
    
    public void setText(String text) {
      this.text = text;
    }
    
    public DeviceProfileItem getDeviceProfileItem() {
      return deviceProfileItem;
    }
    
    public void setDeviceProfileItem(DeviceProfileItem deviceProfileItem) {
      this.deviceProfileItem = deviceProfileItem;
    }
    
    public TaskItem getTaskItem() {
      return taskItem;
    }
    
    public void setTaskItem(TaskItem taskItem) {
      this.taskItem = taskItem;
    }
    
    public ActivityItem getActivityItem() {
      return activityItem;
    }
    
    public void setActivityItem(ActivityItem activityItem) {
      this.activityItem = activityItem;
    }
  }
}
