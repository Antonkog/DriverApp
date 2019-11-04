package com.abona_erp.driver.app.ui.event;

import com.abona_erp.driver.app.data.entity.Notify;

public class MapEvent implements BaseEvent {

  private Notify mNotify;

  public MapEvent(Notify notify) {
    mNotify = notify;
  }

  public Notify getNotify() {
    return mNotify;
  }

  public void setNotify(Notify notify) {
    mNotify = notify;
  }
}
