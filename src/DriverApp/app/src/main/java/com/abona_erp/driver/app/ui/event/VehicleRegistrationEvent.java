package com.abona_erp.driver.app.ui.event;

public class VehicleRegistrationEvent implements BaseEvent {
  
  private boolean deleteAll;
  
  public VehicleRegistrationEvent() {
  }
  
  public boolean isDeleteAll() {
    return deleteAll;
  }
  
  public void setDeleteAll(boolean isDeleteAll) {
    this.deleteAll = isDeleteAll;
  }
}
