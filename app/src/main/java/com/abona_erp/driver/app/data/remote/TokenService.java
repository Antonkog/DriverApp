package com.abona_erp.driver.app.data.remote;

import com.abona_erp.driver.app.data.model.Data;
import com.abona_erp.driver.app.data.remote.response.PostResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TokenService {
  
  @Headers("Content-Type:application/json; charset=UTF-8")
  @POST("deviceprofile")
  Call<PostResponse> deviceProfile(@Body Data data);
}
