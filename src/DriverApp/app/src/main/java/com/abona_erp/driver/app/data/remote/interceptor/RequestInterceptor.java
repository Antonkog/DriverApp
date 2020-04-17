package com.abona_erp.driver.app.data.remote.interceptor;

import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.core.base.ContextUtils;

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
    builder.addHeader("Authorization", "Bearer " +
      TextSecurePreferences.getAccessToken(ContextUtils
        .getApplicationContext()));
    
    return chain.proceed(builder.build());
  }
}
