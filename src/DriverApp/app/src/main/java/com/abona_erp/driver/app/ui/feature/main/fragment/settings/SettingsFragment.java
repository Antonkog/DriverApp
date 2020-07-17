package com.abona_erp.driver.app.ui.feature.main.fragment.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.event.ProtocolEvent;
import com.abona_erp.driver.app.ui.feature.main.MainActivity;
import com.abona_erp.driver.app.ui.feature.main.MainViewModel;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.dynamiclanguage.DynamicLanguageContextWrapper;
import com.abona_erp.driver.core.base.ContextUtils;
import com.abona_erp.driver.core.base.ThreadUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.kongzue.dialog.interfaces.OnInputDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.util.InputInfo;
import com.kongzue.dialog.util.TextInfo;
import com.kongzue.dialog.v3.InputDialog;

import java.util.Date;
import java.util.List;

public class SettingsFragment extends Fragment {
  
  private static final String TAG = SettingsFragment.class.getSimpleName();
  
  private LinearLayout mLlLanguage;
  private Button mBtnSave;
  private AppCompatButton mBtnProtocol;
  //private TextInputEditText mTeServerPort;
  //private TextInputEditText mTeIpAddress;
  
  private AsapTextView mDeviceId;
  private AsapTextView mDeviceModel;
  private AsapTextView mDeviceManufacturer;
  private AsapTextView mDeviceSerial;
  private AsapTextView mDeviceCreated;
  private AsapTextView mDeviceUpdated;
  
  private AppCompatImageButton mBtnBack;
  
  private MainViewModel mainViewModel;
  
  private DriverDatabase mDb = DriverDatabase.getDatabase();
  private DeviceProfileDAO mDeviceDao = mDb.deviceProfileDAO();
  
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
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
      }
    });
    
    mDeviceId = (AsapTextView)root.findViewById(R.id.tv_device_id);
    mDeviceModel = (AsapTextView)root.findViewById(R.id.tv_device_model);
    mDeviceManufacturer = (AsapTextView)root.findViewById(R.id.tv_device_manufacturer);
    mDeviceSerial = (AsapTextView)root.findViewById(R.id.tv_device_serial);
    mDeviceCreated = (AsapTextView)root.findViewById(R.id.tv_device_created);
    mDeviceUpdated = (AsapTextView)root.findViewById(R.id.tv_device_updated);
    
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        List<DeviceProfile> deviceProfiles = mDeviceDao.getDeviceProfiles();
        if (deviceProfiles.size() > 0) {
          DeviceProfile devProf = deviceProfiles.get(0);
    
          if (devProf.getDeviceId() != null) {
            AsyncTask.execute(new Runnable() {
              @Override
              public void run() {
                mDeviceId.setText(devProf.getDeviceId());
              }
            });
          } else {
            mDeviceId.setText("<Error By Device>");
          }
    
          if (devProf.getDeviceModel() != null) {
            mDeviceModel.setText(devProf.getDeviceModel());
          } else {
            mDeviceModel.setText("<Unknown Device Model>");
          }
          
          if (devProf.getDeviceManufacturer() != null) {
            mDeviceManufacturer.setText(devProf.getDeviceManufacturer());
          } else {
            mDeviceManufacturer.setText("<Unknown Manufacturer>");
          }
          
          if (devProf.getDeviceSerial() != null) {
            mDeviceSerial.setText(devProf.getDeviceSerial());
          } else {
            mDeviceSerial.setText("<Unknown Serial>");
          }
          
          if (devProf.getCreatedAt() != null) {
            mDeviceCreated.setText(devProf.getCreatedAt());
          }
          
          if (devProf.getModifiedAt() != null) {
            mDeviceUpdated.setText(devProf.getModifiedAt());
          }
        }
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
    
    //mTeIpAddress = (TextInputEditText)root.findViewById(R.id.te_ip_address);
    //mTeIpAddress.setText(/*"https://" +*/ TextSecurePreferences.getServerIpAddress());
    //Selection.setSelection(new SpannableString("https://")/*mTeIpAddress.getText()*/, /*mTeIpAddress.getText().length()*/8);
    //mTeIpAddress.addTextChangedListener(new TextWatcher() {
    //  @Override
    //  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    //
    //  }
    //
    //  @Override
    //  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    //
    //  }
    //
    //  @Override
    //  public void afterTextChanged(Editable editable) {
    //    if (!editable.toString().startsWith("https://")) {
    //      mTeIpAddress.setText("https://");
    //      Selection.setSelection(mTeIpAddress.getText(), mTeIpAddress.getText().length());
    //    }
    //  }
    //});
    
    //mTeServerPort = (TextInputEditText)root.findViewById(R.id.te_server_port);
    //mTeServerPort.setText(String.valueOf(TextSecurePreferences.getServerPort()));
    
    mBtnSave = (Button)root.findViewById(R.id.btn_settings_save);
    mBtnSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        
        InputDialog.build((AppCompatActivity)getContext())
          .setStyle(DialogSettings.STYLE.STYLE_IOS)
          .setTheme(DialogSettings.THEME.LIGHT)
          .setTitle(getContext().getResources().getString(R.string.action_security_code))
          .setMessage(getContext().getResources().getString(R.string.action_security_code_message))
          .setInputInfo(new InputInfo()
            .setMAX_LENGTH(4)
            .setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
            .setTextInfo(new TextInfo()
              .setFontColor(Color.RED))
          )
          .setOkButton(getContext().getResources().getString(R.string.action_ok))
          .setOnOkButtonClickListener(new OnInputDialogButtonClickListener() {
            @Override
            public boolean onClick(BaseDialog baseDialog, View v, String inputStr) {
              if (inputStr.equals("0000")) {
                // DEVICE RESET BEGIN
                AsyncTask.execute(new Runnable() {
                  @Override
                  public void run() {
                    mDb.lastActivityDAO().deleteAll();
                    mDb.notifyDao().deleteAll();
                    mDb.offlineConfirmationDAO().deleteAll();
      
                    TextSecurePreferences.setFcmTokenUpdate(getContext().getApplicationContext(), true);
                    TextSecurePreferences.setDeviceFirstTimeRun(false);
                    TextSecurePreferences.setLoginPageEnable(true);
                  }
                });
                // DEVICE RESET END
  
                //String ip_address = mTeIpAddress.getText().toString();
                //TextSecurePreferences.setServerIpAddress(ip_address);
  
                //int server_port = Integer.valueOf(mTeServerPort.getText().toString());
                //TextSecurePreferences.setServerPort(server_port);
  
                TextSecurePreferences.setDeviceFirstTimeRun(false);
                TextSecurePreferences.setDeviceRegistrated(false);
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
              }
              return false;
            }
          })
          .setCancelButton(getContext().getResources().getString(R.string.action_cancel))
          .setOnCancelButtonClickListener(new OnInputDialogButtonClickListener() {
            @Override
            public boolean onClick(BaseDialog baseDialog, View v, String inputStr) {
              return false;
            }
          })
          .show();
      }
    });
    
    mBtnProtocol = (AppCompatButton)root.findViewById(R.id.btn_protocol);
    mBtnProtocol.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        App.eventBus.post(new ProtocolEvent());
      }
    });
  }
}
