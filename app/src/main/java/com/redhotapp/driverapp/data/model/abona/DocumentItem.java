package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DocumentItem {
  
  @SerializedName("MandantId")
  @Expose
  private Integer mandantId;
  
  @SerializedName("OrderNo")
  @Expose
  private Integer orderNo;

  public Integer getMandantId() {
    return mandantId;
  }
  
  public void setMandantId(Integer mandantId) {
    this.mandantId = mandantId;
  }
  
  public Integer getOrderNo() {
    return orderNo;
  }
  
  public void setOrderNo(Integer orderNo) {
    this.orderNo = orderNo;
  }
}
