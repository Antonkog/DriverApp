package com.abona_erp.driver.app.ui.event;


public class TaskStatusEvent implements BaseEvent {
  
  private int mPercentage;
  
  public TaskStatusEvent(int percentage) {
    mPercentage = percentage;
  }
  
  public int getPercentage() {
    return mPercentage;
  }
  
  public void setPercentage(int percentage) {
    mPercentage = percentage;
  }
}
