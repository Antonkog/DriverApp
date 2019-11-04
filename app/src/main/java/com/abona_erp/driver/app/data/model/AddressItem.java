package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressItem {
  
  @SerializedName("Name1")
  @Expose
  private String name1;
  
  @SerializedName("Name2")
  @Expose
  private String name2;
  
  @SerializedName("Street")
  @Expose
  private String street;
  
  @SerializedName("ZIP")
  @Expose
  private String zip;
  
  @SerializedName("City")
  @Expose
  private String city;
  
  @SerializedName("State")
  @Expose
  private String state;
  
  @SerializedName("Nation")
  @Expose
  private String nation;
  
  @SerializedName("Longitude")
  @Expose
  private Double longitude;
  
  @SerializedName("Latitude")
  @Expose
  private Double latitude;
  
  @SerializedName("Note")
  @Expose
  private String note;
  
  public String getName1() {
    return name1;
  }
  
  public void setName1(String name1) {
    this.name1 = name1;
  }
  
  public String getName2() {
    return name2;
  }
  
  public void setName2(String name2) {
    this.name2 = name2;
  }
  
  public String getStreet() {
    return street;
  }
  
  public void setStreet(String street) {
    this.street = street;
  }
  
  public String getZip() {
    return zip;
  }
  
  public void setZip(String zip) {
    this.zip = zip;
  }
  
  public String getCity() {
    return city;
  }
  
  public void setCity(String city) {
    this.city = city;
  }
  
  public String getState() {
    return state;
  }
  
  public void setState(String state) {
    this.state = state;
  }
  
  public String getNation() {
    return nation;
  }
  
  public void setNation(String nation) {
    this.nation = nation;
  }
  
  public Double getLongitude() {
    return longitude;
  }
  
  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }
  
  public Double getLatitude() {
    return latitude;
  }
  
  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }
  
  public String getNote() {
    return note;
  }
  
  public void setNote(String note) {
    this.note = note;
  }
}
