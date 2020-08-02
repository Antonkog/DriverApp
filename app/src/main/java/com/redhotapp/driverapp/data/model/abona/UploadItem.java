package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UploadItem {
  
  @SerializedName("Uri")
  @Expose
  private String uri;
  
  @SerializedName("Uploaded")
  @Expose
  private boolean uploaded;
  
  @SerializedName("DMSDocumentType")
  @Expose
  private DMSDocumentType documentType;
  
  @SerializedName("CreatedAt")
  @Expose
  private Date createdAt;
  
  @SerializedName("ModifiedAt")
  @Expose
  private Date modifiedAt;
  
  public String getUri() {
    return uri;
  }
  
  public void setUri(String uri) {
    this.uri = uri;
  }
  
  public boolean getUploaded() {
    return uploaded;
  }
  
  public void setUploaded(boolean uploaded) {
    this.uploaded = uploaded;
  }
  
  public DMSDocumentType getDocumentType() {
    return documentType;
  }
  
  public void setDocumentType(DMSDocumentType documentType) {
    this.documentType = documentType;
  }
  
  public Date getCreatedAt() {
    return createdAt;
  }
  
  public void setCreated(Date createdAt) {
    this.createdAt = createdAt;
  }
  
  public Date getModifiedAt() {
    return modifiedAt;
  }
  
  public void setModifiedAt(Date modifiedAt) {
    this.modifiedAt = modifiedAt;
  }
}
