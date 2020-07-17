package com.abona_erp.driver.app.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatButton;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.DelayReasonEntity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.DelaySource;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.app.util.DelayReasonUtil;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class CustomDelayReasonDialog extends Dialog
  implements View.OnClickListener {
  
  public CustomDelayReasonDialog(Context context, int themeResId) {
    super(context, themeResId);
  }
  
  public CustomDelayReasonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
    super(context, cancelable, cancelListener);
  }
  
  public Activity activity;
  public Dialog dialog;
  public AppCompatButton btn_ok;
  public AppCompatButton btn_cancel;
  public EditText et_delay_reason_comment;
  public AsapTextView tv_current_delay_reason;
  AsapTextView tv_title;
  AsapTextView tv_order_no;
  View vw_action_type;
  
  Notify mNotify;
  int mActivityId;
  
  private int mActionType;
  
  private DelaySource mDelaySource = DelaySource.DRIVER;
  private SmartMaterialSpinner smsDelaySource;
  private List<String> _delaySourceList = new ArrayList<>();
  
  private SmartMaterialSpinner smsDelayReasonText;
  private List<String> _delayReasonTextList = new ArrayList<>();
  
  private SmartMaterialSpinner smsDelayInMinutes;
  private List<String> _delayInMinutes = new ArrayList<>();
  
  String mCurrentDelay;
  List<DelayReasonEntity> mReasonEntity;
  
  public CustomDelayReasonDialog(Activity a, Notify notify, List<DelayReasonEntity> reasonEntity, int activityId, String currentDelay, int actionType) {
    super(a);
    this.activity = a;
    this.mNotify = notify;
    this.mActionType = actionType;
    this.mReasonEntity = reasonEntity;
    this.mActivityId = activityId;
    this.mCurrentDelay = currentDelay;
  }
  
  private void setupLayout() {
    
    _delaySourceList.clear();
    _delaySourceList.add("Dispatcher");
    _delaySourceList.add("Customer");
    _delaySourceList.add("Driver");
    smsDelaySource.setItem(_delaySourceList);
    smsDelaySource.setSelection(2);
    
    _delayReasonTextList.clear();
    for (int i = 0; i < mReasonEntity.size(); i++) {
      
      DelayReasonEntity entity = mReasonEntity.get(i);
      
      String reasonText = "";
      if (entity.getTranslatedReasonText() == null) {
        if (entity.getReasonText() != null) {
          reasonText = entity.getReasonText();
        }
      } else {
        reasonText = entity.getTranslatedReasonText();
      }
      reasonText += " (";
      reasonText += String.valueOf(entity.getCode());
      reasonText += ")";
      _delayReasonTextList.add(reasonText);
    }
    smsDelayReasonText.setItem(_delayReasonTextList);
    smsDelayReasonText.setSelection(0);
  
    _delayInMinutes.clear();
    _delayInMinutes.add("15");    //  0
    _delayInMinutes.add("30");    //  1
    _delayInMinutes.add("45");    //  2
    _delayInMinutes.add("60");    //  3
    _delayInMinutes.add("90");    //  4
    _delayInMinutes.add("120");   //  5
    _delayInMinutes.add("150");   //  6
    _delayInMinutes.add("180");   //  7
    _delayInMinutes.add("210");   //  8
    _delayInMinutes.add("240");   //  9
    _delayInMinutes.add("270");   // 10
    _delayInMinutes.add("300");   // 11
    _delayInMinutes.add("330");   // 12
    _delayInMinutes.add("360");   // 13
    _delayInMinutes.add("390");   // 14
    _delayInMinutes.add("420");   // 15
    _delayInMinutes.add("450");   // 16
    _delayInMinutes.add("480");   // 17
    smsDelayInMinutes.setItem(_delayInMinutes);
    smsDelayInMinutes.setSelection(0);
  }
  
  private String getDelayReasonMinutesFromSelectedId(int selectedId) {
    
    switch (selectedId) {
      case 0:  return "15";
      case 1:  return "30";
      case 2:  return "45";
      case 3:  return "60";
      case 4:  return "90";
      case 5:  return "120";
      case 6:  return "150";
      case 7:  return "180";
      case 8:  return "210";
      case 9:  return "240";
      case 10: return "270";
      case 11: return "300";
      case 12: return "330";
      case 13: return "360";
      case 14: return "390";
      case 15: return "420";
      case 16: return "450";
      case 17: return "480";
      default: return "";
    }
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.custom_delay_reason_layout);
    
    btn_ok = (AppCompatButton)findViewById(R.id.btn_ok);
    btn_cancel = (AppCompatButton)findViewById(R.id.btn_cancel);
    et_delay_reason_comment = (EditText)findViewById(R.id.et_delay_reason_comment);
    tv_title = (AsapTextView)findViewById(R.id.tv_title);
    tv_order_no = (AsapTextView)findViewById(R.id.tv_order_no);
    tv_order_no.setText(AppUtils.parseOrderNo(mNotify.getOrderNo()));
    vw_action_type = (View)findViewById(R.id.vw_action_type);
    vw_action_type.setBackgroundColor(mActionType);
    tv_current_delay_reason = (AsapTextView)findViewById(R.id.tv_current_delay_reason);
    if (!mCurrentDelay.equalsIgnoreCase("0 min")) {
      tv_current_delay_reason.setText(mCurrentDelay);
      tv_current_delay_reason.setTextColor(Color.parseColor("#E30613"));
    } else {
      tv_current_delay_reason.setTextColor(Color.parseColor("#10ac84"));
    }
    
    
    smsDelaySource = (SmartMaterialSpinner)findViewById(R.id.sms_delay_source);
    smsDelayReasonText = (SmartMaterialSpinner)findViewById(R.id.sms_delay_text);
    smsDelayInMinutes = (SmartMaterialSpinner)findViewById(R.id.sms_delay_in_minutes);
    
    smsDelaySource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
          case 0: mDelaySource = DelaySource.DISPATCHER; break;
          case 1: mDelaySource = DelaySource.CUSTOMER; break;
          case 2:
          default: mDelaySource = DelaySource.DRIVER; break;
        }
      }
  
      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {
    
      }
    });
    
    smsDelayInMinutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String delayInMinutes = getDelayReasonMinutesFromSelectedId(i);
      }
  
      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {
    
      }
    });
    
    btn_ok.setOnClickListener(this);
    btn_cancel.setOnClickListener(this);
  
    setupLayout();
  }
  
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_ok: {
        sendDelayReasonToApi();
        dismiss();
      } break;
      case R.id.btn_cancel:
      default: {
        dismiss();
      } break;
    }
  }
  
  private void sendDelayReasonToApi() {
    
    long id = smsDelayReasonText.getSelectedItemPosition();
    int waitingReasonId = mReasonEntity.get((int)id).getWaitingReasonId();
    String delayInMinutes = getDelayReasonMinutesFromSelectedId(smsDelayInMinutes.getSelectedItemPosition());

    DelayReasonUtil.addDelayReasonToSendServer(mNotify.getId(), waitingReasonId, mActivityId,
      mNotify.getMandantId(), mNotify.getTaskId(), Integer.valueOf(delayInMinutes),
      mDelaySource.ordinal(), et_delay_reason_comment.getText().toString());
  }
}
