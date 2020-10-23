package com.abona_erp.driver.app.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class DelayReasonItem {
  
  /** WaitingReason.Oid */
  @SerializedName("WaitingReasonId")
  @Expose
  private Integer waitingReasongId;
  
  @SerializedName("WaitingReasonAppId")
  @Expose
  private String waitingReasonAppId;
  
  /** TransportOrderStatusValue.Oid */
  @SerializedName("ActivityId")
  @Expose
  private Integer activityId;
  
  /** WaitingReason.Name */
  @SerializedName("ReasonText")
  @Expose
  private String reasonText;
  
  /** WaitingReason Text */
  @SerializedName("TranslatedReasonText")
  @Expose
  private String translatedReasonText;
  
  /** WaitingReason.Code */
  @SerializedName("Code")
  @Expose
  private Integer code;
  
  /** WaitingReason.SubCode */
  @SerializedName("SubCode")
  @Expose
  private Integer subCode;
  
  /** From driver app */
  @SerializedName("MandantId")
  @Expose
  private Integer mandantId;
  
  /** From driver app */
  @SerializedName("TaskId")
  @Expose
  private Integer taskId;
  
  /** From driver app */
  @SerializedName("TimestampUtc")
  @Expose
  private Date timestampUtc;
  
  /** From driver app */
  @SerializedName("DelayInMinutes")
  @Expose
  private Integer delayInMinutes;
  
  /** From driver app */
  @SerializedName("DelaySource")
  @Expose
  private DelaySource delaySource;
  
  /** From driver app */
  @SerializedName("Comment")
  @Expose
  private String comment;
  
  public Integer getWaitingReasongId() {
    return this.waitingReasongId;
  }
  
  public void setWaitingReasongId(Integer waitingReasongId) {
    this.waitingReasongId = waitingReasongId;
  }
  
  public String getWaitingReasonAppId() {
    return this.waitingReasonAppId;
  }
  
  public void setWaitingReasonAppId(String waitingReasonAppId) {
    this.waitingReasonAppId = waitingReasonAppId;
  }
  
  public Integer getActivityId() {
    return this.activityId;
  }
  
  public void setActivityId(Integer activityId) {
    this.activityId = activityId;
  }
  
  public String getReasonText() {
    return this.reasonText;
  }
  
  public void setReasonText(String reasonText) {
    this.reasonText = reasonText;
  }
  
  public String getTranslatedReasonText() {
    return this.translatedReasonText;
  }
  
  public void setTranslatedReasonText(String translatedReasonText) {
    this.translatedReasonText = translatedReasonText;
  }
  
  public Integer getCode() {
    return this.code;
  }
  
  public void setCode(Integer code) {
    this.code = code;
  }
  
  public Integer getSubCode() {
    return this.subCode;
  }
  
  public void setSubCode(Integer subCode) {
    this.subCode = subCode;
  }
  
  public Integer getMandantId() {
    return this.mandantId;
  }
  
  public void setMandantId(Integer mandantId) {
    this.mandantId = mandantId;
  }
  
  public Integer getTaskId() {
    return this.taskId;
  }
  
  public void setTaskId(Integer taskId) {
    this.taskId = taskId;
  }
  
  public Date getTimestampUtc() {
    return this.timestampUtc;
  }
  
  public void setTimestampUtc(Date timestampUtc) {
    this.timestampUtc = timestampUtc;
  }
  
  public Integer getDelayInMinutes() {
    return this.delayInMinutes;
  }
  
  public void setDelayInMinutes(Integer delayInMinutes) {
    this.delayInMinutes = delayInMinutes;
  }
  
  public DelaySource getDelaySource() {
    return this.delaySource;
  }
  
  public void setDelaySource(DelaySource delaySource) {
    this.delaySource = delaySource;
  }
  
  public String getComment() {
    return this.comment;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
}
