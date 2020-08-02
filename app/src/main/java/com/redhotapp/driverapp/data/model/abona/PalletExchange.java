package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PalletExchange {
  
  @SerializedName("ExchangeType")
  @Expose
  private EnumPalletExchangeType exchangeType;
  
  @SerializedName("PalletsAmount")
  @Expose
  private Integer palletsAmount;
  
  @SerializedName("IsDPL")
  @Expose
  private Boolean isDPL;
  
  public EnumPalletExchangeType getPalletExchangeType() {
    return exchangeType;
  }
  
  public void setPalletExchangeType(EnumPalletExchangeType exchangeType) {
    this.exchangeType = exchangeType;
  }
  
  public Integer getPalletsAmount() {
    return palletsAmount;
  }
  
  public void setPalletsAmount(Integer palletsAmount) {
    this.palletsAmount = palletsAmount;
  }
  
  public Boolean isDPL() {
    return isDPL;
  }
  
  public void setDPL(Boolean isDPL) {
    this.isDPL = isDPL;
  }
}
