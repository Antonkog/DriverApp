package com.abona_erp.driver.app.data.entity;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntDef;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.converters.TimestampConverter;
import com.abona_erp.driver.app.ui.feature.main.Constants;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Date;

@Entity(tableName = "last_activity_table")
public class LastActivity implements Serializable {

  @PrimaryKey(autoGenerate = true)
  private int id;

  @ColumnInfo(name = "task_id")
  private int taskId;

  @ColumnInfo(name = "client_id")
  private int clientId;
  
  @ColumnInfo(name = "customer")
  private String customer;
  
  @ColumnInfo(name = "order_no")
  private String orderNo;
  
  @ColumnInfo(name = "detail_list")
  private ArrayList<String> detailList = new ArrayList<>();
  
  @ColumnInfo(name = "status_type")
  private int statusType;
  
  @ColumnInfo(name = "confirm_status")
  private int confirmStatus;
  
  @ColumnInfo(name = "visible")
  private boolean visible;
  
  @ColumnInfo(name = "task_action_type")
  private int taskActionType;
  
  @ColumnInfo(name = "created_at")
  @TypeConverters({TimestampConverter.class})
  private Date createdAt;
  
  @ColumnInfo(name = "modified_at")
  @TypeConverters({TimestampConverter.class})
  private Date modifiedAt;

  @Ignore
  private boolean currentlySelected;

  public boolean isCurrentlySelected() {
    return currentlySelected;
  }

  public void setCurrentlySelected(boolean currentlySelected) {
    this.currentlySelected = currentlySelected;
  }
  // ------------------------------------------------------------------------
  // GETTER SETTER
  
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
  
  public int getTaskId() {
    return taskId;
  }
  
  public void setTaskId(int taskId) {
    this.taskId = taskId;
  }
  
  public int getClientId() {
    return clientId;
  }
  
  public void setClientId(int clientId) {
    this.clientId = clientId;
  }
  
  public String getCustomer() {
    return customer;
  }
  
  public void setCustomer(String customer) {
    this.customer = customer;
  }
  
  public String getOrderNo() {
    return orderNo;
  }
  
  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }
  
  public int getStatusType() {
    return statusType;
  }

  public static final int NOT_CONFIRMED = 0;
  public static final int UPDATE = 1;
  public static final int FINISHED_TASK = 3;
  public static final int ERLEDIGT = 4;
  public static final int DELETED = 9;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({NOT_CONFIRMED, UPDATE, FINISHED_TASK, ERLEDIGT,DELETED})
  // Create an interface for validating int types
  public @interface StatusTypeDef {}
  // Declare the constants
  public void setStatusType(@StatusTypeDef int statusType) {
    this.statusType = statusType;
  }

  public void setIntStatus(int statusType) {
    this.statusType = statusType;
  }

  public LastActivity setSelectedAndReturn(boolean currentlySelected) {
    this.currentlySelected = currentlySelected;
    return this;
  }


  public ArrayList<String> getDetailList() {
    return detailList;
  }
  
  public void setDetailList(ArrayList<String> detailList) {
    this.detailList = detailList;
  }
  
  public int getConfirmStatus() {
    return confirmStatus;
  }
  
  public void setConfirmStatus(int confirmStatus) {
    this.confirmStatus = confirmStatus;
  }
  
  public boolean getVisible() {
    return visible;
  }
  
  public void setVisible(boolean visible) {
    this.visible = visible;
  }
  
  public int getTaskActionType() {
    return taskActionType;
  }

  public static String getStatusTypeString(int statusType, Context context){
    switch (statusType){
      case NOT_CONFIRMED:
        return context.getResources().getString(R.string.action_not_confirmed);
      case UPDATE:
        return context.getResources().getString(R.string.action_update);
      case FINISHED_TASK:
        return context.getResources().getString(R.string.action_finished);
      case ERLEDIGT:
        return context.getResources().getString(R.string.action_erledict);
      case DELETED:
        return context.getResources().getString(R.string.action_deleted);
      default: return Constants.empty_str;
    }
  }

  public String getActionTypeString(Context context){
    switch (taskActionType){
      case 0:
        return context.getResources().getString(R.string.action_type_pick_up);
      case 1:
        return context.getResources().getString(R.string.action_type_drop_off);
      case 2:
        return context.getResources().getString(R.string.action_type_general);
      case 3:
        return context.getResources().getString(R.string.action_type_tractor_swap);
      case 4:
        return context.getResources().getString(R.string.action_type_delay);
      case 5:
      case 6:
        return context.getResources().getString(R.string.action_type_changed_by_a);
      case 9:
        return context.getResources().getString(R.string.label_deleted);
      case 100:
        return context.getResources().getString(R.string.action_type_unknown);
      default: return Constants.empty_str;
    }
  }

  public Drawable getActionTypeBackground(Context context){
    switch (taskActionType){
      case 0:
        return context.getResources().getDrawable(R.drawable.bg_action_type_pick_up, null);
      case 1:
        return context.getResources().getDrawable(R.drawable.bg_action_type_drop_off, null);
      case 2:
        return context.getResources().getDrawable(R.drawable.bg_action_type_general, null);
      case 3:
        return context.getResources().getDrawable(R.drawable.bg_action_type_tractor_swap, null);
      case 4:
        return context.getResources().getDrawable(R.drawable.bg_action_type_delay, null);
      case 5:
      case 6:
        return context.getResources().getDrawable(R.drawable.bg_changed_by_abona, null);
      case 9:
        return context.getResources().getDrawable(R.drawable.bg_deleted, null);
      case 100:
        return context.getResources().getDrawable(R.drawable.bg_action_type_unknown, null);
      default: return context.getResources().getDrawable(R.drawable.transparent, null);
    }
  }
  public Drawable getActionTypeIcon(Context context){
    switch (taskActionType){
      case 0:
        return context.getResources().getDrawable(R.drawable.ic_notifications, null);
      case 1:
        return context.getResources().getDrawable(R.drawable.ic_down_arrow, null);
      case 2:
        return context.getResources().getDrawable(R.drawable.ic_refresh, null);
      case 3:
      case 4:
        return context.getResources().getDrawable(R.drawable.ic_done, null);
      case 5:
      case 6:
        return context.getResources().getDrawable(R.drawable.abona_36x36, null);
      case 9:
        return context.getResources().getDrawable(R.drawable.ic_delete, null);
      case 100:
        return context.getResources().getDrawable(R.drawable.bg_action_type_unknown, null);
      default: return context.getResources().getDrawable(R.drawable.transparent, null);
    }
  }

  public void setTaskActionType(int taskActionType) {
    this.taskActionType = taskActionType;
  }
  
  public Date getCreatedAt() {
    return createdAt;
  }
  
  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }
  
  public Date getModifiedAt() {
    return modifiedAt;
  }
  
  public void setModifiedAt(Date modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

  @Override
  public String toString() {
    return "LastActivity{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", clientId=" + clientId +
            ", customer='" + customer + '\'' +
            ", orderNo='" + orderNo + '\'' +
            ", detailList=" + detailList +
            ", statusType=" + statusType +
            ", confirmStatus=" + confirmStatus +
            ", visible=" + visible +
            ", taskActionType=" + taskActionType +
            ", createdAt=" + createdAt +
            ", modifiedAt=" + modifiedAt +
            ", currentlySelected=" + currentlySelected +
            '}';
  }
}
