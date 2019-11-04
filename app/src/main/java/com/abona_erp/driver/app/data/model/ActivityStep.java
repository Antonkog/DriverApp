package com.abona_erp.driver.app.data.model;

public class ActivityStep {

  private TaskStatus mTaskStatus;
  private ActivityItem mActivityItem;

  public ActivityStep(TaskStatus taskStatus, ActivityItem activityItem) {
    mTaskStatus = taskStatus;
    mActivityItem = activityItem;
  }

  public TaskStatus getTaskStatus() {
    return mTaskStatus;
  }

  public void setTaskStatus(TaskStatus taskStatus) {
    mTaskStatus = taskStatus;
  }

  public ActivityItem getActivityItem() {
    return mActivityItem;
  }

  public void setActivityItem(ActivityItem activityItem) {
    mActivityItem = activityItem;
  }
}
