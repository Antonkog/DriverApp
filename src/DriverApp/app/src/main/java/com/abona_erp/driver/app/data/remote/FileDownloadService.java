package com.abona_erp.driver.app.data.remote;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface FileDownloadService {
  
  @Streaming
  @GET("download")
  Call<ResponseBody> downloadFile(@Query("id") String id);
}
