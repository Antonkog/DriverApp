package com.abona_erp.driver.app.data.remote.response;

import com.abona_erp.driver.app.data.model.ActivityItem;
import com.abona_erp.driver.app.data.model.DeviceProfileItem;
import com.abona_erp.driver.app.data.model.TaskItem;
import com.google.gson.annotations.SerializedName;

public class PostResponse {
  
  @SerializedName("IsSuccess")
  Boolean isSuccess;
  
  @SerializedName("Text")
  String text;
  
  @SerializedName("DeviceProfileItem")
  DeviceProfileItem deviceProfileItem;
  
  @SerializedName("TaskItem")
  TaskItem taskItem;
  
  @SerializedName("ActivityItem")
  ActivityItem activityItem;
  
  public Boolean getIsSuccess() {
    return isSuccess;
  }
  
  public void setIsSuccess(Boolean isSuccess) {
    this.isSuccess = isSuccess;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public DeviceProfileItem getDeviceProfileItem() {
    return deviceProfileItem;
  }
  
  public void setDeviceProfileItem(DeviceProfileItem deviceProfileItem) {
    this.deviceProfileItem = deviceProfileItem;
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
}