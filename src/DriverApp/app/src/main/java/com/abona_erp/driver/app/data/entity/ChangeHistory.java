package com.abona_erp.driver.app.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.abona_erp.driver.app.data.converters.DateConverter;
import com.abona_erp.driver.app.data.converters.LogType;

import java.util.Date;

@Entity(tableName = "change_history")
@TypeConverters({ChangeHistoryState.class, ActionType.class, LogType.class, DateConverter.class})
public class ChangeHistory /*implements Serializable*/ {
  
  @PrimaryKey(autoGenerate = true)
  private int id;
  
  @ColumnInfo(name = "title") // driving to loading
  private String title;
  
  @ColumnInfo(name = "message")
  private String message;
  
  @ColumnInfo(name = "direction")
  private LogType type; //app to server etc.

  @ColumnInfo(name = "action_type")
  private ActionType actionType;

  @ColumnInfo(name = "state")
  private ChangeHistoryState state;

  @ColumnInfo(name = "created_at")
  private Date createdAt;

  @ColumnInfo(name = "modified_at")
  private Date modifiedAt;

  @ColumnInfo(name = "modified_long")
  private long timeModifyLong;

  @ColumnInfo(name = "task_id")
  private int taskId;

  @ColumnInfo(name = "activity_id")
  private int activityId;

  @ColumnInfo(name = "order_number")
  private int orderNumber;

  @ColumnInfo(name = "mandant_id")
  private int mandantID;

  @ColumnInfo(name = "confirmation_id")
  private int offlineConfirmationID;

  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }

  public Integer getTaskId() {
    return taskId;
  }

  public void setTaskId(Integer taskId) {
    this.taskId = taskId;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getMessage() {
    return message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public LogType getType() {
    return type;
  }
  
  public void setType(LogType type) {
    this.type = type;
  }
  
  public Date getCreatedAt() {
    return createdAt;
  }
  
  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public void setActionType(ActionType actionType) {
    this.actionType = actionType;
  }

  public void setState(ChangeHistoryState state) {
    this.state = state;
  }

  public void setModifiedAt(Date modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

  public void setActivityId(int activityId) {
    this.activityId = activityId;
  }

  public void setOrderNumber(int orderNumber) {
    this.orderNumber = orderNumber;
  }

  public void setMandantID(int mandantID) {
    this.mandantID = mandantID;
  }

  public void setOfflineConfirmationID(int offlineConfirmationID) {
    this.offlineConfirmationID = offlineConfirmationID;
  }

  public void setTimeModifyLong(long timeModifyLong) {
    this.timeModifyLong = timeModifyLong;
  }

  public long getTimeModifyLong() {
    return timeModifyLong;
  }

  public ActionType getActionType() {
    return actionType;
  }

  public ChangeHistoryState getState() {
    return state;
  }

  public Date getModifiedAt() {
    return modifiedAt;
  }

  public int getActivityId() {
    return activityId;
  }

  public int getOrderNumber() {
    return orderNumber;
  }

  public int getMandantID() {
    return mandantID;
  }

  public int getOfflineConfirmationID() {
    return offlineConfirmationID;
  }
}
