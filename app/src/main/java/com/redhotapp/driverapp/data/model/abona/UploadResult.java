package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadResult {
  
  @SerializedName("FileName")
  @Expose
  private String filename;
  
  @SerializedName("IsUploadSuccess")
  @Expose
  private boolean isUploadSuccess;
  
  @SerializedName("IsDownloadSuccess")
  @Expose
  private boolean isDownloadSuccess;
  
  @SerializedName("ErrorText")
  @Expose
  private String errorText;
  
  public String getFileName() {
    return filename;
  }
  
  public void setFileName(String filename) {
    this.filename = filename;
  }
  
  public boolean isUploadSuccess() {
    return isUploadSuccess;
  }
  
  public void setUploadSuccess(boolean isUploadSuccess) {
    this.isUploadSuccess = isUploadSuccess;
  }
  
  public boolean isDownloadSuccess() {
    return isDownloadSuccess;
  }
  
  public void setDownloadSuccess(boolean isDownloadSuccess) {
    this.isDownloadSuccess = isDownloadSuccess;
  }
  
  public String getErrorText() {
    return errorText;
  }
  
  public void setErrorText(String errorText) {
    this.errorText = errorText;
  }
}
