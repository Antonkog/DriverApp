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
  
  @NonNull
  @Override
  public Result doWork() {
    Log.d(TAG, "WorkManager - doWork() started...");
    
    // TOKEN:
    String token = getInputData().getString("token");
  
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
    deviceProfileItem.setModel(Build.MODEL);
    deviceProfileItem.setManufacturer(Build.MANUFACTURER);
    
    Log.d(TAG, "MODEL " + Build.MODEL);
    Log.d(TAG, "MANUFACTURER " + Build.MANUFACTURER);
    Log.d(TAG, "DEVICE " + Build.DEVICE);
    
    TelephonyManager tm = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
    try {
      deviceProfileItem.setDeviceId(tm.getDeviceId());
      Log.d(TAG, "ID " + tm.getDeviceId());
    } catch (SecurityException ignore){
    }
    
    deviceProfileItem.setSerial(Build.SERIAL);
    Log.d(TAG, "SERIAL " + Build.SERIAL);
    deviceProfileItem.setInstanceId(token);
    Log.d(TAG, "TOKEN " + token);

    Date currentDate = new Date();
    Log.d(TAG, currentDate.toString());
    deviceProfileItem.setCreatedDate(currentDate);

    
    // TODO: created and updated
    //long firstInstallTime = AppUtils.getAppFirstInstallTime(getApplicationContext());
    //mData.getDeviceProfileItem().setCreatedDate();
    // Updated
    //mData.getDeviceProfileItem().setLanguageCode(Locale.getDefault().toString());
    Log.d(TAG, Locale.getDefault().toString());
    deviceProfileItem.setLanguageCode(Locale.getDefault().toString());
    //mData.getDeviceProfileItem().setVersionCode(1000);
    //mData.getDeviceProfileItem().setVersionName("1.0.0.0");
    
    // TODO: versionName and versionCode
    
    mData.setDeviceProfileItem(deviceProfileItem);
    
    
    TokenService service = retrofit.create(TokenService.class);
    Call<PostResponse> call = service.deviceProfile(mData);
    call.enqueue(new Callback<PostResponse>() {
      @Override
      public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
        if (response.isSuccessful()) {
          Log.d("+++++", "Communicate with REST-API successfully!");
        }
      }
  
      @Override
      public void onFailure(Call<PostResponse> call, Throwable t) {
        Result.failure();
      }
    });
    
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
