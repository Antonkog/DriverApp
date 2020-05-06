package com.abona_erp.driver.app.data.remote;

import com.abona_erp.driver.app.data.model.ResultOfAction;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TaskService {
  
  @GET("GetAllTask")
  Call<ResultOfAction> getAllTasks(@Query("deviceId") String deviceId);
}
