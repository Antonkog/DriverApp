package com.abona_erp.driver.app.ui.feature.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.base.BaseActivity;
import com.abona_erp.driver.app.ui.feature.main.MainActivity;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.MessageDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends BaseActivity {
  
  private AppCompatButton btnLogin;
  private TextInputEditText etClientID;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
  
    Intent intent = new Intent(this, MainActivity.class);
    
    etClientID = (TextInputEditText)findViewById(R.id.client_id);
    
    btnLogin = (AppCompatButton) findViewById(R.id.btn_login);
    btnLogin.setText(getApplicationContext().getResources()
      .getString(R.string.action_lets_start));
    btnLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        
        String clientID = etClientID.getText().toString().trim();
        if (TextUtils.isEmpty(clientID)) {
          return;
        }
    
        String url = "http://endpoint.abona-erp.com/Api/AbonaClients/GetServerURLByClientId/" + clientID + "/2";
        
        try {
          if (!isConnected()) {
            messageBox_Ok(getApplicationContext().getResources()
              .getString(R.string.action_warning_notice),
              getApplicationContext().getResources()
                .getString(R.string.no_internet));
            return;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest =
          new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              //Log.i(">>>>>>>>>>", "Response: " + response.toString());
              
              try {
                boolean active = response.getBoolean("IsActive");
                if (active) {
                  String webService = response.getString("WebService");
                  if (TextUtils.isEmpty(webService) || webService.equals("null")) {
                    Toast.makeText(getApplicationContext(), "Endpoint is not set!", Toast.LENGTH_SHORT).show();
                    return;
                  }
                  TextSecurePreferences.setEndpoint(webService);
                  TextSecurePreferences.setClientID(etClientID.getText().toString().trim());
                  etClientID.setText("");
                  TextSecurePreferences.setLoginPageEnable(false);
                  startActivity(intent);
                  finish();
                } else {
                  messageBox_Ok(getApplicationContext().getResources()
                      .getString(R.string.action_warning_notice),
                    getApplicationContext().getResources()
                      .getString(R.string.client_id_is_not_active));
                }
              } catch (JSONException e) {
                e.printStackTrace();
              }
            }
          }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              messageBox_Ok(getApplicationContext().getResources()
                .getString(R.string.action_error),
                error.getLocalizedMessage());
            }
          });
        
        // Add JsonObjectRequest to the RequestQueue:
        requestQueue.add(jsonObjectRequest);
      }
    });
  }
  
  @Override
  protected void onStart() {
    super.onStart();
  }
  
  @Override
  protected void onResume() {
    super.onResume();
  }
  
  @Override
  protected void onPause() {
    super.onPause();
  }
  
  @Override
  protected void onStop() {
    super.onStop();
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
  }
  
  private void messageBox_Ok(String title, String message) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        MessageDialog.build((AppCompatActivity) LoginActivity.this)
          .setStyle(DialogSettings.STYLE.STYLE_IOS)
          .setTheme(DialogSettings.THEME.LIGHT)
          .setTitle(title)
          .setMessage(message)
          .setOkButton(getApplicationContext().getResources().getString(R.string.action_ok),
            new OnDialogButtonClickListener() {
              @Override
              public boolean onClick(BaseDialog baseDialog, View v) {
                return false;
              }
            })
          .show();
      }
    });
  }
  
  public boolean isConnected() throws InterruptedException, IOException {
    final String command = "ping -c 1 google.com";
    return Runtime.getRuntime().exec(command).waitFor() == 0;
  }
}
