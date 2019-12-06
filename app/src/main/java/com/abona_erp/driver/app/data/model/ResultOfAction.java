package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultOfAction {
  
  @SerializedName("IsSuccess")
  @Expose
  private boolean isSuccess;
  
  @SerializedName("Text")
  @Expose
  private String text;
  
  @SerializedName("CommunicationItem")
  @Expose
  private CommItem commItem;
  
  public boolean getIsSuccess() {
    return isSuccess;
  }
  
  public void setIsSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public CommItem getCommItem() {
    return commItem;
  }
  
  public void setCommItem(CommItem commItem) {
    this.commItem = commItem;
  }
}
