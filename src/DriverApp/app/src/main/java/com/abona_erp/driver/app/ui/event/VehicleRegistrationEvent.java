package com.abona_erp.driver.app.ui.event;

import com.abona_erp.driver.app.data.model.VehicleItem;

public class VehicleRegistrationEvent implements BaseEvent {


  private VehicleItem vehicleItem;

  public VehicleRegistrationEvent(VehicleItem vehicleItem) {
    this.vehicleItem = vehicleItem;
  }

  public VehicleItem getVehicleItem() {
    return vehicleItem;
  }
}
