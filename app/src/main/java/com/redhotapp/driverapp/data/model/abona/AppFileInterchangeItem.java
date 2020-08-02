package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class AppFileInterchangeItem {
  
  @SerializedName("LinkToFile")
  @Expose
  private String linkToFile;
  
  @SerializedName("SourceReference")
  @Expose
  private SourceReference sourceReference;
  
  @SerializedName("AddedDate")
  @Expose
  private Date addedDate;
  
  @SerializedName("AddedUser")
  @Expose
  private String addedUser;
  
  @SerializedName("FileType")
  @Expose
  private AppFileType fileType;
  
  @SerializedName("FileName")
  @Expose
  private String fileName;
  
  @SerializedName("Thumbnail")
  @Expose
  private String thumbnail;
  
  @SerializedName("TaskId")
  @Expose
  private Integer taskId;
  
  public String getLinkToFile() {
    return linkToFile;
  }
  
  public void setLinkToFile(String linkToFile) {
    this.linkToFile = linkToFile;
  }
  
  public SourceReference getSourceReference() {
    return sourceReference;
  }
  
  public void setSourceReference(SourceReference sourceReference) {
    this.sourceReference = sourceReference;
  }
  
  public Date getAddedDate() {
    return addedDate;
  }
  
  public void setAddedDate(Date addedDate) {
    this.addedDate = addedDate;
  }
  
  public String getAddedUser() {
    return addedUser;
  }
  
  public void setAddedUser(String addedUser) {
    this.addedUser = addedUser;
  }
  
  public AppFileType getFileType() {
    return fileType;
  }
  
  public void setFileType(AppFileType fileType) {
    this.fileType = fileType;
  }
  
  public String getFileName() {
    return fileName;
  }
  
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  
  public String getThumbnail() {
    return thumbnail;
  }
  
  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }
  
  public Integer getTaskId() {
    return taskId;
  }
  
  public void setTaskId(Integer taskId) {
    this.taskId = taskId;
  }
}
