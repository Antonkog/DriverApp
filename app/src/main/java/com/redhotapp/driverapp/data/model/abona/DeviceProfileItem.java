package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class DeviceProfileItem {
  
  @SerializedName("Model")
  @Expose
  private String model;
  
  @SerializedName("Manufacturer")
  @Expose
  private String manufacturer;
  
  @SerializedName("DeviceId")
  @Expose
  private String deviceId;
  
  @SerializedName("Serial")
  @Expose
  private String serial;
  
  @SerializedName("InstanceId")
  @Expose
  private String instanceId;
  
  @SerializedName("CreatedDate")
  @Expose
  private String createdDate;
  
  @SerializedName("UpdatedDate")
  @Expose
  private String updatedDate;
  
  @SerializedName("LanguageCode")
  @Expose
  private String languageCode;
  
  @SerializedName("VersionCode")
  @Expose
  private Integer versionCode;
  
  @SerializedName("VersionName")
  @Expose
  private String versionName;
  
  public String getModel() {
    return model;
  }
  
  public void setModel(String model) {
    this.model = model;
  }
  
  public String getManufacturer() {
    return manufacturer;
  }
  
  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }
  
  public String getDeviceId() {
    return deviceId;
  }
  
  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }
  
  public String getSerial() {
    return serial;
  }
  
  public void setSerial(String serial) {
    this.serial = serial;
  }
  
  public String getInstanceId() {
    return instanceId;
  }
  
  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }
  
  public String getCreatedDate() {
    return createdDate;
  }
  
  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }
  
  public String getUpdatedDate() {
    return updatedDate;
  }
  
  public void setUpdatedDate(String updatedDate) {
    this.updatedDate = updatedDate;
  }
  
  public String getLanguageCode() {
    return languageCode;
  }
  
  public void setLanguageCode(String languageCode) {
    this.languageCode = languageCode;
  }
  
  public Integer getVersionCode() {
    return versionCode;
  }
  
  public void setVersionCode(Integer versionCode) {
    this.versionCode = versionCode;
  }
  
  public String getVersionName() {
    return versionName;
  }
  
  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }
}
