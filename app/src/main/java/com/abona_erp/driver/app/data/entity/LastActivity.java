package com.abona_erp.driver.app.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.abona_erp.driver.app.util.DateConverter;

import java.util.Date;

@Entity(tableName = "last_activity_table")
public class LastActivity {

  @PrimaryKey(autoGenerate = true)
  private int id;

  @NonNull
  @ColumnInfo(name = "status_name")
  private int statusType;
  
  @NonNull
  @ColumnInfo(name = "task_oid")
  private int taskOid;
  
  @NonNull
  @ColumnInfo(name = "mandant_oid")
  private int mandantOid;

  @NonNull
  @ColumnInfo(name = "order_no")
  private int orderNo;
  
  @ColumnInfo(name = "description")
  private String description;

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

  @NonNull
  public int getStatusType() {
    return statusType;
  }

  public void setStatusType(@NonNull int statusType) {
    this.statusType = statusType;
  }

  @NonNull
  public int getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(@NonNull int orderNo) {
    this.orderNo = orderNo;
  }
  
  @NonNull
  public int getMandantOid() {
    return mandantOid;
  }
  
  public void setMandantOid(@NonNull int mandantOid) {
    this.mandantOid = mandantOid;
  }
  
  @NonNull
  public int getTaskOid() {
    return taskOid;
  }
  
  public void setTaskOid(@NonNull int taskOid) {
    this.taskOid = taskOid;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }

  @NonNull
  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(@NonNull Date createdAt) {
    this.createdAt = createdAt;
  }

  @NonNull
  public Date getModifiedAt() {
    return modifiedAt;
  }

  public void setModifiedAt(@NonNull Date modifiedAt) {
    this.modifiedAt = modifiedAt;
  }
}
