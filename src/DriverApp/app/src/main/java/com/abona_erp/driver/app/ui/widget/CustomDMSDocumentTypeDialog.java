package com.abona_erp.driver.app.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatButton;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.model.DMSDocumentType;

public class CustomDMSDocumentTypeDialog extends Dialog implements View.OnClickListener {
  
  public interface OnSelectedTypeEventListener {
    void selectedEvent(DMSDocumentType documentType);
  }
  
  public CustomDMSDocumentTypeDialog(Context context, int themeResId) {
    super(context, themeResId);
  }
  
  public CustomDMSDocumentTypeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
    super(context, cancelable, cancelListener);
  }
  
  public Activity activity;
  public Dialog dialog;
  public AppCompatButton btn_ok;
  AsapTextView tv_title;
  AsapTextView tv_order_no;
  View vw_action_type;
  
  private OnSelectedTypeEventListener onSelectedTypeEventListener;
  
  RadioGroup rg_group_1;
  RadioGroup rg_group_2;
  RadioGroup rg_group_3;
  RadioGroup rg_group_4;
  RadioButton rb_na;
  RadioButton rb_pod;
  RadioButton rb_cmr;
  RadioButton rb_pallets_note;
  RadioButton rb_safety_certificate;
  RadioButton rb_shipment_image;
  RadioButton rb_damaged_shipment_image;
  RadioButton rb_damaged_vehicle_image;
  
  private DMSDocumentType mDocumentType;
  private boolean isChecking = true;
  private int mCheckedId = R.id.rb_na;
  private DMSDocumentType documentType = DMSDocumentType.NA;
  
  private String mOrderNo;
  private int mActionType;
  
  public CustomDMSDocumentTypeDialog(Activity a, String orderNo, DMSDocumentType documentType, OnSelectedTypeEventListener onSelectedTypeEventListener) {
    super(a);
    this.activity = a;
    this.mOrderNo = orderNo;
    this.mDocumentType = documentType;
    this.onSelectedTypeEventListener = onSelectedTypeEventListener;
    //this.mActionType = actionType;
    setupLayout();
  }
  
  private void setupLayout() {
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.custom_dms_layout);
    
    btn_ok = (AppCompatButton)findViewById(R.id.btn_ok);
    tv_title = (AsapTextView)findViewById(R.id.tv_title);
    tv_order_no = (AsapTextView)findViewById(R.id.tv_order_no);
    tv_order_no.setText(mOrderNo);
    vw_action_type = (View)findViewById(R.id.vw_action_type);
    //vw_action_type.setBackgroundColor(mActionType);
    
    rg_group_1 = (RadioGroup)findViewById(R.id.rg_group_1);
    rg_group_1.setOnCheckedChangeListener(listener1);
    rg_group_2 = (RadioGroup)findViewById(R.id.rg_group_2);
    rg_group_2.setOnCheckedChangeListener(listener2);
    rg_group_3 = (RadioGroup)findViewById(R.id.rg_group_3);
    rg_group_3.setOnCheckedChangeListener(listener3);
    rg_group_4 = (RadioGroup)findViewById(R.id.rg_group_4);
    rg_group_4.setOnCheckedChangeListener(listener4);
    rb_na = (RadioButton)findViewById(R.id.rb_na);
    rb_na.setChecked(true);
    rb_pod = (RadioButton)findViewById(R.id.rb_pod);
    rb_cmr = (RadioButton)findViewById(R.id.rb_cmr);
    rb_pallets_note = (RadioButton)findViewById(R.id.rb_pallets_note);
    rb_safety_certificate = (RadioButton)findViewById(R.id.rb_safety_certificate);
    rb_shipment_image = (RadioButton)findViewById(R.id.rb_shipment_image);
    rb_damaged_shipment_image = (RadioButton)findViewById(R.id.rb_damaged_shipment_image);
    rb_damaged_vehicle_image = (RadioButton)findViewById(R.id.rb_damaged_vehicle_image);
    
    switch (mDocumentType) {
      case POD:
        rb_pod.setChecked(true);
        break;
      case CMR:
        rb_cmr.setChecked(true);
        break;
      case PALLETS_NOTE:
        rb_pallets_note.setChecked(true);
        break;
      case SAFETY_CERTIFICATE:
        rb_safety_certificate.setChecked(true);
        break;
      case SHIPMENT_IMAGE:
        rb_shipment_image.setChecked(true);
        break;
      case DAMAGED_SHIPMENT_IMAGE:
        rb_damaged_shipment_image.setChecked(true);
        break;
      case DAMAGED_VEHICLE_IMAGE:
        rb_damaged_vehicle_image.setChecked(true);
        break;
      case NA:
      default:
        rb_na.setChecked(true);
        break;
    }

    btn_ok.setOnClickListener(this);
  }
  
  private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
      if (checkedId != -1 && isChecking) {
        rg_group_2.setOnCheckedChangeListener(null);
        rg_group_2.clearCheck();
        rg_group_2.setOnCheckedChangeListener(listener2);
        
        rg_group_3.setOnCheckedChangeListener(null);
        rg_group_3.clearCheck();
        rg_group_3.setOnCheckedChangeListener(listener3);
        
        rg_group_4.setOnCheckedChangeListener(null);
        rg_group_4.clearCheck();
        rg_group_4.setOnCheckedChangeListener(listener4);
        
        mCheckedId = checkedId;
        isChecking = false;
      }
      isChecking = true;
    }
  };
  
  private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
      if (checkedId != -1 && isChecking) {
        rg_group_1.setOnCheckedChangeListener(null);
        rg_group_1.clearCheck();
        rg_group_1.setOnCheckedChangeListener(listener1);
    
        rg_group_3.setOnCheckedChangeListener(null);
        rg_group_3.clearCheck();
        rg_group_3.setOnCheckedChangeListener(listener3);
        
        rg_group_4.setOnCheckedChangeListener(null);
        rg_group_4.clearCheck();
        rg_group_4.setOnCheckedChangeListener(listener4);
        
        mCheckedId = checkedId;
        isChecking = false;
      }
      isChecking = true;
    }
  };
  
  private RadioGroup.OnCheckedChangeListener listener3 = new RadioGroup.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
      if (checkedId != -1 && isChecking) {
        rg_group_1.setOnCheckedChangeListener(null);
        rg_group_1.clearCheck();
        rg_group_1.setOnCheckedChangeListener(listener1);
    
        rg_group_2.setOnCheckedChangeListener(null);
        rg_group_2.clearCheck();
        rg_group_2.setOnCheckedChangeListener(listener2);
    
        rg_group_4.setOnCheckedChangeListener(null);
        rg_group_4.clearCheck();
        rg_group_4.setOnCheckedChangeListener(listener4);
        
        mCheckedId = checkedId;
        isChecking = false;
      }
      isChecking = true;
    }
  };
  
  private RadioGroup.OnCheckedChangeListener listener4 = new RadioGroup.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
      if (checkedId != -1 && isChecking) {
        rg_group_1.setOnCheckedChangeListener(null);
        rg_group_1.clearCheck();
        rg_group_1.setOnCheckedChangeListener(listener1);
    
        rg_group_2.setOnCheckedChangeListener(null);
        rg_group_2.clearCheck();
        rg_group_2.setOnCheckedChangeListener(listener2);
    
        rg_group_3.setOnCheckedChangeListener(null);
        rg_group_3.clearCheck();
        rg_group_3.setOnCheckedChangeListener(listener3);
    
        mCheckedId = checkedId;
        isChecking = false;
      }
      isChecking = true;
    }
  };
  
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_ok:
        if (mCheckedId == R.id.rb_na) {
          mDocumentType = DMSDocumentType.NA;
        } else if (mCheckedId == R.id.rb_pod) {
          mDocumentType = DMSDocumentType.POD;
        } else if (mCheckedId == R.id.rb_cmr) {
          mDocumentType = DMSDocumentType.CMR;
        } else if (mCheckedId == R.id.rb_pallets_note) {
          mDocumentType = DMSDocumentType.PALLETS_NOTE;
        } else if (mCheckedId == R.id.rb_safety_certificate) {
          mDocumentType = DMSDocumentType.SAFETY_CERTIFICATE;
        } else if (mCheckedId == R.id.rb_shipment_image) {
          mDocumentType = DMSDocumentType.SHIPMENT_IMAGE;
        } else if (mCheckedId == R.id.rb_damaged_shipment_image) {
          mDocumentType = DMSDocumentType.DAMAGED_SHIPMENT_IMAGE;
        } else if (mCheckedId == R.id.rb_damaged_vehicle_image) {
          mDocumentType = DMSDocumentType.DAMAGED_VEHICLE_IMAGE;
        } else {
          mDocumentType = DMSDocumentType.NA;
        }
        
        onSelectedTypeEventListener.selectedEvent(mDocumentType);
        dismiss();
        break;
      default:
        dismiss();
        break;
    }
  }
}
