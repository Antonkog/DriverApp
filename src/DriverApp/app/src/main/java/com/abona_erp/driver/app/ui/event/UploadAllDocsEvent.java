package com.abona_erp.driver.app.ui.event;

import androidx.annotation.NonNull;

public class UploadAllDocsEvent implements BaseEvent {
  private int taskId;

  public UploadAllDocsEvent(@NonNull int taskId) {
    this.taskId = taskId;
  }

  
  public int getTaskId() {
    return taskId;
  }
  
  public void setTaskId(@NonNull int orderNo) {
    this.taskId = orderNo;
  }
}
