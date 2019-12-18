package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverItem {
  
  @SerializedName("LastnameFirstname")
  @Expose
  private String lastnameFirstname;
  
  @SerializedName("DriverNo")
  @Expose
  private Integer driverNo;
  
  @SerializedName("ImageUrl")
  @Expose
  private String imageUrl;
  
  public String getLastnameFirstname() {
    return lastnameFirstname;
  }
  
  public void setLastnameFirstname(String lastnameFirstname) {
    this.lastnameFirstname = lastnameFirstname;
  }
  
  public Integer getDriverNo() {
    return driverNo;
  }
  
  public void setDriverNo(int driverNo) {
    this.driverNo = driverNo;
  }
  
  public String getImageUrl() {
    return imageUrl;
  }
  
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
