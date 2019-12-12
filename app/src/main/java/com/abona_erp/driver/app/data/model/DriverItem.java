package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverItem {
  
  @SerializedName("LastnameFirstname")
  @Expose
  private String lastnameFirstname;
  
  @SerializedName("ImageUrl")
  @Expose
  private String imageUrl;
  
  public String getLastnameFirstname() {
    return lastnameFirstname;
  }
  
  public void setLastnameFirstname(String lastnameFirstname) {
    this.lastnameFirstname = lastnameFirstname;
  }
  
  public String getImageUrl() {
    return imageUrl;
  }
  
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
