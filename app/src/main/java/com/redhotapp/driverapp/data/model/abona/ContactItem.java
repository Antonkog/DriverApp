package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactItem {
  
  @SerializedName("ContactType")
  @Expose
  private EnumContactType contactType;
  
  @SerializedName("NumberType")
  @Expose
  private EnumNumberType numberType;
  
  @SerializedName("Name")
  @Expose
  private String name;
  
  @SerializedName("Number")
  @Expose
  private String number;
  
  public EnumContactType getContactType() {
    return contactType;
  }
  
  public void setContactType(EnumContactType contactType) {
    this.contactType = contactType;
  }
  
  public EnumNumberType getNumberType() {
    return numberType;
  }
  
  public void setNumberType(EnumNumberType numberType) {
    this.numberType = numberType;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getNumber() {
    return number;
  }
  
  public void setNumber(String number) {
    this.number = number;
  }
}
