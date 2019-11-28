package com.abona_erp.driver.app.ui.event;

import com.abona_erp.driver.app.data.entity.Notify;

public class CameraEvent implements BaseEvent {
  
  private Notify mNotify;
  
  public CameraEvent(Notify notify) {
    mNotify = notify;
  }
  
  public Notify getNotify() {
    return mNotify;
  }
  
  public void setNotify(Notify notify) {
    mNotify = notify;
  }
}
