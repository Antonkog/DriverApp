package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaskStatusInfo {
  
  @SerializedName("MandantId")
  @Expose
  private Integer mandantId;
  
  @SerializedName("TaskId")
  @Expose
  private Integer taskId;
  
  @SerializedName("TaskStatus")
  @Expose
  private TaskStatus taskStatus;
  
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
  
  public TaskStatus getTaskStatus() {
    return taskStatus;
  }
  
  public void setTaskStatus(TaskStatus taskStatus) {
    this.taskStatus = taskStatus;
  }
}
