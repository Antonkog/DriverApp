package com.abona_erp.driver.app.ui.event;


import com.abona_erp.driver.app.data.entity.ChangeHistory;

public class ChangeHistoryEvent {
    private ChangeHistory changeHistory;

    public ChangeHistoryEvent(ChangeHistory changeHistory) {
        this.changeHistory = changeHistory;
    }

    public ChangeHistory getChangeHistory() {
        return changeHistory;
    }
}
