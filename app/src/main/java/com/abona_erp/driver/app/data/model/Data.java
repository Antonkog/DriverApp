package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
  
  @SerializedName("Header")
  @Expose
  private Header header;
  
  @SerializedName("TaskItem")
  @Expose
  private TaskItem taskItem;
  
  @SerializedName("ActivityItem")
  @Expose
  private ActivityItem activityItem;
  
  @SerializedName("ConfirmationItem")
  @Expose
  private ConfirmationItem confirmationItem;
  
  @SerializedName("DeviceProfileItem")
  @Expose
  private DeviceProfileItem deviceProfileItem;
  
  public Header getHeader() {
    return header;
  }
  
  public void setHeader(Header header) {
    this.header = header;
  }
  
  public TaskItem getTaskItem() {
    return taskItem;
  }
  
  public void setTaskItem(TaskItem taskItem) {
    this.taskItem = taskItem;
  }
  
  public ActivityItem getActivityItem() {
    return activityItem;
  }
  
  public void setActivityItem(ActivityItem activityItem) {
    this.activityItem = activityItem;
  }
  
  public ConfirmationItem getConfirmationItem() {
    return confirmationItem;
  }
  
  public void setConfirmationItem(ConfirmationItem confirmationItem) {
    this.confirmationItem = confirmationItem;
  }
  
  public DeviceProfileItem getDeviceProfileItem() {
    return deviceProfileItem;
  }
  
  public void setDeviceProfileItem(DeviceProfileItem deviceProfileItem) {
    this.deviceProfileItem = deviceProfileItem;
  }
}
