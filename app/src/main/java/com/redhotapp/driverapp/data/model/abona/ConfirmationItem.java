package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ConfirmationItem {
  
  @SerializedName("ConfirmationType")
  @Expose
  private ConfirmationType confirmationType;
  
  @SerializedName("TimeStampConfirmationUTC")
  @Expose
  private Date timeStampConfirmationUTC;
  
  @SerializedName("MandantId")
  @Expose
  private Integer mandantId;
  
  @SerializedName("TaskId")
  @Expose
  private Integer taskId;
  
  @SerializedName("TaskChangeId")
  @Expose
  private Integer taskChangeId;
  
  @SerializedName("ActivityId")
  @Expose
  private Integer activityId;
  
  @SerializedName("Task")
  @Expose
  private TaskItem taskItem;
  
  @SerializedName("Activity")
  @Expose
  private ActivityItem activityItem;
  
  @SerializedName("Text")
  @Expose
  private String text;
  
  public ConfirmationType getConfirmationType() {
    return confirmationType;
  }
  
  public void setConfirmationType(ConfirmationType confirmationType) {
    this.confirmationType = confirmationType;
  }
  
  public Date getTimeStampConfirmationUTC() {
    return timeStampConfirmationUTC;
  }
  
  public void setTimeStampConfirmationUTC(Date timeStampConfirmationUTC) {
    this.timeStampConfirmationUTC = timeStampConfirmationUTC;
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
  
  public Integer getTaskChangeId() {
    return taskChangeId;
  }
  
  public void setTaskChangeId(Integer taskChangeId) {
    this.taskChangeId = taskChangeId;
  }
  
  public Integer getActivityId() {
    return activityId;
  }
  
  public void setActivityId(Integer activityId) {
    this.activityId = activityId;
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
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
}
