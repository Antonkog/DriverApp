package com.abona_erp.driver.app.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.abona_erp.driver.app.data.converters.TimestampConverter;

import java.util.Date;

@Entity(tableName = "offline_delay_reason_entity")
public class OfflineDelayReasonEntity {
  
  @PrimaryKey(autoGenerate = true)
  private int id;
  
  @ColumnInfo(name = "notify_id")
  private int notifyId;
  
  @ColumnInfo(name = "waiting_reason_id")
  private int waitingReasonId;
  
  @ColumnInfo(name = "activity_id")
  private int activityId;
  
  @ColumnInfo(name = "mandant_id")
  private int mandantId;
  
  @ColumnInfo(name = "task_id")
  private int taskId;
  
  @ColumnInfo(name = "delay_in_minutes")
  private int delayInMinutes;
  
  @ColumnInfo(name = "delay_source")
  private int delaySource;
  
  @ColumnInfo(name = "comment")
  private String comment;
  
  @ColumnInfo(name = "timestamp")
  @TypeConverters({TimestampConverter.class})
  private Date timestamp;
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public int getNotifyId() {
    return notifyId;
  }
  
  public void setNotifyId(int notifyId) {
    this.notifyId = notifyId;
  }
  
  public int getWaitingReasonId() {
    return waitingReasonId;
  }
  
  public void setWaitingReasonId(int waitingReasonId) {
    this.waitingReasonId = waitingReasonId;
  }
  
  public int getActivityId() {
    return activityId;
  }
  
  public void setActivityId(int activityId) {
    this.activityId = activityId;
  }
  
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
  
  public int getDelayInMinutes() {
    return delayInMinutes;
  }
  
  public void setDelayInMinutes(int delayInMinutes) {
    this.delayInMinutes = delayInMinutes;
  }
  
  public int getDelaySource() {
    return delaySource;
  }
  
  public void setDelaySource(int delaySource) {
    this.delaySource = delaySource;
  }
  
  public String getComment() {
    return comment;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public Date getTimestamp() {
    return timestamp;
  }
  
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }
}
