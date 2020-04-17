package com.abona_erp.driver.app.data.remote;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TokenService {
  
  @Headers("Content-Type:application/x-www-form-urlencoded")
  @POST("authentication")
  Call<String> authentication(@Body String body);
}
