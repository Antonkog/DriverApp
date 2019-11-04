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
  private String statusName;

  @NonNull
  @ColumnInfo(name = "order_no")
  private int orderNo;

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
  public String getStatusName() {
    return statusName;
  }

  public void setStatusName(@NonNull String statusName) {
    this.statusName = statusName;
  }

  @NonNull
  public int getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(@NonNull int orderNo) {
    this.orderNo = orderNo;
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
