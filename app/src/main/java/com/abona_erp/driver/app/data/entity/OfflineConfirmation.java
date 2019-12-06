package com.abona_erp.driver.app.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "offline_confirmation")
public class OfflineConfirmation {
  
  @PrimaryKey(autoGenerate = true)
  private int id;
  
  @ColumnInfo(name = "notify_id")
  private int notifyId;
  
  @ColumnInfo(name = "activity_id")
  private int activityId;
  
  @ColumnInfo(name = "confirm_type")
  private int confirmType;
  
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
  
  public int getActivityId() {
    return activityId;
  }
  
  public void setActivityId(int activityId) {
    this.activityId = activityId;
  }
  
  public int getConfirmType() {
    return confirmType;
  }
  
  public void setConfirmType(int confirmType) {
    this.confirmType = confirmType;
  }
}
