package com.abona_erp.driver.app.ui.feature.main.fragment.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.BuildConfig;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.event.ProtocolEvent;
import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.app.ui.feature.main.MainViewModel;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.dynamiclanguage.DynamicLanguageContextWrapper;
import com.abona_erp.driver.app.worker.DeviceProfileWorker;
import com.abona_erp.driver.core.base.ThreadUtils;
import com.kongzue.dialog.interfaces.OnInputDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.util.InputInfo;
import com.kongzue.dialog.util.TextInfo;
import com.kongzue.dialog.v3.InputDialog;

import java.util.List;

public class SettingsFragment extends Fragment {
  
  private static final String TAG = SettingsFragment.class.getSimpleName();
  
  private LinearLayout mLlLanguage;
  private LinearLayout notificationContainer;
  private Button mBtnSave;
  private AppCompatButton mBtnProtocol;
  //private TextInputEditText mTeServerPort;
  //private TextInputEditText mTeIpAddress;
  
  private AsapTextView versionName;
  private AsapTextView mDeviceId;
  private AsapTextView mDeviceModel;
  private AsapTextView mDeviceManufacturer;
  private AsapTextView mDeviceSerial;
  private AsapTextView mDeviceCreated;
  private AsapTextView mDeviceUpdated;
  private SeekBar seekBar;
  private AppCompatImageButton mBtnBack;

  private MainViewModel mainViewModel;
  
  private DriverDatabase mDb = DriverDatabase.getDatabase();
  private DeviceProfileDAO mDeviceDao = mDb.deviceProfileDAO();
  
  public SettingsFragment() {
    // Required empty public constructor.
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

  /**
   * this method should start DeviceProfileWorker
   * that brings device profile to Abona server
   * now not used as all work in BackgroundServiceWorker.java
   * that is wrong.
   */
  private void startUpdateDeviceWork() {
    OneTimeWorkRequest taskAlarmRequest =
            new OneTimeWorkRequest.Builder(DeviceProfileWorker.class)
                    .addTag(Constants.WORK_TAG_DEVICE_UPDATE)
                    .build();
    WorkManager.getInstance(getContext()).enqueue(taskAlarmRequest);
  }

  private void updateLanguage() {
    DynamicLanguageContextWrapper.updateContext(getContext(),
            TextSecurePreferences.getLanguage(getContext()));
    getActivity().recreate();
    updatePreferenceFlags();
  }

  public static void updatePreferenceFlags() {
    TextSecurePreferences.setUpdateLangCode(true);
    TextSecurePreferences.setUpdateAllTasks(true);
    TextSecurePreferences.setUpdateDelayReason(true);
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
    versionName = (AsapTextView)root.findViewById(R.id.app_version_name);
    mDeviceModel = (AsapTextView)root.findViewById(R.id.tv_device_model);
    mDeviceManufacturer = (AsapTextView)root.findViewById(R.id.tv_device_manufacturer);
    mDeviceSerial = (AsapTextView)root.findViewById(R.id.tv_device_serial);
    mDeviceCreated = (AsapTextView)root.findViewById(R.id.tv_device_created);
    mDeviceUpdated = (AsapTextView)root.findViewById(R.id.tv_device_updated);
    
    try {
      versionName.setText(BuildConfig.VERSION_NAME);
      List<DeviceProfile> deviceProfiles = mDeviceDao.getDeviceProfiles();
      if (deviceProfiles.size() > 0) {
        DeviceProfile devProf = deviceProfiles.get(0);
    
        if (devProf.getDeviceId() != null) {
          mDeviceId.setText(devProf.getDeviceId());
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
    } catch (Exception e) {
      e.printStackTrace();
    }

    AsapTextView notificationTimeText = root.findViewById(R.id.notification_text_before);
    seekBar = root.findViewById(R.id.seekBar);
    seekBar.setProgress(TextSecurePreferences.getNotificationTime());
    notificationTimeText.setText(TextSecurePreferences.getNotificationTime() + " ");

    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        TextSecurePreferences.setNotificationTime(seekBar.getProgress());
        notificationTimeText.setText(TextSecurePreferences.getNotificationTime() + " ");
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

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AbonaDialog));
        builder.setTitle("Select Language");
        
        builder.setItems(listItems, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            
            switch (i) {
              case 0: // ENGLISCH
                TextSecurePreferences.setLanguage(getContext(), Constants.LANG_TO_SERVER_ENGLISH);
                updateLanguage();
                break;
              
              case 1: // DEUTSCH
                TextSecurePreferences.setLanguage(getContext(), Constants.LANG_TO_SERVER_GERMAN);
                updateLanguage();
                break;
                
              case 2: // RUSSISCH
                TextSecurePreferences.setLanguage(getContext(), Constants.LANG_TO_SERVER_RUSSIAN);
                updateLanguage();
                break;
                
              case 3: // POLNISCH
                TextSecurePreferences.setLanguage(getContext(), Constants.LANG_TO_SERVER_POLISH);
                updateLanguage();
                break;
            }
          }
        });
        
        AlertDialog dialog = builder.create();
        dialog.show();
      }
    });

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
