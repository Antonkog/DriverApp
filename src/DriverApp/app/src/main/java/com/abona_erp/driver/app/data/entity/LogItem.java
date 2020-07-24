package com.abona_erp.driver.app.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.abona_erp.driver.app.data.converters.LogLevel;
import com.abona_erp.driver.app.data.converters.LogType;
import com.abona_erp.driver.app.data.converters.DateConverter;

import java.util.Date;

@Entity(tableName = "logItem")
@TypeConverters({LogLevel.class, LogType.class, DateConverter.class})
public class LogItem /*implements Serializable*/ {
  
  @PrimaryKey(autoGenerate = true)
  private int id;
  
  @ColumnInfo(name = "level")
  private LogLevel level;
  
  @ColumnInfo(name = "title")
  private String title;
  
  @ColumnInfo(name = "message")
  private String message;
  
  @ColumnInfo(name = "type")
  private LogType type;
  
  @ColumnInfo(name = "created_at")
  private Date createdAt;
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public LogLevel getLevel() {
    return level;
  }
  
  public void setLevel(LogLevel level) {
    this.level = level;
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
}
