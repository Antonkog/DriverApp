package com.abona_erp.driver.app.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.data.converters.TimestampConverter;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.LastActivityDetails;
import com.abona_erp.driver.app.data.model.TaskActionType;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.util.AppUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "last_activity_table")
public class LastActivity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "task_id")
    private int taskId;

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

    public void setStatusType(int statusType) {
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


    public static LastActivity updateFromCommItem(LastActivity lastActivity, CommItem commItem) {
        lastActivity.setCustomer(commItem.getTaskItem().getKundenName());
        lastActivity.setOrderNo(AppUtils.parseOrderNo(commItem.getTaskItem().getOrderNo()));
        lastActivity.setStatusType(1);
        lastActivity.setConfirmStatus(0);
        lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
        lastActivity.setTaskActionType(TaskActionType.getCodeByTaskActionType(commItem.getTaskItem().getActionType()));

        ArrayList<String> _list = lastActivity.getDetailList();

        LastActivityDetails _detail = new LastActivityDetails();
        _detail.setDescription("UPDATE");
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                Locale.getDefault());
        _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
        _list.add(App.getInstance().gson.toJson(_detail));
        lastActivity.setDetailList(_list);
        if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING) || commItem.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING)) {
            lastActivity.setVisible(true);
        } else {
            lastActivity.setVisible(false);
        }
        return lastActivity;
    }
}
