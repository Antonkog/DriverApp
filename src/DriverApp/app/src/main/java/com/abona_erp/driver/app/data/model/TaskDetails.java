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
}
