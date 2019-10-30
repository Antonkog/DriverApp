package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class TaskItem {
  
  @SerializedName("MandantId")
  @Expose
  private int mandantId;
  
  @SerializedName("TaskId")
  @Expose
  private int taskId;
  
  @SerializedName("AbonaTransferNr")
  @Expose
  private String abonaTransferNr;
  
  @SerializedName("PreviousTaskId")
  @Expose
  private int previousTaskId;
  
  @SerializedName("NextTaskId")
  @Expose
  private int nextTaskId;
  
  @SerializedName("VehiclePreviousTaskId")
  @Expose
  private int vehiclePreviousTaskId;
  
  @SerializedName("VehicleNextTaskId")
  @Expose
  private int vehicleNextTaskId;
  
  @SerializedName("ChangeReason")
  @Expose
  private TaskChangeReason changeReason;
  
  @SerializedName("OrderNo")
  @Expose
  private int orderNo;
  
  @SerializedName("Description")
  @Expose
  private String description;
  
  @SerializedName("KundenName")
  @Expose
  private String kundenName;
  
  @SerializedName("KundenNr")
  @Expose
  private int kundenNr;
  
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
  
  @SerializedName("PalletsAmount")
  @Expose
  private int palletsAmount;
  
  @SerializedName("Activities")
  @Expose
  List<ActivityItem> activities;
  
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
  
  public String getAbonaTransferNr() {
    return abonaTransferNr;
  }
  
  public void setAbonaTransferNr(String abonaTransferNr) {
    this.abonaTransferNr = abonaTransferNr;
  }
  
  public int getPreviousTaskId() {
    return previousTaskId;
  }
  
  public void setPreviousTaskId(int previousTaskId) {
    this.previousTaskId = previousTaskId;
  }
  
  public int getNextTaskId() {
    return nextTaskId;
  }
  
  public void setNextTaskId(int nextTaskId) {
    this.nextTaskId = nextTaskId;
  }
  
  public int getVehiclePreviousTaskId() {
    return vehiclePreviousTaskId;
  }
  
  public void setVehiclePreviousTaskId(int vehiclePreviousTaskId) {
    this.vehiclePreviousTaskId = vehiclePreviousTaskId;
  }
  
  public int getVehicleNextTaskId() {
    return vehicleNextTaskId;
  }
  
  public void setVehicleNextTaskId(int vehicleNextTaskId) {
    this.vehicleNextTaskId = vehicleNextTaskId;
  }
  
  public TaskChangeReason getChangeReason() {
    return changeReason;
  }
  
  public void setChangeReason(TaskChangeReason changeReason) {
    this.changeReason = changeReason;
  }
  
  public int getOrderNo() {
    return orderNo;
  }
  
  public void setOrderNo(int orderNo) {
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
  
  public int getKundenNr() {
    return kundenNr;
  }
  
  public void setKundenNr(int kundenNr) {
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
  
  public int getPalletsAmount() {
    return palletsAmount;
  }
  
  public void setPalletsAmount(int palletsAmount) {
    this.palletsAmount = palletsAmount;
  }
  
  public List<ActivityItem> getActivities() {
    return activities;
  }
}
