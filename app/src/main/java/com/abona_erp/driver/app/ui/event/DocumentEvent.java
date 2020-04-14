package com.abona_erp.driver.app.ui.event;

import androidx.annotation.NonNull;

public class DocumentEvent implements BaseEvent {
  
  private int mMandantID;
  private int mOrderNo;
  
  public DocumentEvent(@NonNull int mandantID, @NonNull int orderNo) {
    this.mMandantID = mandantID;
    this.mOrderNo   = orderNo;
  }
  
  public int getMandantID() {
    return mMandantID;
  }
  
  public void setMandantID(@NonNull int mandantID) {
    this.mMandantID = mandantID;
  }
  
  public int getOrderNo() {
    return mOrderNo;
  }
  
  public void setOrderNo(@NonNull int orderNo) {
    this.mOrderNo = orderNo;
  }
}
