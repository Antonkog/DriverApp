package com.abona_erp.driver.app.ui.base;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import com.abona_erp.driver.app.R;

public abstract class BaseActivity extends AppCompatActivity {
  
  public ProgressBar mProgressBar;
  
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
}
