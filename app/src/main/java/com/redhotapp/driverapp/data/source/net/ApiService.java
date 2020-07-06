package com.redhotapp.driverapp.data.source.net;

import com.redhotapp.driverapp.data.Result;
import com.redhotapp.driverapp.data.source.local.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiService {

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("authentication")
    Call<String> authentication(@Body String body);

//
//    @Headers("Content-Type:application/json; charset=UTF-8")
//    @POST("api/device/deviceprofile")
//    Call<ResultOfAction> deviceProfile(@Body CommItem commItem);
//
//
    @GET("api/device/GetAllTask")
    Observable<Result<List<Task>>> getAllTasks (@Query("deviceId") String deviceId);
//
//    @Headers("Content-Type:application/json")
//    @POST("api/activity/activity")
//    Call<ResultOfAction> activityChange(@Body CommItem commItem);
//
//    @Headers("Content-Type:application/json; charset=UTF-8")
//    @POST("api/confirmation/confirm")
//    Call<ResultOfAction> confirm(@Body CommItem commItem);
//
//
//    @Headers("Content-Type:application/json")
//    @GET("api/uploader/documents")
//    Call<ArrayList<AppFileInterchangeItem>> getDocuments(@Query("mandantId") int mandantId, @Query("orderNo") int orderNo, @Query("deviceId") String deviceId);
//
//
//    @Streaming
//    @GET("api/uploader/download")
//    Call<ResponseBody> downloadFile(@Query("id") String id);
//
//    @Multipart
//    @POST("api/uploader/upload")
//    Call<UploadResult> upload(
//            @Part("MandantId") RequestBody mandantId,
//            @Part("OrderNo") RequestBody orderNo,
//            @Part("TaskId") RequestBody taskId,
//            @Part("DriverNo") RequestBody driverNo,
//            @Part("DMSDocumentType") RequestBody documentType,
//            @Part MultipartBody.Part file
//    );

}