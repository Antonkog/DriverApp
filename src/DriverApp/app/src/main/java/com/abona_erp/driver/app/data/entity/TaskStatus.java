package com.abona_erp.driver.app.data.entity;

public class TaskStatus {

    private LastActivity data;
    private boolean expanded = false;

    public TaskStatus(LastActivity data) {
        this.data = data;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public LastActivity getData() {
        return data;
    }

    public void setData(LastActivity data) {
        this.data = data;
    }
}