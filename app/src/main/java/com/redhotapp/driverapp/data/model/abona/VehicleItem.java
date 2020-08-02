package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VehicleItem {
  
  @SerializedName("MandantId")
  @Expose
  private Integer mandantId;
  
  @SerializedName("ClientName")
  @Expose
  private String clientName;
  
  @SerializedName("RegistrationNumber")
  @Expose
  private String registrationNumber;
  
  @SerializedName("Drivers")
  @Expose
  private List<DriverItem> drivers;
  
  // ------------------------------------------------------------------------
  // GETTER & SETTER
  
  public Integer getMandantId() {
    return mandantId;
  }
  
  public void setMandantId(Integer mandantId) {
    this.mandantId = mandantId;
  }
  
  public String getClientName() {
    return clientName;
  }
  
  public void setClientName(String clientName) {
    this.clientName = clientName;
  }
  
  public String getRegistrationNumber() {
    return registrationNumber;
  }
  
  public void setRegistrationNumber(String registrationNumber) {
    this.registrationNumber = registrationNumber;
  }
  
  public List<DriverItem> getDrivers() {
    return drivers;
  }
  
  public void setDrivers(List<DriverItem> drivers) {
    this.drivers = drivers;
  }
}
