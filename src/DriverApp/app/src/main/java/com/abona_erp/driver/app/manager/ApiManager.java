package com.abona_erp.driver.app.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.BuildConfig;
import com.abona_erp.driver.app.data.remote.ActivityService;
import com.abona_erp.driver.app.data.remote.ConfirmService;
import com.abona_erp.driver.app.data.remote.DocumentService;
import com.abona_erp.driver.app.data.remote.FileDownloadService;
import com.abona_erp.driver.app.data.remote.FCMService;
import com.abona_erp.driver.app.data.remote.FileUploadService;
import com.abona_erp.driver.app.data.remote.RemoteConstants;
import com.abona_erp.driver.app.data.remote.TokenService;
import com.abona_erp.driver.app.data.remote.interceptor.AccessTokenInterceptor;
import com.abona_erp.driver.app.data.remote.interceptor.NetworkConnectionInterceptor;
import com.abona_erp.driver.app.data.remote.interceptor.RequestInterceptor;
import com.abona_erp.driver.app.data.remote.interceptor.UserAgentInterceptor;
import com.abona_erp.driver.app.util.ClientSSLSocketFactory;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.core.base.ContextUtils;

import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager implements Manager {
  private static final String TAG = ApiManager.class.getSimpleName();
  
  private static final int DISK_CACHE_SIZE = 10 * 1024 * 1024;  // 10 MB
  
  private Context mContext;
  private TokenService mTokenService;
  private FCMService mFCMService;
  private ConfirmService mConfirmService;
  private ActivityService mActivityService;
  private FileUploadService mFileUploadService;
  private DocumentService mDocumentService;
  private FileDownloadService mFileDownloadService;
  
  static OkHttpClient.Builder apiClientBuilder;
  static OkHttpClient.Builder authClientBuilder;
  static Request.Builder      authRequestBuilder;
  
  public static OkHttpClient provideAuthClient() {
    return makeAuthClientBuilder().build();
  }
  
  public static OkHttpClient provideApiClient() {
    return makeApiClientBuilder().build();
  }
  
  public static Request provideAuthRequest() {
    return makeAuthRequestBuilder().build();
  }
  
  public FCMService getFCMApi() {
    if (mFCMService == null) {
      mFCMService = provideRetrofit(
        TextSecurePreferences.getEndpoint()
        + "api/device/")
        .create(FCMService.class);
    }
    return mFCMService;
  }
  
  public TokenService getTokenApi() {
    if (mTokenService == null) {
      mTokenService = provideRetrofit(TextSecurePreferences.getEndpoint())
        .create(TokenService.class);
    }
    return mTokenService;
  }
  
  public ConfirmService getConfirmApi() {
    if (mConfirmService == null) {
      mConfirmService = provideRetrofitUtc(
        TextSecurePreferences.getEndpoint()
        + "api/confirmation/")
        .create(ConfirmService.class);
    }
    return mConfirmService;
  }
  
  public ActivityService getActivityApi() {
    if (mActivityService == null) {
      mActivityService = provideRetrofit(
        TextSecurePreferences.getEndpoint()
        + "api/activity/")
        .create(ActivityService.class);
    }
    return mActivityService;
  }
  
  public FileUploadService getFileUploadApi() {
    if (mFileUploadService == null) {
      mFileUploadService = provideRetrofit(
        TextSecurePreferences.getEndpoint()
      + "api/uploader/")
        .create(FileUploadService.class);
    }
    return mFileUploadService;
  }
  
  public DocumentService getDocumentApi() {
    if (mDocumentService == null) {
      mDocumentService = provideRetrofit(
        TextSecurePreferences.getEndpoint()
      + "api/uploader/")
        .create(DocumentService.class);
    }
    return mDocumentService;
  }
  
  public FileDownloadService getFileDownloadApi() {
    if (mFileDownloadService == null) {
      mFileDownloadService = provideRetrofit(
        TextSecurePreferences.getEndpoint()
        + "api/uploader/")
        .create(FileDownloadService.class);
    }
    return mFileDownloadService;
  }
  
  private Retrofit provideRetrofit(String url) {
    
    return new Retrofit.Builder()
      .baseUrl(url)
      .client(provideOkHttpClient())
      .addConverterFactory(GsonConverterFactory.create(App.getGson()))
      .build();
  }
  
  private Retrofit provideRetrofitUtc(String url) {
    
    return new Retrofit.Builder()
      .baseUrl(url)
      .client(provideOkHttpClient())
      .addConverterFactory(GsonConverterFactory.create(App.getGsonUtc()))
      .build();
  }
  
  private OkHttpClient provideOkHttpClient() {
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    httpClient.connectTimeout(30, TimeUnit.SECONDS);
    httpClient.readTimeout(30, TimeUnit.SECONDS);
    httpClient.writeTimeout(30, TimeUnit.SECONDS);
    
    String versionName = BuildConfig.VERSION_NAME;
    try {
      PackageInfo pi = mContext.getApplicationContext()
        .getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
      versionName = pi.versionName;
    } catch (PackageManager.NameNotFoundException ignore) {
    }
    
    // Get Android User Agent:
    //String UA = System.getProperty("http.agent");
    httpClient.addInterceptor(new UserAgentInterceptor("ABONA DriverApp", versionName));
    httpClient.addInterceptor(new RequestInterceptor());
    httpClient.addInterceptor(new NetworkConnectionInterceptor() {
      @Override
      public boolean isInternetAvailable() {
        return true;
      }
  
      @Override
      public void onInternetUnavailable() {
    
      }
  
      @Override
      public void onCacheUnavailable() {
    
      }
    });
    httpClient.addInterceptor(new AccessTokenInterceptor());
  
    if (BuildConfig.DEBUG) {
      HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
      logging.setLevel(HttpLoggingInterceptor.Level.BODY);
      httpClient.addInterceptor(logging);
    }
  
    httpClient.cache(getCache());
    httpClient.sslSocketFactory(ClientSSLSocketFactory.getSocketFactory());
    httpClient.hostnameVerifier(new HostnameVerifier() {
      @Override
      public boolean verify(String s, SSLSession sslSession) {
        return true;
      }
    });
  
    return httpClient.build();
  }
  
  static Request.Builder makeAuthRequestBuilder() {
    if (authRequestBuilder == null) {
      MediaType mediaType = MediaType.parse(RemoteConstants.MEDIA_TYPE_X_WWW_FORM_URLENCODED);
      RequestBody requestBody = RequestBody.create(mediaType, RemoteConstants.ENDPOINT_AUTH);
      
      authRequestBuilder = new Request.Builder()
        .url(TextSecurePreferences.getEndpoint() + "authentication")
        .post(requestBody)
        .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .addHeader("Accept-Encoding", "gzip, deflate")
        .addHeader("Connection", "keep-alive")
        .addHeader("cache-control", "no-cache");
    }
    return authRequestBuilder;
  }
  
  static OkHttpClient.Builder makeApiClientBuilder() {
    if (apiClientBuilder == null) {
      apiClientBuilder = new OkHttpClient.Builder()
        .hostnameVerifier(new HostnameVerifier() {
          @Override
          public boolean verify(String s, SSLSession sslSession) {
            return true;
          }
        })
        .sslSocketFactory(ClientSSLSocketFactory.getSocketFactory())
        .followRedirects(true)
        .followSslRedirects(true)
        .retryOnConnectionFailure(true)
        .cache(null)
        .connectTimeout(RemoteConstants.TIME_OUT_API, TimeUnit.SECONDS)
        .writeTimeout(RemoteConstants.TIME_OUT_API, TimeUnit.SECONDS)
        .readTimeout(RemoteConstants.TIME_OUT_API, TimeUnit.SECONDS);
  
      String versionName = BuildConfig.VERSION_NAME;
      try {
        PackageInfo pi = ContextUtils.getApplicationContext()
          .getPackageManager().getPackageInfo(ContextUtils.getApplicationContext().getPackageName(), 0);
        versionName = pi.versionName;
      } catch (PackageManager.NameNotFoundException ignore) {}
      apiClientBuilder.addInterceptor(new UserAgentInterceptor("ABONA DriverApp", versionName));
      apiClientBuilder.addInterceptor(new RequestInterceptor());
      apiClientBuilder.addInterceptor(new NetworkConnectionInterceptor() {
        @Override
        public boolean isInternetAvailable() {
          return true;
        }
  
        @Override
        public void onInternetUnavailable() {
    
        }
  
        @Override
        public void onCacheUnavailable() {
    
        }
      });
      apiClientBuilder.addInterceptor(new AccessTokenInterceptor());
  
      if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        apiClientBuilder.addInterceptor(logging);
      }
    }
    return apiClientBuilder;
  }
  
  static OkHttpClient.Builder makeAuthClientBuilder() {
    if (authClientBuilder == null) {
      authClientBuilder = new OkHttpClient.Builder()
        .hostnameVerifier(new HostnameVerifier() {
          @Override
          public boolean verify(String s, SSLSession sslSession) {
            return true;
          }
        })
        .sslSocketFactory(ClientSSLSocketFactory.getSocketFactory())
        .followRedirects(true)
        .followSslRedirects(true)
        .retryOnConnectionFailure(true)
        .cache(null)
        .connectTimeout(RemoteConstants.TIME_OUT_API, TimeUnit.SECONDS)
        .writeTimeout(RemoteConstants.TIME_OUT_API, TimeUnit.SECONDS)
        .readTimeout(RemoteConstants.TIME_OUT_API, TimeUnit.SECONDS);
  
      String versionName = BuildConfig.VERSION_NAME;
      try {
        PackageInfo pi = ContextUtils.getApplicationContext()
          .getPackageManager().getPackageInfo(ContextUtils.getApplicationContext().getPackageName(), 0);
        versionName = pi.versionName;
      } catch (PackageManager.NameNotFoundException ignore) {}
      authClientBuilder.addInterceptor(new UserAgentInterceptor("ABONA DriverApp", versionName));
    }
    return authClientBuilder;
  }
  
  private Cache getCache() {
    File cacheDir = new File(mContext.getCacheDir(), "cache");
    Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
    return cache;
  }
  
  @Override
  public void init(Context context) {
    mContext = context;
  }
  
  @Override
  public void clear() {
  
  }
}
