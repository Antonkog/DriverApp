package com.abona_erp.driver.app.ui.event;

public class QREvent implements BaseEvent {
  
  private int mNotifyId;
  private int mActivityId;
  
  public QREvent(int notifyId, int activityId) {
    this.mNotifyId = notifyId;
    this.mActivityId = activityId;
  }
  
  public int getNotifyId() {
    return mNotifyId;
  }
  
  public void setNotifyId(int notifyId) {
    this.mNotifyId = notifyId;
  }
  
  public int getActivityId() {
    return mActivityId;
  }
  
  public void setActivityId(int activityId) {
    this.mActivityId = activityId;
  }
}
