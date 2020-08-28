package com.redhotapp.driverapp.data.remote

import com.redhotapp.driverapp.data.model.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("/authentication")
    fun authentication(@Field("grant_type")  grantType :String, @Field("username")  username :String, @Field("password")  password :String) : Observable<Response<TokenResponse>>


//    @Headers("Content-Type:application/json; charset=UTF-8")
    @POST("/api/device/deviceprofile")
    fun deviceProfile(@Body commItem: CommItem?): Observable<ResultOfAction>


    @GET("api/device/GetAllTask")
    fun getAllTasks(@Query("deviceId") deviceId: String?): Observable<CommResponseItem>
//
//    @Headers("Content-Type:application/json")
//    @POST("api/activity/activity")
//    Call<ResultOfAction> activityChange(@Body CommItem commItem);
//
    @POST("api/Activity/Activity")
    fun postActivity(deviceId: String): Observable<CommResponseItem>
//
    @GET("api/uploader/documents")
    fun getDocuments(@Query("mandantId") mandantId: Int,@Query("orderNo") orderNo: Int, @Query("deviceId") deviceId: String ): Single<List<AppFileInterchangeItem>>

//    @Headers("Content-Type:application/json; charset=UTF-8")
//    @POST("api/confirmation/confirm")
//    Call<ResultOfAction> confirm(@Body CommItem commItem);
//
////
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