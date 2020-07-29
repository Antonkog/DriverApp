package com.abona_erp.driver.app.data.remote;

import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ResultOfAction;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMService {
  
  @Headers("Content-Type:application/json; charset=UTF-8")
  @POST("deviceprofile")
  Call<ResultOfAction> deviceProfile(@Body CommItem commItem);

  @Headers("Content-Type:application/json; charset=UTF-8")
  @POST("deviceprofile")
  Single<ResultOfAction> updateDevice(@Body CommItem commItem);
}
