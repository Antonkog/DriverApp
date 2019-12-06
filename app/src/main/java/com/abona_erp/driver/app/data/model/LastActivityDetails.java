package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastActivityDetails {
  
  @SerializedName("Description")
  @Expose
  private String description;
  
  @SerializedName("Timestamp")
  @Expose
  private String timestamp;
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getTimestamp() {
    return timestamp;
  }
  
  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }
}
