package com.abona_erp.driver.app.data.remote;

import com.abona_erp.driver.app.data.model.UploadResult;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileUploadService {
  
  @Multipart
  @POST("upload")
  Call<UploadResult> upload(
    @Part("MandantId") RequestBody mandantId,
    @Part("OrderNo") RequestBody orderNo,
    @Part("TaskId") RequestBody taskId,
    @Part("DriverNo") RequestBody driverNo,
    @Part MultipartBody.Part file
  );
}
