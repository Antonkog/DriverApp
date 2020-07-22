package com.abona_erp.driver.app.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "device_profile")
public class DeviceProfile {
  
  @PrimaryKey(autoGenerate = true)
  private int id;
  
  @ColumnInfo(name = "device_id")
  private String deviceId;
  
  @ColumnInfo(name = "instance_id")
  private String instanceId;
  
  @ColumnInfo(name = "device_model")
  private String deviceModel;
  
  @ColumnInfo(name = "device_manufacturer")
  private String deviceManufacturer;
  
  @ColumnInfo(name = "device_serial")
  private String deviceSerial;
  
  @ColumnInfo(name = "language_code")
  private String languageCode;
  
  @ColumnInfo(name = "version_code")
  private int versionCode;
  
  @ColumnInfo(name = "version_name")
  private String versionName;
  
  @ColumnInfo(name = "created_at")
  //@TypeConverters({DateConverter.class})
  private String createdAt;
  
  @ColumnInfo(name = "modified_at")
  //@TypeConverters({DateConverter.class})
  private String modifiedAt;
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public String getDeviceId() {
    return deviceId;
  }
  
  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }
  
  public String getInstanceId() {
    return instanceId;
  }
  
  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }
  
  public String getDeviceModel() {
    return deviceModel;
  }
  
  public void setDeviceModel(String deviceModel) {
    this.deviceModel = deviceModel;
  }
  
  public String getDeviceManufacturer() {
    return deviceManufacturer;
  }
  
  public void setDeviceManufacturer(String deviceManufacturer) {
    this.deviceManufacturer = deviceManufacturer;
  }
  
  public String getDeviceSerial() {
    return deviceSerial;
  }
  
  public void setDeviceSerial(String deviceSerial) {
    this.deviceSerial = deviceSerial;
  }
  
  public String getLanguageCode() {
    return languageCode;
  }
  
  public void setLanguageCode(String languageCode) {
    this.languageCode = languageCode;
  }
  
  public int getVersionCode() {
    return versionCode;
  }
  
  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }
  
  public String getVersionName() {
    return versionName;
  }
  
  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }
  
  @NonNull
  public String getCreatedAt() {
    return createdAt;
  }
  
  public void setCreatedAt(@NonNull String createdAt) {
    this.createdAt = createdAt;
  }
  
  @NonNull
  public String getModifiedAt() {
    return modifiedAt;
  }
  
  public void setModifiedAt(@NonNull String modifiedAt) {
    this.modifiedAt = modifiedAt;
  }
}
