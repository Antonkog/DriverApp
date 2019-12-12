package com.abona_erp.driver.app.data.remote;

import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ResultOfAction;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ActivityService {
  
  @Headers("Content-Type:application/json")
  @POST("activity")
  Call<ResultOfAction> activityChange(@Body CommItem commItem);
}
