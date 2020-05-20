package com.abona_erp.driver.app.ui.feature.main;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.widget.AsapTextView;

import java.util.Calendar;
import java.util.Date;

public class DueInCounterRunnable implements Runnable {
  
  Handler handler;
  Context context;
  
  public Date endDate;
  
  public AsapTextView tv_DueIn;
  
  public AppCompatImageView iv_Warning;
  public LinearLayout ll_Background;
  
  public DueInCounterRunnable(
    Handler handler,
    Context context,
    AsapTextView tv_DueIn,
    AppCompatImageView iv_Warning,
    LinearLayout ll_Background,
    Date endDate
  ) {
    this.handler = handler;
    this.context = context;
    this.tv_DueIn = tv_DueIn;
    this.iv_Warning = iv_Warning;
    this.ll_Background = ll_Background;
    this.endDate = endDate;
  }
  
  @Override
  public void run() {
    
    // Get date and time information in milliseconds:
    long now = System.currentTimeMillis();
    
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(endDate);
      
    long diff = (calendar.getTimeInMillis() - now) / 1000 / 60;
    //tv_Minutes.setText(String.format("%02d", Math.abs(diff % 60)));
      
    long hours = diff / 60;
    //tv_Hours.setText(String.format("%02d", Math.abs(hours % 24)));
      
    long days = hours / 24;
    String d = diff < 0 ? "- " : "";
    d += String.valueOf(Math.abs(days));
    if (tv_DueIn != null) {
      tv_DueIn.setText(d + "d " + String.format("%02d", Math.abs(hours % 24)) + "h " + String.format("%02d", Math.abs(diff % 60)) + "min");
    }
    
    if (diff < 0) {
      if (iv_Warning != null) {
        iv_Warning.setVisibility(View.VISIBLE);
      }
      if (ll_Background != null) {
        ll_Background.setBackground(context.getResources().getDrawable(R.drawable.warning_header_bg));
      }
    } else {
      if (iv_Warning != null) {
        iv_Warning.setVisibility(View.GONE);
      }
      if (ll_Background != null) {
        ll_Background.setBackground(context.getResources().getDrawable(R.drawable.header_bg));
      }
    }
    
    handler.postDelayed(this, 1000 * 60);
  }
}
