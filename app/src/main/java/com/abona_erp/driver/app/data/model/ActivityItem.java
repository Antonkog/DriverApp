package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ActivityItem {
  
  @SerializedName("MandantId")
  @Expose
  private int mandantId;
  
  @SerializedName("TaskId")
  @Expose
  private int taskId;
  
  @SerializedName("ActivityId")
  @Expose
  private int activityId;
  
  @SerializedName("Name")
  @Expose
  private String name;
  
  @SerializedName("Description")
  @Expose
  private String description;
  
  @SerializedName("Started")
  @Expose
  private Date started;
  
  @SerializedName("Finished")
  @Expose
  private Date finished;
  
  @SerializedName("Status")
  @Expose
  private ActivityStatus status;
  
  @SerializedName("Sequence")
  @Expose
  private int sequence;
  
  public int getMandantId() {
    return mandantId;
  }
  
  public void setMandantId(int mandantId) {
    this.mandantId = mandantId;
  }
  
  public int getTaskId() {
    return taskId;
  }
  
  public void setTaskId(int taskId) {
    this.taskId = taskId;
  }
  
  public int getActivityId() {
    return activityId;
  }
  
  public void setActivityId(int activityId) {
    this.activityId = activityId;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public Date getStarted() {
    return started;
  }
  
  public void setStarted(Date started) {
    this.started = started;
  }
  
  public Date getFinished() {
    return finished;
  }
  
  public void setFinished(Date finished) {
    this.finished = finished;
  }
  
  public ActivityStatus getStatus() {
    return status;
  }
  
  public void setStatus(ActivityStatus status) {
    this.status = status;
  }
  
  public int getSequence() {
    return sequence;
  }
  
  public void setSequence(int sequence) {
    this.sequence = sequence;
  }
}
