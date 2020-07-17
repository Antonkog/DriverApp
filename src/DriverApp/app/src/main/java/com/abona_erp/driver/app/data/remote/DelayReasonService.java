package com.abona_erp.driver.app.data.remote;

import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ResultOfAction;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DelayReasonService {
  
  @Headers("Content-Type:application/json")
  @GET("GetDelayReasons")
  Call<ResultOfAction> getDelayReasons(@Query("mandantId") int mandantId, @Query("languageCode") String languageCode);
  
  @Headers("Content-Type:application/json")
  @POST("DelayReasons")
  Call<ResultOfAction> setDelayReasons(@Body CommItem commItem);
}
