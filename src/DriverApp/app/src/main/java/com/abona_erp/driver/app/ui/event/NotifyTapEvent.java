package com.abona_erp.driver.app.ui.event;

public class NotifyTapEvent implements BaseEvent {
  boolean isOpen;
  int taskId;

  public NotifyTapEvent(boolean isOpen, int taskId) {
    this.isOpen = isOpen;
    this.taskId = taskId;
  }

  public boolean isOpen() {
    return isOpen;
  }

  public int getTaskId() {
    return taskId;
  }

  @Override
  public String toString() {
    return "NotifyTapEvent{" +
            "isOpen=" + isOpen +
            ", taskId=" + taskId +
            '}';
  }
}
