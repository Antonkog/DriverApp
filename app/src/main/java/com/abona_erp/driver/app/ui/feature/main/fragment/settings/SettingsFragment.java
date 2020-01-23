package com.abona_erp.driver.app.ui.feature.main.fragment.settings;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.BackEvent;
import com.abona_erp.driver.app.ui.feature.main.MainActivity;
import com.abona_erp.driver.app.ui.feature.main.MainViewModel;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.dynamiclanguage.DynamicLanguageContextWrapper;
import com.abona_erp.driver.core.base.ContextUtils;
import com.abona_erp.driver.core.base.ThreadUtils;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsFragment extends Fragment {
  
  private static final String TAG = SettingsFragment.class.getSimpleName();
  
  private LinearLayout mLlLanguage;
  private Button mBtnSave;
  private TextInputEditText mTeServerPort;
  private TextInputEditText mTeIpAddress;
  
  private AppCompatImageButton mBtnBack;
  
  private MainViewModel mainViewModel;
  
  private DriverDatabase mDb = DriverDatabase.getDatabase();
  
  public SettingsFragment() {
    // Required empty public constructor.
  }
  
  public static SettingsFragment newInstance() {
    return new SettingsFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.activity_settings_layout, container, false);
    initComponents(root);
    return root;
  }
  
  @Override
  public void onResume() {
    super.onResume();
  }
  
  private void initComponents(@NonNull View root) {
    mBtnBack = (AppCompatImageButton)root.findViewById(R.id.btn_settings_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        App.eventBus.post(new BackEvent());
      }
    });
    
    mLlLanguage = (LinearLayout)root.findViewById(R.id.ll_settings_language);
    mLlLanguage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        
        String[] listItems = {
          "English (US)",
          "Deutsch",
          "Русский",
          "Polski"
        };
  
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Language");
        
        builder.setItems(listItems, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            
            switch (i) {
              case 0: // ENGLISCH
                TextSecurePreferences.setLanguage(getContext(), "en_US");
                DynamicLanguageContextWrapper.updateContext(getContext(),
                  TextSecurePreferences.getLanguage(getContext()));
                getActivity().recreate();
              break;
              
              case 1: // DEUTSCH
                TextSecurePreferences.setLanguage(getContext(), "de_DE");
                DynamicLanguageContextWrapper.updateContext(getContext(),
                  TextSecurePreferences.getLanguage(getContext()));
                getActivity().recreate();
                break;
                
              case 2: // RUSSISCH
                TextSecurePreferences.setLanguage(getContext(), "ru_RU");
                DynamicLanguageContextWrapper.updateContext(getContext(),
                  TextSecurePreferences.getLanguage(getContext()));
                getActivity().recreate();
                break;
                
              case 3: // POLNISCH
                TextSecurePreferences.setLanguage(getContext(), "pl_PL");
                DynamicLanguageContextWrapper.updateContext(getContext(),
                  TextSecurePreferences.getLanguage(getContext()));
                getActivity().recreate();
                break;
            }
          }
        });
        
        AlertDialog dialog = builder.create();
        dialog.show();
      }
    });
    
    mTeIpAddress = (TextInputEditText)root.findViewById(R.id.te_ip_address);
    mTeIpAddress.setText(/*"https://" +*/ TextSecurePreferences.getServerIpAddress());
    Selection.setSelection(new SpannableString("https://")/*mTeIpAddress.getText()*/, /*mTeIpAddress.getText().length()*/8);
    mTeIpAddress.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    
      }
  
      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    
      }
  
      @Override
      public void afterTextChanged(Editable editable) {
        if (!editable.toString().startsWith("https://")) {
          mTeIpAddress.setText("https://");
          Selection.setSelection(mTeIpAddress.getText(), mTeIpAddress.getText().length());
        }
      }
    });
    
    mTeServerPort = (TextInputEditText)root.findViewById(R.id.te_server_port);
    mTeServerPort.setText(String.valueOf(TextSecurePreferences.getServerPort()));
    
    mBtnSave = (Button)root.findViewById(R.id.btn_settings_save);
    mBtnSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        
        // DEVICE RESET BEGIN
        AsyncTask.execute(new Runnable() {
          @Override
          public void run() {
            mDb.lastActivityDAO().deleteAll();
            mDb.notifyDao().deleteAll();
            mDb.offlineConfirmationDAO().deleteAll();
  
            TextSecurePreferences.setFcmTokenUpdate(getContext().getApplicationContext(), true);
            TextSecurePreferences.setDeviceFirstTimeRun(getContext().getApplicationContext(), false);
          }
        });
        // DEVICE RESET END
        
        String ip_address = mTeIpAddress.getText().toString();
        TextSecurePreferences.setServerIpAddress(ip_address);
        
        int server_port = Integer.valueOf(mTeServerPort.getText().toString());
        TextSecurePreferences.setServerPort(server_port);
        
        TextSecurePreferences.setDeviceFirstTimeRun(ContextUtils.getApplicationContext(), false);
        TextSecurePreferences.setDeviceRegistrated(ContextUtils.getApplicationContext(), false);
        ThreadUtils.postOnUiThreadDelayed(new Runnable() {
          @Override
          public void run() {
            
            Intent restartIntent = getContext().getPackageManager().getLaunchIntentForPackage(getContext().getPackageName());
            PendingIntent intent = PendingIntent.getActivity(getContext(), 0 , restartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager manager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
            manager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, intent);
            getActivity().finish();
            Runtime.getRuntime().exit(0);
          }
        }, 1000);
        
  /*
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        if (getContext() instanceof Activity) {
          ((Activity)getContext()).finish();
        }
        Runtime.getRuntime().exit(0);
       
   */
      }
    });
  }
}
