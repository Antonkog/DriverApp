package com.abona_erp.driver.app.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.TypeConverters;

import com.abona_erp.driver.app.data.converters.TimestampConverter;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "delay_reason_table", primaryKeys = {"MandantId", "ActivityId", "WaitingReasonId"})
public class DelayReasonEntity implements Serializable {
  
  @Ignore
  private static final String TAG = DelayReasonEntity.class.getSimpleName();
  
  @ColumnInfo(name = "MandantId")
  private int mandantId;
  
  @ColumnInfo(name = "ActivityId")
  private int activityId;
  
  @ColumnInfo(name = "WaitingReasonId")
  private int waitingReasonId;
  
  @ColumnInfo(name = "ReasonText")
  private String reasonText;
  
  @ColumnInfo(name = "TranslatedReasonText")
  private String translatedReasonText;
  
  @ColumnInfo(name = "Code")
  private int code;
  
  @ColumnInfo(name = "SubCode")
  private int subCode;
  
  @ColumnInfo(name = "CreatedAt")
  @TypeConverters({TimestampConverter.class})
  private Date createdAt;
  
  @ColumnInfo(name = "ModifiedAt")
  @TypeConverters({TimestampConverter.class})
  private Date modifiedAt;
  
  public int getMandantId() {
    return this.mandantId;
  }
  
  public void setMandantId(int mandantId) {
    this.mandantId = mandantId;
  }
  
  public int getActivityId() {
    return this.activityId;
  }
  
  public void setActivityId(int activityId) {
    this.activityId = activityId;
  }
  
  public int getWaitingReasonId() {
    return this.waitingReasonId;
  }
  
  public void setWaitingReasonId(int waitingReasonId) {
    this.waitingReasonId = waitingReasonId;
  }
  
  public String getReasonText() {
    return this.reasonText;
  }
  
  public void setReasonText(String reasonText) {
    this.reasonText = reasonText;
  }
  
  public String getTranslatedReasonText() {
    return this.translatedReasonText;
  }
  
  public void setTranslatedReasonText(String translatedReasonText) {
    this.translatedReasonText = translatedReasonText;
  }
  
  public int getCode() {
    return this.code;
  }
  
  public void setCode(int code) {
    this.code = code;
  }
  
  public int getSubCode() {
    return this.subCode;
  }
  
  public void setSubCode(int subCode) {
    this.subCode = subCode;
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
  
  @Override
  public String toString() {
    return "DelayReason";
  }
}
