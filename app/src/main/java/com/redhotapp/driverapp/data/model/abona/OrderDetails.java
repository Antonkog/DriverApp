package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderDetails {
  
  @SerializedName("OrderNo")
  @Expose
  private Integer orderNo;
  
  @SerializedName("CustomerName")
  @Expose
  private String customerName;
  
  @SerializedName("CustomerNo")
  @Expose
  private Integer customerNo;
  
  @SerializedName("ReferenceIdCustomer1")
  @Expose
  private String referenceIdCustomer1;
  
  @SerializedName("ReferenceIdCustomer2")
  @Expose
  private String referenceIdCustomer2;
  
  public Integer getOrderNo() {
    return orderNo;
  }
  
  public void setOrderNo(Integer orderNo) {
    this.orderNo = orderNo;
  }
  
  public String getCustomerName() {
    return customerName;
  }
  
  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }
  
  public Integer getCustomerNo() {
    return customerNo;
  }
  
  public void setCustomerNo(Integer customerNo) {
    this.customerNo = customerNo;
  }
  
  public String getReferenceIdCustomer1() {
    return referenceIdCustomer1;
  }
  
  public void setReferenceIdCustomer1(String referenceIdCustomer1) {
    this.referenceIdCustomer1 = referenceIdCustomer1;
  }
  
  public String getReferenceIdCustomer2() {
    return referenceIdCustomer2;
  }
  
  public void setReferenceIdCustomer2(String referenceIdCustomer2) {
    this.referenceIdCustomer2 = referenceIdCustomer2;
  }
}
