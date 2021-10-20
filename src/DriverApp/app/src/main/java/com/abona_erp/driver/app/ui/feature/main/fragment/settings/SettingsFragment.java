package com.abona_erp.driver.app.ui.feature.main.fragment.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

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
import com.abona_erp.driver.app.util.CustomDialogFragment;
import com.abona_erp.driver.app.util.DeviceUtils;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.core.base.ContextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsFragment extends Fragment {
  
  private static final String TAG = SettingsFragment.class.getSimpleName();
  
  private LinearLayout mLlLanguage;
  private Button mBtnSave;
  private AppCompatButton mBtnProtocol;
  
  private AsapTextView versionName;
  private AsapTextView restApiVersion;
  private AsapTextView mDeviceId;
  private AsapTextView mDeviceManufacturer;
  private AsapTextView mDeviceCreated;
  private SeekBar seekBar;
  private AppCompatImageButton mBtnBack;

  private MainViewModel mainViewModel;

  private DeviceProfileDAO mDeviceDao = DriverDatabase.getDatabase().deviceProfileDAO();
  
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

  private void initComponents(@NonNull View root) {
    
    mBtnBack = (AppCompatImageButton)root.findViewById(R.id.btn_settings_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_BACK), null));
      }
    });

    AsapTextView  langText =  root.findViewById(R.id.txt_language);
    switch (TextSecurePreferences.getLanguage(getContext())){
      case Constants.LANG_TO_SERVER_ENGLISH: langText.setText(getResources().getString(R.string.preference_language_eng)); break;
      case Constants.LANG_TO_SERVER_GERMAN: langText.setText(getResources().getString(R.string.preference_language_ger)); break;
      case Constants.LANG_TO_SERVER_RUSSIAN: langText.setText(getResources().getString(R.string.preference_language_rus)); break;
      case Constants.LANG_TO_SERVER_POLISH: langText.setText(getResources().getString(R.string.preference_language_pol)); break;
    }

    mDeviceId =  root.findViewById(R.id.tv_device_id);
    versionName =  root.findViewById(R.id.app_version_name);
    restApiVersion =  root.findViewById(R.id.rest_api_version);
    mDeviceManufacturer =  root.findViewById(R.id.tv_device_manufacturer);
    mDeviceCreated = root.findViewById(R.id.tv_device_created);
    
    try {
      versionName.setText(BuildConfig.VERSION_NAME);
      restApiVersion.setText(TextSecurePreferences.getRestApiVersion());
      List<DeviceProfile> deviceProfiles = mDeviceDao.getDeviceProfiles();
      if (deviceProfiles.size() > 0) {
        DeviceProfile devProf = deviceProfiles.get(0);

        String deviceID= DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext());
        if (deviceID!=null && !deviceID.trim().isEmpty()) {
          mDeviceId.setText(DeviceUtils.getUniqueIMEI(ContextUtils.getApplicationContext()));
        } else {
          mDeviceId.setText("deviceIDNotSet");
        }
    
        if (devProf.getDeviceManufacturer() != null && devProf.getDeviceModel() != null) {
          mDeviceManufacturer.setText(devProf.getDeviceModel() + " - " + devProf.getDeviceManufacturer());
        } else {
          mDeviceManufacturer.setText("<Unknown Manufacturer>");
        }
    
        if (devProf.getCreatedAt() != null && !TextUtils.isEmpty(devProf.getCreatedAt())) {
          SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
          long updateTime = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),
            0).lastUpdateTime;
          mDeviceCreated.setText(sdf.format(new Date(updateTime)));
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
        CustomDialogFragment fragment = CustomDialogFragment.newInstance(CustomDialogFragment.DialogType.LANGUAGE);
        fragment.show(getActivity().getSupportFragmentManager(), CustomDialogFragment.DialogType.LANGUAGE.name());
      }
    });

    mBtnSave = (Button)root.findViewById(R.id.btn_device_reset);
    mBtnSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        CustomDialogFragment fragment = CustomDialogFragment.newInstance(CustomDialogFragment.DialogType.DEVICE_RESET);
        fragment.show(getActivity().getSupportFragmentManager(),  CustomDialogFragment.DialogType.DEVICE_RESET.name());
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
