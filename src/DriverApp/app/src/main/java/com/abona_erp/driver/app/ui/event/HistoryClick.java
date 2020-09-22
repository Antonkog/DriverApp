package com.abona_erp.driver.app.ui.event;

public class HistoryClick {
    int taskId;
    int orderNo;

    public HistoryClick(int taskId, int orderNo) {
        this.taskId = taskId;
        this.orderNo = orderNo;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public int getTaskId() {
        return taskId;
    }
}
