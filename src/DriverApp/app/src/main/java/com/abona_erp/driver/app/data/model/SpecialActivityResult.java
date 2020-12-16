package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class SpecialActivityResult {
  
  @SerializedName("SpecialFunctionFinished")
  @Expose
  private Date specialFunctionFinished;
  
  @SerializedName("ResultString1")
  @Expose
  private String resultString1;
  
  @SerializedName("ResultInt1")
  @Expose
  private Integer resultInt1;
  
  public Date getSpecialFunctionFinished() {
    return specialFunctionFinished;
  }
  
  public void setSpecialFunctionFinished(Date specialFunctionFinished) {
    this.specialFunctionFinished = specialFunctionFinished;
  }
  
  public String getResultString1() {
    return resultString1;
  }
  
  public void setResultString1(String resultString1) {
    this.resultString1 = resultString1;
  }
  
  public Integer getResultInt1() {
    return resultInt1;
  }
  
  public void setResultInt1(Integer resultInt1) {
    this.resultInt1 = resultInt1;
  }
}
