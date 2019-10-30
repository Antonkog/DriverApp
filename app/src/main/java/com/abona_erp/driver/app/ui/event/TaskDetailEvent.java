package com.abona_erp.driver.app.ui.event;

import com.abona_erp.driver.app.data.entity.Notify;

public class TaskDetailEvent implements BaseEvent {
  
  Notify notify;
  
  public TaskDetailEvent(Notify notify) {
    this.notify = notify;
  }
  
  public Notify getNotify() {
    return notify;
  }
  
  public void setNotify(Notify notify) {
    this.notify = notify;
  }
}
