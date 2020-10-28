package com.abona_erp.driver.app.data.remote.interceptor;

import com.abona_erp.driver.app.util.TextSecurePreferences;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

public final class HostSelectionInterceptor implements Interceptor {
  
  @Override public okhttp3.Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    String reqUrl = request.url().host();
    
//    HttpUrl newUrl = request.url().newBuilder().host(TextSecurePreferences.getEndpoint()).build();
    //request = request.newBuilder().url(newUrl).build();
    return chain.proceed(request);
  }
}
