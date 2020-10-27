package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActivityDelayItem {
  
  @SerializedName("ActivityId")
  @Expose
  public Integer activityId;
  
  @SerializedName("MandantId")
  @Expose
  public Integer mandantId;
  
  @SerializedName("TaskId")
  @Expose
  public Integer taskId;
  
  @SerializedName("DelayReasons")
  @Expose
  public List<DelayReasonItem> delayReasonItems;
  
  public Integer getActivityId() {
    return activityId;
  }
  
  public void setActivityId(Integer activityId) {
    this.activityId = activityId;
  }
  
  public Integer getMandantId() {
    return mandantId;
  }
  
  public void setMandantId(Integer mandantId) {
    this.mandantId = mandantId;
  }
  
  public Integer getTaskId() {
    return taskId;
  }
  
  public void setTaskId(Integer taskId) {
    this.taskId = taskId;
  }
  
  public List<DelayReasonItem> getDelayReasonItems() {
    return delayReasonItems;
  }
  
  public void setDelayReasonItems(List<DelayReasonItem> delayReasonItems) {
    this.delayReasonItems = delayReasonItems;
  }
}
