package com.abona_erp.driver.app.data.remote;

import com.abona_erp.driver.app.data.model.ResultOfAction;
import com.abona_erp.driver.app.data.model.TokenResponse;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @FormUrlEncoded
    @POST("/authentication")
    Single<Response<TokenResponse>> authentication(@Field("grant_type") String grantType, @Field("username") String username, @Field("password") String password);

    @POST("/api/device/updateDeviceID")
    Single<ResultOfAction> migrateDeviceId(@Query("oldDeviceId") String oldDeviceId, @Query("newDeviceId") String newDeviceId);
}