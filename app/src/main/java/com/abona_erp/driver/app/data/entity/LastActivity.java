package com.abona_erp.driver.app.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.abona_erp.driver.app.data.converters.TimestampConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@Entity(tableName = "last_activity_table")
public class LastActivity implements Serializable {

  @PrimaryKey(autoGenerate = true)
  private int id;
  
  @ColumnInfo(name = "task_id")
  private int taskId;
  
  @ColumnInfo(name = "client_id")
  private int clientId;
  
  @ColumnInfo(name = "customer")
  private String customer;
  
  @ColumnInfo(name = "order_no")
  private String orderNo;
  
  @ColumnInfo(name = "detail_list")
  private ArrayList<String> detailList = new ArrayList<>();
  
  @ColumnInfo(name = "status_type")
  private int statusType;
  
  @ColumnInfo(name = "confirm_status")
  private int confirmStatus;
  
  @ColumnInfo(name = "created_at")
  @TypeConverters({TimestampConverter.class})
  private Date createdAt;
  
  @ColumnInfo(name = "modified_at")
  @TypeConverters({TimestampConverter.class})
  private Date modifiedAt;

  // ------------------------------------------------------------------------
  // GETTER SETTER
  
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
  
  public int getTaskId() {
    return taskId;
  }
  
  public void setTaskId(int taskId) {
    this.taskId = taskId;
  }
  
  public int getClientId() {
    return clientId;
  }
  
  public void setClientId(int clientId) {
    this.clientId = clientId;
  }
  
  public String getCustomer() {
    return customer;
  }
  
  public void setCustomer(String customer) {
    this.customer = customer;
  }
  
  public String getOrderNo() {
    return orderNo;
  }
  
  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }
  
  public int getStatusType() {
    return statusType;
  }
  
  public void setStatusType(int statusType) {
    this.statusType = statusType;
  }
  
  public ArrayList<String> getDetailList() {
    return detailList;
  }
  
  public void setDetailList(ArrayList<String> detailList) {
    this.detailList = detailList;
  }
  
  public int getConfirmStatus() {
    return confirmStatus;
  }
  
  public void setConfirmStatus(int confirmStatus) {
    this.confirmStatus = confirmStatus;
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
