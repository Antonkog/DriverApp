package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommItem {
  
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
  
  //@SerializedName("TaskStatusInfo")
  //@Expose
  //private TaskStatusInfo taskStatusInfo;
  
  @SerializedName("VehicleItem")
  @Expose
  private VehicleItem vehicleItem;
  
  @SerializedName("PercentItem")
  @Expose
  private PercentItem percentItem;
  
  @SerializedName("DocumentItem")
  @Expose
  private DocumentItem documentItem;
  
  @SerializedName("DelayReasons")
  @Expose
  private List<DelayReasonItem> delayReasonItems;
  
  // ------------------------------------------------------------------------
  // GETTER & SETTER
  
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
  /*
  public TaskStatusInfo getTaskStatusInfo() {
    return taskStatusInfo;
  }
  
  public void setTaskStatusInfo(TaskStatusInfo taskStatusInfo) {
    this.taskStatusInfo = taskStatusInfo;
  }
  */
  public VehicleItem getVehicleItem() {
    return vehicleItem;
  }
  
  public void setVehicleItem(VehicleItem vehicleItem) {
    this.vehicleItem = vehicleItem;
  }
  
  public PercentItem getPercentItem() {
    return percentItem;
  }
  
  public void setPercentItem(PercentItem percentItem) {
    this.percentItem = percentItem;
  }
  
  public DocumentItem getDocumentItem() {
    return documentItem;
  }
  
  public void setDocumentItem(DocumentItem documentItem) {
    this.documentItem = documentItem;
  }
  
  public List<DelayReasonItem> getDelayReasonItems() {
    return this.delayReasonItems;
  }
  
  public void setDelayReasonItems(List<DelayReasonItem> delayReasonItems) {
    this.delayReasonItems = delayReasonItems;
  }
}
