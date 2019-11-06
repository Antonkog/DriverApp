package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Header {
  
  public Header() {
    timestampSenderUTC = new Date(System.currentTimeMillis());
    version = 1;
  }
  
  @SerializedName("TimestampSenderUTC")
  @Expose
  private Date timestampSenderUTC;
  
  @SerializedName("Version")
  @Expose
  private int version;
  
  @SerializedName("DataType")
  @Expose
  private DataType dataType;
  
  public Date getTimestampSenderUTC() {
    return timestampSenderUTC;
  }
  
  public void setTimestampSenderUTC(Date timestampSenderUTC) {
    this.timestampSenderUTC = timestampSenderUTC;
  }
  
  public int getVersion() {
    return version;
  }
  
  public void setVersion(int version) {
    this.version = version;
  }
  
  public DataType getDataType() {
    return dataType;
  }
  
  public void setDataType(DataType dataType) {
    this.dataType = dataType;
  }
}
