package com.abona_erp.driver.app.ui.event;

public class HistoryClick {
    int taskId;

    public HistoryClick(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }
}
