package com.abona_erp.driver.app.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.util.BoolConverter;
import com.abona_erp.driver.app.util.DateConverter;
import com.abona_erp.driver.app.util.DateConverterWithoutUTC;

import java.util.ArrayList;
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
  
  @ColumnInfo(name = "percent_finished")
  private int percentFinished;
  
  @ColumnInfo(name = "photo_urls")
  private ArrayList<String> photoUrls = new ArrayList<>();
  
  @ColumnInfo(name = "document_urls")
  private ArrayList<String> documentUrls = new ArrayList<>();
  
  @ColumnInfo(name = "task_due_finish")
  @TypeConverters({DateConverterWithoutUTC.class})
  private Date taskDueFinish;
  
  @ColumnInfo(name = "created_at")
  @TypeConverters({DateConverter.class})
  private Date createdAt;
  
  @ColumnInfo(name = "modified_at")
  @TypeConverters({DateConverter.class})
  private Date modifiedAt;

  @ColumnInfo(name = "currently_selected")
  @TypeConverters({BoolConverter.class})
  private boolean currentlySelected;
  
  @ColumnInfo(name = "confirmation_status")
  private int confirmationStatus;
  
  public int getConfirmationStatus() {
    return this.confirmationStatus;
  }
  
  public void setConfirmationStatus(int confirmationStatus) {
    this.confirmationStatus = confirmationStatus;
  }

  public boolean isCurrentlySelected() {
    return currentlySelected;
  }

  public void setCurrentlySelected(boolean currentlySelected) {
    this.currentlySelected = currentlySelected;
  }


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

  public void setStatus(TaskStatus status) {
    switch (status) {
      case PENDING:
        setStatus(0);
        break;
      case RUNNING:
        setStatus(50);
      case BREAK:
        setStatus(51);
        break;
      case CMR:
        setStatus(90);
        break;
      case FINISHED:
        setStatus(100);
        break;
    }
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
  
  public int getPercentFinished() {
    return percentFinished;
  }
  
  public void setPercentFinished(int percentFinished) {
    this.percentFinished = percentFinished;
  }
  
  public ArrayList<String> getPhotoUrls() {
    return photoUrls;
  }
  
  public void setPhotoUrls(ArrayList<String> photoUrls) {
    this.photoUrls = photoUrls;
  }
  
  public ArrayList<String> getDocumentUrls() {
    return documentUrls;
  }
  
  public void setDocumentUrls(ArrayList<String> documentUrls) {
    this.documentUrls = documentUrls;
  }
  
  public Date getCreatedAt() {
    return createdAt;
  }
  
  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public String toString() {
    return "Notify{" +
            "id=" + id +
            ", data='" + data + '\'' +
            ", read=" + read +
            ", status=" + status +
            ", orderNo=" + orderNo +
            ", mandantId=" + mandantId +
            ", taskId=" + taskId +
            ", percentFinished=" + percentFinished +
            ", photoUrls=" + photoUrls +
            ", documentUrls=" + documentUrls +
            ", taskDueFinish=" + taskDueFinish +
            ", createdAt=" + createdAt +
            ", modifiedAt=" + modifiedAt +
            ", currentlySelected=" + currentlySelected +
            '}';
  }

  public Date getModifiedAt() {
    return modifiedAt;
  }
  
  public void setModifiedAt(Date modifiedAt) {
    this.modifiedAt = modifiedAt;
  }
}
