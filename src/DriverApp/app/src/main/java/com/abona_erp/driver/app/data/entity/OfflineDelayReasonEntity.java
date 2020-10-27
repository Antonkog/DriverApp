package com.abona_erp.driver.app.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.abona_erp.driver.app.data.converters.TimestampConverter;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity(tableName = "offline_delay_reason_entity")
public class OfflineDelayReasonEntity {
  
  @PrimaryKey(autoGenerate = true)
  private int id;
  
  @ColumnInfo(name = "notify_id")
  private int notifyId;
  
  @ColumnInfo(name = "waiting_reason_id")
  private int waitingReasonId;
  
  @ColumnInfo(name = "WaitingReasonAppId")
  private String waitingReasonAppId;
  
  @ColumnInfo(name = "activity_id")
  @NotNull
  private int activityId;
  
  @ColumnInfo(name = "mandant_id")
  @NotNull
  private int mandantId;
  
  @ColumnInfo(name = "task_id")
  private int taskId;
  
  @ColumnInfo(name = "delay_in_minutes")
  @NotNull
  private int delayInMinutes;

  @ColumnInfo(name = "delay_source")
  @NotNull
  private int delaySource;
  
  @ColumnInfo(name = "comment")
  private String comment;
  
  @ColumnInfo(name = "in_progress")
  @NotNull
  private int inProgress;
  
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
  
  public String getWaitingReasonAppId() {
    return this.waitingReasonAppId;
  }
  
  public void setWaitingReasonAppId(String waitingReasonAppId) {
    this.waitingReasonAppId = waitingReasonAppId;
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
  
  public int getInProgress() {
    return inProgress;
  }
  
  public void setInProgress(int inProgress) {
    this.inProgress = inProgress;
  }
}
