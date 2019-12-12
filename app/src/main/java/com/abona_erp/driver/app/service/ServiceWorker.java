package com.abona_erp.driver.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.dao.LastActivityDAO;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.model.ActivityItem;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ConfirmationItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.DataType;
import com.abona_erp.driver.app.data.model.DeviceProfileItem;
import com.abona_erp.driver.app.data.model.Header;
import com.abona_erp.driver.app.data.model.LastActivityDetails;
import com.abona_erp.driver.app.data.model.ResultOfAction;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.manager.ApiManager;
import com.abona_erp.driver.app.receiver.NetworkStateReceiver;
import com.abona_erp.driver.app.ui.event.DeviceRegistratedEvent;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DateConverter;
import com.abona_erp.driver.app.util.TextSecurePreferences;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceWorker extends Service
  implements NetworkStateReceiver.NetworkStateReceiverListener {
  
  private static final String TAG = ServiceWorker.class.getSimpleName();
  
  Context mContext;
  private static boolean fConnected = true;
  
  private NetworkStateReceiver mNetworkStateReceiver;
 
  public ServiceWorker() {
    //super();
    //Log.d(TAG, "ServiceWorker() 1 called!");
    //mContext = ContextUtils.getApplicationContext();
  }
  
  public ServiceWorker(Context ctx) {
    super();
    Log.d(TAG, "ServiceWorker() 2 called!");
    mContext = ctx;
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
  }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
  
    Log.i(TAG, "onStartCommand() called!");
    
    try {
      unregisterNetworkBroadcastReceiver(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
    startTimer();
    startNetworkBroadcastReceiver(this);
    
    return START_STICKY;
  }
  
  @Override
  public void onDestroy() {
  /*
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction("com.abona_erp.driver.RestartWorkerService");
    intentFilter.setPriority(100);
    sendBroadcast(new Intent());
   */
    
    //super.onDestroy();
    Log.i(TAG, "onDestroy() called!");
  
    Intent broadcastIntent = new Intent("restart0");
    sendBroadcast(broadcastIntent);
    stopTimerTask();
  }

  private Timer mTimer;
  private TimerTask mTimerTask;
  
  public void startTimer() {
    // set a new Timer:
    mTimer = new Timer();
    
    // initialize the TimerTask's job:
    initializeTimerTask();
    
    // schedule the timer, to wake up every 40 second.
    mTimer.schedule(mTimerTask, 1000, 20000);
  }
  
  public void initializeTimerTask() {
    mTimerTask = new TimerTask() {
      @Override
      public void run() {
        //Log.i(TAG, "******* BACKGROUND SERVICE WORKER RUNNING *******");
        
        
      }
    };
  }
  
  public void stopTimerTask() {
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
    }
  }
  
  @Override
  public void onTaskRemoved(Intent rootIntent) {
    Intent intent = new Intent(getApplicationContext(), this.getClass());
    intent.setPackage(getPackageName());
  
    PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),
      1, intent, PendingIntent.FLAG_ONE_SHOT);
    AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    alarmService.set(
      AlarmManager.ELAPSED_REALTIME,
      SystemClock.elapsedRealtime() + 1000,
      pendingIntent
    );
    
    super.onTaskRemoved(rootIntent);
  }
  
  @Override
  public void onLowMemory() {
    super.onLowMemory();
    Log.i(TAG, "onLowMemory() called!");
  }
  
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
  
  private void startNetworkBroadcastReceiver(Context ctx) {
    mNetworkStateReceiver = new NetworkStateReceiver();
    mNetworkStateReceiver.addListener((NetworkStateReceiver.NetworkStateReceiverListener)ctx);
    registerNetworkBroadcastReceiver(ctx);
  }
  
  /**
   * Register the NetworkStateReceiver with your activity.
   * @param ctx
   */
  public void registerNetworkBroadcastReceiver(Context ctx) {
    ctx.registerReceiver(mNetworkStateReceiver,
      new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
  }
  
  /**
   * Unregister the NetworkStateReceiver with your Service.
   * @param ctx
   */
  public void unregisterNetworkBroadcastReceiver(Context ctx) {
    ctx.unregisterReceiver(mNetworkStateReceiver);
  }
  
  @Override
  public void networkAvailable() {
    Log.i(TAG, "networkAvailable()");
    fConnected = true;
  }
  
  @Override
  public void networkUnavailable() {
    Log.i(TAG, "networkUnavailable()");
    fConnected = false;
  }
  
  private void handleConfirmation(CommItem commItem) {
    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create(mediaType, App.getGson().toJson(commItem));
    Request request = new Request.Builder()
      .url("https://213.144.11.162:5000/api/confirmation/confirm")
      .post(body)
      .addHeader("Content-Type", "application/json")
      .addHeader("Accept-Encoding", "gzip, deflate")
      .addHeader("Connection", "keep-alive")
      .addHeader("cache-control", "no-cache")
      .addHeader("Authorization", "bearer " + TextSecurePreferences.getAccessToken(getApplicationContext()))
      .build();
  
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        getOkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
        
          @Override
          public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
              try {
                String jsonData = response.body().string().toString();
                JSONObject jobject = new JSONObject(jsonData);
                
                if (jobject.getBoolean("isSuccess")) {
                  AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
    
                    }
                  });
                } else {
                
                }
                
              } catch (NullPointerException e) {
                e.printStackTrace();
              } catch (JSONException e) {
                e.printStackTrace();
              }
            } else {
              
              switch (response.code()) {
                case 401:
                  handleAccessToken();
                  break;
              }
            }
          }
        
          @Override
          public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
          
          }
        });
      }
    });
  }
  
  private void handleAccessToken() {
    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    RequestBody body = RequestBody.create(mediaType, "grant_type=password&username=manyvehicles%40abona-erp.com&password=1234qwerQWER%2C.-");
  
    Request request = new Request.Builder()
      .url("https://213.144.11.162:5000/authentication")
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
        
        getOkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
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
                  TextSecurePreferences.setAccessToken(getApplicationContext(), access_token);
                }
              } catch (NullPointerException e) {
                e.printStackTrace();
              } catch (JSONException e) {
                e.printStackTrace();
              }
            }
          }
        });
  /*
        try (okhttp3.Response response = getOkHttpClient().newCall(request).execute()) {
          ResponseBody body = response.body();
          Log.i(TAG, body.toString());
        } catch (IOException e) {
          e.printStackTrace();
        }*/
      }
    });
  }
  
  OkHttpClient okHttpClient = null;
  private OkHttpClient getOkHttpClient() {
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    
    if (okHttpClient == null) {
      synchronized (ServiceWorker.class) {
        if (okHttpClient == null) {
          okHttpClient = new OkHttpClient.Builder()
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
    return okHttpClient;
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

  private void updateLastActivity(LastActivityDAO dao, LastActivity lastActivity, int statusType, String description, int confirmationStatus) {
    lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
    
    ArrayList<String> _list = lastActivity.getDetailList();
    LastActivityDetails _detail = new LastActivityDetails();
    if (description != null && !TextUtils.isEmpty(description)) {
      _detail.setDescription(description);
    }
    _list.add(App.getGson().toJson(_detail));
    lastActivity.setDetailList(_list);
    
    if (confirmationStatus != -1) {
      lastActivity.setConfirmStatus(confirmationStatus);
    }
    if (statusType != -1) {
      lastActivity.setStatusType(statusType);
    }
    
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        if (dao != null) {
          dao.update(lastActivity);
        }
      }
    });
  }
}
