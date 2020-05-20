package com.abona_erp.driver.app.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;

public class CustomPaletteDialog extends Dialog implements View.OnClickListener {
  
  public CustomPaletteDialog(Context context, int themeResId) {
    super(context, themeResId);
  }
  
  public CustomPaletteDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
    super(context, cancelable, cancelListener);
  }
  
  public Activity activity;
  public Dialog dialog;
  public AppCompatButton btn_ok;
  AsapTextView tv_title;
  AsapTextView tv_order_no;
  View vw_action_type;
  RecyclerView rv_list;
  private RecyclerView.LayoutManager mLayoutManager;
  RecyclerView.Adapter adapter;
  
  private String mOrderNo;
  private int mActionType;
  
  public CustomPaletteDialog(Activity a, RecyclerView.Adapter adapter, String orderNo, int actionTypeColor) {
    super(a);
    this.activity = a;
    this.adapter = adapter;
    this.mOrderNo = orderNo;
    this.mActionType = actionTypeColor;
    setupLayout();
  }
  
  private void setupLayout() {
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.custom_paletts_layout);
  
    btn_ok = (AppCompatButton)findViewById(R.id.btn_ok);
    tv_title = (AsapTextView)findViewById(R.id.tv_title);
    tv_order_no = (AsapTextView)findViewById(R.id.tv_order_no);
    tv_order_no.setText(mOrderNo);
    vw_action_type = (View)findViewById(R.id.vw_action_type);
    vw_action_type.setBackgroundColor(mActionType);
    rv_list = (RecyclerView)findViewById(R.id.rv_list);
    mLayoutManager = new LinearLayoutManager(activity);
    rv_list.setLayoutManager(mLayoutManager);
  
    rv_list.setAdapter(adapter);
    btn_ok.setOnClickListener(this);
  }
  
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_ok:
        dismiss();
        break;
      default:
        dismiss();
        break;
    }
  }
}
