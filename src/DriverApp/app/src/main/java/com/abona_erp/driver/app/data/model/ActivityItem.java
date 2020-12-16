package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class ActivityItem {
  
  @SerializedName("MandantId")
  @Expose
  private Integer mandantId;
  
  @SerializedName("TaskId")
  @Expose
  private Integer taskId;
  
  @SerializedName("ActivityId")
  @Expose
  private Integer activityId;
  
  @SerializedName("Name")
  @Expose
  private String name;
  
  @SerializedName("Description")
  @Expose
  private String description;
  
  @SerializedName("Started")
  @Expose
  //@TypeConverters(DateConverterUTC.class)
  private Date started;
  
  @SerializedName("Finished")
  @Expose
  //@TypeConverters(DateConverterUTC.class)
  private Date finished;
  
  @SerializedName("Status")
  @Expose
  private ActivityStatus status;
  
  @SerializedName("Sequence")
  @Expose
  private Integer sequence;
  
  @SerializedName("DeviceId")
  @Expose
  private String deviceId;
  
  @SerializedName("DelayReasons")
  @Expose
  private List<DelayReasonItem> delayReasonItems;
  
  @SerializedName("SpecialActivities")
  @Expose
  private List<SpecialActivities> specialActivities;
  
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
  
  public Integer getActivityId() {
    return activityId;
  }
  
  public void setActivityId(Integer activityId) {
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
  
  public Integer getSequence() {
    return sequence;
  }
  
  public void setSequence(Integer sequence) {
    this.sequence = sequence;
  }
  
  public String getDeviceId() {
    return deviceId;
  }
  
  public void setDeviceId(String deviceInstanceId) {
    this.deviceId = deviceInstanceId;
  }
  
  public List<DelayReasonItem> getDelayReasonItems() {
    return this.delayReasonItems;
  }
  
  public void setDelayReasonItems(List<DelayReasonItem> delayReasonItems) {
    this.delayReasonItems = delayReasonItems;
  }
  
  public List<SpecialActivities> getSpecialActivities() {
    return specialActivities;
  }
  
  public void setSpecialActivities(List<SpecialActivities> specialActivities) {
    this.specialActivities = specialActivities;
  }
}
