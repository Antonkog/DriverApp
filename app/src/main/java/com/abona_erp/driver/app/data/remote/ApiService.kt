package com.abona_erp.driver.app.data.remote

import com.abona_erp.driver.app.data.model.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface ApiService {
    @FormUrlEncoded
    @POST("/authentication")
    fun authentication(@Field("grant_type")  grantType :String, @Field("username")  username :String, @Field("password")  password :String) : Observable<Response<TokenResponse>>

    @POST("/api/device/deviceprofile")
    fun setDeviceProfile(@Body commItem: CommItem?): Observable<ResultOfAction>


    @GET("api/device/GetAllTask")
    suspend fun getAllTasks(@Query("deviceId") deviceId: String?): CommResponseItem

    @POST("api/activity/activity")
    fun postActivityChange(@Body commItem: CommItem): Observable<ResultOfAction>


//    @Headers("Content-Type:application/json")
//    @GET("documents")
//    fun getDocuments(
//        @Query("mandantId") mandantId: Int,
//        @Query("orderNo") orderNo: Int,
//        @Query("deviceId") deviceId: String?
//    ): Call<ArrayList<AppFileInterchangeItem?>?>?

    @GET("api/uploader/documents")
    fun getDocuments(
        @Query("mandantId") mandantId: Int,
        @Query("orderNo") orderNo: Int,
        @Query("deviceId") deviceId: String
    ): Single<List<DocumentResponse>>
   //  https://213.144.11.162:5000/api/uploader/documents?mandantId=3&orderNo=202039155&deviceId=78c5b0bc3b2de700
    // https://213.144.11.162:5000/api/uploader/documents?mandantId=3&orderNo=202038112&deviceId=78c5b0bc3b2de700
   //     https://213.144.11.162:5000/api/uploader/documents?mandantId=3&orderNo=9695&deviceId=78c5b0bc3b2de700
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