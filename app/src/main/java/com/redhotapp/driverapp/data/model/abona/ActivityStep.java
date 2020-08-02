package com.redhotapp.driverapp.data.model.abona;

public class ActivityStep {

  private TaskStatus mTaskStatus;
  private TaskActionType mTaskActionType;
  private ActivityItem mActivityItem;

  public ActivityStep(TaskStatus taskStatus, TaskActionType taskActionType, ActivityItem activityItem) {
    this.mTaskStatus = taskStatus;
    this.mTaskActionType = taskActionType;
    this.mActivityItem = activityItem;
  }

  public TaskStatus getTaskStatus() {
    return mTaskStatus;
  }

  public void setTaskStatus(TaskStatus taskStatus) {
    this.mTaskStatus = taskStatus;
  }
  
  public TaskActionType getTaskActionType() {
    return mTaskActionType;
  }
  
  public void setTaskActionType(TaskActionType taskActionType) {
    this.mTaskActionType = taskActionType;
  }

  public ActivityItem getActivityItem() {
    return mActivityItem;
  }

  public void setActivityItem(ActivityItem activityItem) {
    this.mActivityItem = activityItem;
  }
}
