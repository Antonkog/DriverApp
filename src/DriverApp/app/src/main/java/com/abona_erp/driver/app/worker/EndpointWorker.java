package com.abona_erp.driver.app.worker;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class EndpointWorker extends Worker {
  
  private static final String TAG = EndpointWorker.class.getSimpleName();
  
  public EndpointWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParameters) {
    super(appContext, workerParameters);
  }
  
  @NonNull
  @Override
  public Result doWork() {
    
    Context applicationContext = getApplicationContext();
    
    Log.i(TAG, "Checking endpoint... ---------------------------------------------------");
  
    String url = "http://endpoint.abona-erp.com/Api/AbonaClients/GetServerURLByClientId/"
      +
      TextSecurePreferences.getClientID()
      + "/2";
  
    RequestQueue requestQueue = Volley.newRequestQueue(applicationContext);
    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
  
        try {
          boolean active = response.getBoolean("IsActive");
          if (active) {
  
            String webService = response.getString("WebService");
            if (TextUtils.isEmpty(webService) || webService.equals("null")) {
              return;
            }
  
            TextSecurePreferences.setEndpoint(webService);
          } else {
          
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
      }
    });
    
    requestQueue.add(request);
    
    return Result.success();
  }
  
  @Override
  public void onStopped() {
    super.onStopped();
    Log.i(TAG, "OnStopped called for this worker.");
  }
}
