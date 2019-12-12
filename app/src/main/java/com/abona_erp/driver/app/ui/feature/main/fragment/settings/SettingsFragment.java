package com.abona_erp.driver.app.ui.feature.main.fragment.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.event.BackEvent;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.dynamiclanguage.DynamicLanguageContextWrapper;

import java.util.Locale;

public class SettingsFragment extends Fragment {
  
  private static final String TAG = SettingsFragment.class.getSimpleName();
  
  private LinearLayout mLlLanguage;
  
  private AppCompatImageButton mBtnBack;
  
  public SettingsFragment() {
    // Required empty public constructor.
  }
  
  public static SettingsFragment newInstance() {
    return new SettingsFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
  }
}
