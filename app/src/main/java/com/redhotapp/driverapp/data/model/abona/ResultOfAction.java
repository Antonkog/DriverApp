package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultOfAction {
  
  @SerializedName("IsSuccess")
  @Expose
  private boolean isSuccess;
  
  @SerializedName("IsException")
  @Expose
  private boolean isException;
  
  @SerializedName("Text")
  @Expose
  private String text;
  
  @SerializedName("CommunicationItem")
  @Expose
  private CommItem commItem;
  
  @SerializedName("AllTask")
  @Expose
  private List<TaskItem> allTask;
  
  @SerializedName("DelayReasons")
  @Expose
  private List<DelayReasonItem> delayReasonItems;
  
  public boolean getIsSuccess() {
    return isSuccess;
  }
  
  public void setIsSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }
  
  public boolean getIsException() {
    return isException;
  }
  
  public void setIsException(boolean isException) {
    this.isException = isException;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public CommItem getCommItem() {
    return commItem;
  }
  
  public void setCommItem(CommItem commItem) {
    this.commItem = commItem;
  }
  
  public List<TaskItem> getAllTask() {
    return allTask;
  }
  
  public void setAllTask(List<TaskItem> allTask) {
    this.allTask = allTask;
  }
  
  public List<DelayReasonItem> getDelayReasonItems() {
    return delayReasonItems;
  }
  
  public void setDelayReasonItems(List<DelayReasonItem> delayReasonItems) {
    this.delayReasonItems = delayReasonItems;
  }

  @Override
  public String toString() {
    return "ResultOfAction{" +
            "isSuccess=" + isSuccess +
            ", isException=" + isException +
            ", text='" + text + '\'' +
            ", commItem=" + commItem +
            ", allTask=" + allTask +
            ", delayReasonItems=" + delayReasonItems +
            '}';
  }
}
