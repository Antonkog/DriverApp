package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TokenResponse implements Serializable
{

@SerializedName("access_token")
@Expose
private String accessToken;
@SerializedName("token_type")
@Expose
private String tokenType;
@SerializedName("expires_in")
@Expose
private Integer expiresIn;
@SerializedName("refresh_token")
@Expose
private String refreshToken;
@SerializedName("as:client_id")
@Expose
private String asClientId;
@SerializedName("refresh_token.expires")
@Expose
private String refreshTokenExpires;
@SerializedName(".issued")
@Expose
private String issued;
@SerializedName(".expires")
@Expose
private String expires;


public String getAccessToken() {
return accessToken;
}

public void setAccessToken(String accessToken) {
this.accessToken = accessToken;
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

public String getRefreshToken() {
return refreshToken;
}

public void setRefreshToken(String refreshToken) {
this.refreshToken = refreshToken;
}

public String getAsClientId() {
return asClientId;
}

public void setAsClientId(String asClientId) {
this.asClientId = asClientId;
}

public String getRefreshTokenExpires() {
return refreshTokenExpires;
}

public void setRefreshTokenExpires(String refreshTokenExpires) {
this.refreshTokenExpires = refreshTokenExpires;
}

public String getIssued() {
return issued;
}

public void setIssued(String issued) {
this.issued = issued;
}

public String getExpires() {
return expires;
}

public void setExpires(String expires) {
this.expires = expires;
}

    @Override
    public String toString() {
        return "TokenResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", refreshToken='" + refreshToken + '\'' +
                ", asClientId='" + asClientId + '\'' +
                ", refreshTokenExpires='" + refreshTokenExpires + '\'' +
                ", issued='" + issued + '\'' +
                ", expires='" + expires + '\'' +
                '}';
    }
}