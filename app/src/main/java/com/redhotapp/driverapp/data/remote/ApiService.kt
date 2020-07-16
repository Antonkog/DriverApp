package com.redhotapp.driverapp.data.remote

import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("/authentication")
    fun authentication(@Field("grant_type")  grantType :String, @Field("username")  username :String, @Field("password")  password :String) : Observable<Response<String>>

//
//    @Headers("Content-Type:application/json; charset=UTF-8")
//    @POST("api/device/deviceprofile")
//    Call<ResultOfAction> deviceProfile(@Body CommItem commItem);
//
//
//    @GET("api/device/GetAllTask")
//    fun getAllTasks(@Query("deviceId") deviceId: String?): io.reactivex.Observable<Result<List<Task?>?>?>?
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