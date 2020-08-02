package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DangerousGoods {
  
  @SerializedName("IsGoodsDangerous")
  @Expose
  private Boolean isGoodsDangerous;
  
  @SerializedName("ADRClass")
  @Expose
  private String adrClass;
  
  @SerializedName("DangerousGoodsClassType")
  @Expose
  private DangerousGoodsClass dangerousGoodsClassType;
  
  @SerializedName("UNNo")
  @Expose
  private String unNo;
  
  public Boolean isGoodsDangerous() {
    return isGoodsDangerous;
  }
  
  public void setGoodsDangerous(Boolean isGoodsDangerous) {
    this.isGoodsDangerous = isGoodsDangerous;
  }
  
  public String getAdrClass() {
    return adrClass;
  }
  
  public void setAdrClass(String adrClass) {
    this.adrClass = adrClass;
  }
  
  public DangerousGoodsClass getDangerousGoodsClassType() {
    return dangerousGoodsClassType;
  }
  
  public void setDangerousGoodsClassType(DangerousGoodsClass dangerousGoodsClassType) {
    this.dangerousGoodsClassType = dangerousGoodsClassType;
  }
  
  public String getUnNo() {
    return unNo;
  }
  
  public void setUnNo(String unNo) {
    this.unNo = unNo;
  }
}
