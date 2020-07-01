package com.abona_erp.driver.app.ui.event;

public class ConnectivityEvent implements BaseEvent {

  private boolean isConnected;

  public boolean isConnected() {
    return isConnected;
  }

  public ConnectivityEvent(boolean isConnected) {
    this.isConnected = isConnected;
  }
}
