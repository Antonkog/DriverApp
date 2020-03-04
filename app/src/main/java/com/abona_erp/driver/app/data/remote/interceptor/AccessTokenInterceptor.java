package com.abona_erp.driver.app.data.remote.interceptor;

import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.core.base.ContextUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class AccessTokenInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {
    String accessToken = getAccessToken();
    Request request = newRequestWithAccessToken(chain.request(), accessToken);
    return chain.proceed(request);
  }
  
  private String getAccessToken() {
    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    RequestBody body = RequestBody.create(mediaType, "grant_type=password&username=manyvehicles%40abona-erp.com&password=1234qwerQWER%2C.-");
  
    Request request = new Request.Builder()
      .url(TextSecurePreferences.getServerIpAddress() + ":" + TextSecurePreferences.getServerPort() + "/authentication")
      .post(body)
      .addHeader("Content-Type", "application/x-www-form-urlencoded")
      .addHeader("Accept-Encoding", "gzip, deflate")
      .addHeader("Content-Length", "84")
      .addHeader("Connection", "keep-alive")
      .addHeader("cache-control", "no-cache")
      .build();
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
      
        getClient().newCall(request).enqueue(new okhttp3.Callback() {
          @Override
          public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
          
          }
        
          @Override
          public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
              try {
                String jsonData = response.body().string().toString();
                JSONObject jobject = new JSONObject(jsonData);
                //Log.i(TAG, "ACCESS_TOKEN " + jobject.getString("access_token"));
                String access_token = jobject.getString("access_token");
                if (!TextUtils.isEmpty(access_token)) {
                  TextSecurePreferences.setAccessToken(ContextUtils.getApplicationContext(), access_token);
                }
              } catch (NullPointerException e) {
                e.printStackTrace();
              } catch (JSONException e) {
                e.printStackTrace();
              }
            }
          }
        });
      }
    });
    
    return TextSecurePreferences.getAccessToken(ContextUtils.getApplicationContext());
  }
  
  @NonNull
  private Request newRequestWithAccessToken(@NonNull Request request, @NonNull String accessToken) {
    return request.newBuilder()
      .header("Authorization", "Bearer " + accessToken)
      .build();
  }
  
  OkHttpClient mClient = null;
  private OkHttpClient getClient() {
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    
    if (mClient == null) {
      synchronized (AccessTokenInterceptor.class) {
        if (mClient == null) {
          mClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .hostnameVerifier(new HostnameVerifier() {
              @Override
              public boolean verify(String s, SSLSession sslSession) {
                return true;
              }
            })
            .sslSocketFactory(getSslSocket())
            .addInterceptor(logging)
            .build();
        }
      }
    }
    return mClient;
  }
  
  private SSLSocketFactory getSslSocket() {
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      X509TrustManager tm = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
    
        }
  
        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
    
        }
  
        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[0];
        }
      };
      sslContext.init(null, new TrustManager[]{tm}, null);
      return sslContext.getSocketFactory();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
