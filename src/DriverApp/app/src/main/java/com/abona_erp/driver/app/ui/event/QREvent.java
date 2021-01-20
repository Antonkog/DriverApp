package com.abona_erp.driver.app.ui.event;

public class QREvent implements BaseEvent {
  
  private int mNotifyId;
  private int mActivityId;
  private int mType;   // 0 = QR, 1 = PHOTO
  
  public QREvent(int notifyId, int activityId, int type) {
    this.mNotifyId = notifyId;
    this.mActivityId = activityId;
    this.mType = type;
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
  
  public int getType() {
    return mType;
  }
  
  public void setType(int type) {
    this.mType = type;
  }
}
