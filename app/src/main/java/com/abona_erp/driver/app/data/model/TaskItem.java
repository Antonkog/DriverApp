package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class TaskItem {
  
  @SerializedName("MandantId")
  @Expose
  private Integer mandantId;
  
  @SerializedName("TaskId")
  @Expose
  private Integer taskId;
  
  @SerializedName("TaskChangeId")
  @Expose
  private Integer taskChangeId;
  
  @SerializedName("AbonaTransferNr")
  @Expose
  private String abonaTransferNr;
  
  @SerializedName("PreviousTaskId")
  @Expose
  private Integer previousTaskId;
  
  @SerializedName("NextTaskId")
  @Expose
  private Integer nextTaskId;
  
  @SerializedName("VehiclePreviousTaskId")
  @Expose
  private Integer vehiclePreviousTaskId;
  
  @SerializedName("VehicleNextTaskId")
  @Expose
  private Integer vehicleNextTaskId;
  
  @SerializedName("ChangeReason")
  @Expose
  private TaskChangeReason changeReason;
  
  @SerializedName("OrderNo")
  @Expose
  private Integer orderNo;
  
  @SerializedName("Description")
  @Expose
  private String description;
  
  @SerializedName("KundenName")
  @Expose
  private String kundenName;
  
  @SerializedName("KundenNr")
  @Expose
  private Integer kundenNr;
  
  @SerializedName("ReferenceIdCustomer1")
  @Expose
  private String referenceIdCustomer1;
  
  @SerializedName("ReferenceIdCustomer2")
  @Expose
  private String referenceIdCustomer2;
  
  @SerializedName("Address")
  @Expose
  private AddressItem address;
  
  @SerializedName("TaskDueDateStart")
  @Expose
  private Date taskDueDateStart;
  
  @SerializedName("TaskDueDateFinish")
  @Expose
  private Date taskDueDateFinish;
  
  @SerializedName("Status")
  @Expose
  private TaskStatus status;
  
  @SerializedName("PercentFinished")
  @Expose
  private Double percentFinished;
  
  @SerializedName("PalletsAmount")
  @Expose
  private int palletsAmount;
  
  @SerializedName("Activities")
  @Expose
  List<ActivityItem> activities;
  
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
  
  public String getAbonaTransferNr() {
    return abonaTransferNr;
  }
  
  public void setAbonaTransferNr(String abonaTransferNr) {
    this.abonaTransferNr = abonaTransferNr;
  }
  
  public Integer getPreviousTaskId() {
    return previousTaskId;
  }
  
  public void setPreviousTaskId(Integer previousTaskId) {
    this.previousTaskId = previousTaskId;
  }
  
  public Integer getNextTaskId() {
    return nextTaskId;
  }
  
  public void setNextTaskId(Integer nextTaskId) {
    this.nextTaskId = nextTaskId;
  }
  
  public Integer getVehiclePreviousTaskId() {
    return vehiclePreviousTaskId;
  }
  
  public void setVehiclePreviousTaskId(Integer vehiclePreviousTaskId) {
    this.vehiclePreviousTaskId = vehiclePreviousTaskId;
  }
  
  public Integer getVehicleNextTaskId() {
    return vehicleNextTaskId;
  }
  
  public void setVehicleNextTaskId(Integer vehicleNextTaskId) {
    this.vehicleNextTaskId = vehicleNextTaskId;
  }
  
  public TaskChangeReason getChangeReason() {
    return changeReason;
  }
  
  public void setChangeReason(TaskChangeReason changeReason) {
    this.changeReason = changeReason;
  }
  
  public Integer getOrderNo() {
    return orderNo;
  }
  
  public void setOrderNo(Integer orderNo) {
    this.orderNo = orderNo;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getKundenName() {
    return kundenName;
  }
  
  public void setKundenName(String kundenName) {
    this.kundenName = kundenName;
  }
  
  public Integer getKundenNr() {
    return kundenNr;
  }
  
  public void setKundenNr(Integer kundenNr) {
    this.kundenNr = kundenNr;
  }
  
  public String getReferenceIdCustomer1() {
    return referenceIdCustomer1;
  }
  
  public void setReferenceIdCustomer1(String referenceIdCustomer1) {
    this.referenceIdCustomer1 = referenceIdCustomer1;
  }
  
  public String getReferenceIdCustomer2() {
    return referenceIdCustomer2;
  }
  
  public void setReferenceIdCustomer2(String referenceIdCustomer2) {
    this.referenceIdCustomer2 = referenceIdCustomer2;
  }
  
  public AddressItem getAddress() {
    return address;
  }
  
  public void setAddress(AddressItem address) {
    this.address = address;
  }
  
  public Date getTaskDueDateStart() {
    return taskDueDateStart;
  }
  
  public void setTaskDueDateStart(Date taskDueDateStart) {
    this.taskDueDateStart = taskDueDateStart;
  }
  
  public Date getTaskDueDateFinish() {
    return taskDueDateFinish;
  }
  
  public void setTaskDueDateFinish(Date taskDueDateFinish) {
    this.taskDueDateFinish = taskDueDateFinish;
  }
  
  public TaskStatus getTaskStatus() {
    return status;
  }
  
  public void setTaskStatus(TaskStatus status) {
    this.status = status;
  }
  
  public Double getPercentFinished() {
    return percentFinished;
  }
  
  public void setPercentFinished(Double percentFinished) {
    this.percentFinished = percentFinished;
  }
  
  public Integer getPalletsAmount() {
    return palletsAmount;
  }
  
  public void setPalletsAmount(Integer palletsAmount) {
    this.palletsAmount = palletsAmount;
  }
  
  public List<ActivityItem> getActivities() {
    return activities;
  }
}
