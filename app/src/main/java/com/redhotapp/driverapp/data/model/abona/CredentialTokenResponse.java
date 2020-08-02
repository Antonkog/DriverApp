package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CredentialTokenResponse {
  
  @SerializedName("access_token")
  @Expose
  private String accssToken;
  
  @SerializedName("token_type")
  @Expose
  private String tokenType;
  
  @SerializedName("expires_in")
  @Expose
  private Integer expiresIn;
  
  public String getAccssToken() {
    return accssToken;
  }
  
  public void setAccessToken(String accessToken) {
    this.accssToken = accessToken;
  }
  
  public String getTokenType() {
    return tokenType;
  }
  
  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }
  
  public Integer getExpiresIn() {
    return expiresIn;
  }
  
  public void setExpiresIn(Integer expiresIn) {
    this.expiresIn = expiresIn;
  }
}
