package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskDocument {
  
  @SerializedName("TaskId")
  @Expose
  private Integer taskId;
  
  @SerializedName("LinksToTaskId")
  @Expose
  private List<String> linksToTaskId;
  
  public Integer getTaskId() {
    return taskId;
  }
  
  public void setTaskId(Integer taskId) {
    this.taskId = taskId;
  }
  
  public List<String> getLinksToTaskId() {
    return linksToTaskId;
  }
  
  public void setLinksToTaskId(List<String> linksToTaskId) {
    this.linksToTaskId = linksToTaskId;
  }
}
