package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PercentItem {
  
  @SerializedName("PercentFinished")
  @Expose
  private Double percentFinished;
  
  @SerializedName("TotalPercentFinished")
  @Expose
  private Double totalPercentFinished;
  
  public Double getPercentFinished() {
    return percentFinished;
  }
  
  public void setPercentFinished(Double percentFinished) {
    this.percentFinished = percentFinished;
  }
  
  public Double getTotalPercentFinished() {
    return totalPercentFinished;
  }
  
  public void setTotalPercentFinished(Double totalPercentFinished) {
    this.totalPercentFinished = totalPercentFinished;
  }
}
