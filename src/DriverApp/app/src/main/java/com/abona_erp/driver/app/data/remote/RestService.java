package com.abona_erp.driver.app.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RestService {
  
  @GET("Version")
  Call<String> getVersion();
  
}
