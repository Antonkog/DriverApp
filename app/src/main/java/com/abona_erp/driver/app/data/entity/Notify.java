package com.abona_erp.driver.app.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.abona_erp.driver.app.util.DateConverter;

import java.util.Date;

@Entity(tableName = "taskItem")
public class Notify {
  
  @PrimaryKey(autoGenerate = true)
  private int id;
  
  @ColumnInfo(name = "data")
  private String data;
  
  @ColumnInfo(name = "read")
  private boolean read;
  
  @ColumnInfo(name = "status")
  private int status;
  
  @ColumnInfo(name = "order_no")
  private int orderNo;
  
  @ColumnInfo(name = "mandant_id")
  private int mandantId;
  
  @ColumnInfo(name = "task_id")
  private int taskId;
  
  @ColumnInfo(name = "task_due_finish")
  @TypeConverters({DateConverter.class})
  private Date taskDueFinish;
  
  @ColumnInfo(name = "created_at")
  @TypeConverters({DateConverter.class})
  private Date createdAt;
  
  @ColumnInfo(name = "modified_at")
  @TypeConverters({DateConverter.class})
  private Date modifiedAt;
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public String getData() {
    return data;
  }
  
  public void setData(String data) {
    this.data = data;
  }
  
  public boolean getRead() {
    return read;
  }
  
  public void setRead(boolean read) {
    this.read = read;
  }
  
  public int getStatus() {
    return status;
  }
  
  public void setStatus(int status) {
    this.status = status;
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
  
  public int getOrderNo() {
    return orderNo;
  }
  
  public void setOrderNo(int orderNo) {
    this.orderNo = orderNo;
  }
  
  public Date getTaskDueFinish() {
    return taskDueFinish;
  }
  
  public void setTaskDueFinish(Date taskDueFinish) {
    this.taskDueFinish = taskDueFinish;
  }
  
  public Date getCreatedAt() {
    return createdAt;
  }
  
  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }
  
  public Date getModifiedAt() {
    return modifiedAt;
  }
  
  public void setModifiedAt(Date modifiedAt) {
    this.modifiedAt = modifiedAt;
  }
}
