package com.abona_erp.driver.app.data.remote;

import com.abona_erp.driver.app.data.Constant;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {
  
  @Override
  public Response intercept(Chain chain) throws IOException {
    Request.Builder builder = chain.request().newBuilder();
    builder.addHeader("Content-Type", "application/json");
    builder.addHeader("Connection", "keep-alive");
    builder.addHeader("cache-control", "no-cache");
    builder.addHeader("Host", Constant.defaultApiUrl);
    return chain.proceed(builder.build());
  }
}