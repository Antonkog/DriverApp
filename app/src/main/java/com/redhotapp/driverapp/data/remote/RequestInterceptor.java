package com.redhotapp.driverapp.data.remote;

import com.google.android.material.internal.ContextUtils;
import com.redhotapp.driverapp.App;
import com.redhotapp.driverapp.data.local.Preferences;

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
     "" //Preferences.Companion.getAccessToken( )
    );
    
    return chain.proceed(builder.build());
  }
}
