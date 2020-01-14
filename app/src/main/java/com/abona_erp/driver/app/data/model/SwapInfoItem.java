package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SwapInfoItem {
  
  //@SerializedName("SwapTaskItem")
  //@Expose
  //private TaskItem swapTaskItem;
  
  @SerializedName("SwapVehicleItem")
  @Expose
  private VehicleItem vehicleItem;
  
  //public TaskItem getSwapTaskItem() {
  //  return swapTaskItem;
  //}
  
  //public void setSwapTaskItem(TaskItem swapTaskItem) {
  //  this.swapTaskItem = swapTaskItem;
  //}
  
  public VehicleItem getVehicleItem() {
    return vehicleItem;
  }
  
  public void setVehicleItem(VehicleItem swapVehicleItem) {
    this.vehicleItem = vehicleItem;
  }
}
