package com.abona_erp.driver.app.ui.event;

public class ConnectivityEvent implements BaseEvent {
  
  private int mConnectivityStatus;

  public ConnectivityEvent(final int connectivityStatus) {
    this.mConnectivityStatus = connectivityStatus;
  }
  
  public int getConnectivityStatus() {
    return mConnectivityStatus;
  }
}
