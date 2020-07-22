package com.redhotapp.driverapp.data.remote;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {
  
  @Override
  public Response intercept(Chain chain) throws IOException {
    Request.Builder builder = chain.request().newBuilder();
    builder.addHeader("Content-Type", "application/json");
    builder.addHeader("Accept", "application/json");
    builder.addHeader("Connection", "keep-alive");
    builder.addHeader("cache-control", "no-cache");
//    builder.addHeader("Authorization", "Bearer " +
//     "" //Preferences.Companion.getAccessToken( )
//    );
    return chain.proceed(builder.build());
  }
}
