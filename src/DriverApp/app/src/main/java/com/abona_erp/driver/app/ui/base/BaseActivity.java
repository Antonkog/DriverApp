package com.abona_erp.driver.app.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.app.util.dynamiclanguage.DynamicLanguageActivityHelper;
import com.abona_erp.driver.app.util.dynamiclanguage.DynamicLanguageContextWrapper;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
  
  public ProgressBar mProgressBar;
  
  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(DynamicLanguageContextWrapper
      .updateContext(newBase, TextSecurePreferences.getLanguage(newBase)));
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public void setContentView(int layoutResID) {
  
    ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
    FrameLayout frameLayout = constraintLayout.findViewById(R.id.activity_content);
    mProgressBar = constraintLayout.findViewById(R.id.progress_bar);
    
    getLayoutInflater().inflate(layoutResID, frameLayout, true);
    super.setContentView(constraintLayout);
  }
  
  public void showProgressBar(boolean visibility) {
    mProgressBar.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    DynamicLanguageActivityHelper.recreateIfNotInCorrectLanguage(this,
      TextSecurePreferences.getLanguage(this));
  
    Log.d(BaseActivity.class.getSimpleName(), Locale.getDefault().toString());
  }
}
