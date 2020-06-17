package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaskDetails {
  
  // Ladestelle.Waren
  @SerializedName("Description")
  @Expose
  private String description;
  
  // Ladestelle.Ladereihenfolge
  @SerializedName("LoadingOrder")
  @Expose
  private Integer loadingOrder;
  
  @SerializedName("ReferenceId1")
  @Expose
  private String referenceId1;
  
  @SerializedName("ReferenceId2")
  @Expose
  private String referenceId2;
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public Integer getLoadingOrder() {
    return loadingOrder;
  }
  
  public void setLoadingOrder(Integer loadingOrder) {
    this.loadingOrder = loadingOrder;
  }
  
  public String getReferenceId1() {
    return referenceId1;
  }
  
  public void setReferenceId1(String referenceId1) {
    this.referenceId1 = referenceId1;
  }
  
  public String getReferenceId2() {
    return referenceId2;
  }
  
  public void setReferenceId2(String referenceId2) {
    this.referenceId2 = referenceId2;
  }
}
