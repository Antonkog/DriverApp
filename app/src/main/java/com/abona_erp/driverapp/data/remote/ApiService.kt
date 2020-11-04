package com.abona_erp.driverapp.data.remote

import com.abona_erp.driverapp.data.model.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("/authentication")
    suspend fun authentication(
        @Field("grant_type") grantType: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): TokenResponse

    @GET("api/device/GetAllTask")
    suspend fun getAllTasks(@Query("deviceId") deviceId: String?): CommResponseItem

    @GET("api/uploader/documents")
    suspend fun getDocuments(
        @Query("mandantId") mandantId: Int,
        @Query("orderNo") orderNo: Int,
        @Query("deviceId") deviceId: String
    ): List<DocumentResponse>?


    @POST("/api/device/deviceprofile")
    suspend fun setDeviceProfile(@Body commItem: CommItem?): ResultOfAction

    @POST("api/activity/activity")
    suspend fun postActivityChange(@Body commItem: CommItem): ResultOfAction


    @Multipart
    @POST("api/uploader/upload")
    fun uploadDocument(
        @Part("MandantId") mandantId: RequestBody?,
        @Part("OrderNo") orderNo: RequestBody?,
        @Part("TaskId") taskId: RequestBody?,
        @Part("DriverNo") driverNo: RequestBody?,
        @Part("DMSDocumentType") documentType: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Single<UploadResult>

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