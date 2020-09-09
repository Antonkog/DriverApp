package com.abona_erp.driver.app.ui.event;


import com.abona_erp.driver.app.data.converters.LogType;
import com.abona_erp.driver.app.data.entity.ActionType;
import com.abona_erp.driver.app.data.entity.ChangeHistory;
import com.abona_erp.driver.app.data.entity.ChangeHistoryState;

import java.util.Date;

public class ChangeHistoryEvent {
    private ChangeHistory changeHistory;

    public ChangeHistoryEvent(String title, String message, LogType type, ActionType actionType, ChangeHistoryState state,
                              int taskId, int activityId, int orderNumber, int mandantID, int offlineConfirmationID) {
        ChangeHistory history = new ChangeHistory();
        history.setTitle(title);
        history.setMessage(message);
        history.setCreatedAt(new Date());
        history.setModifiedAt(new Date());
        history.setType(type);
        history.setActionType(actionType);
        history.setState(state);
        history.setOrderNumber(orderNumber);
        history.setMandantID(mandantID);
        history.setActivityId(activityId);
        history.setTaskId(taskId);
        history.setOfflineConfirmationID(offlineConfirmationID);
        changeHistory = history;
    }

    private String title;

    private String message;

    private LogType type; //app to server etc.

    private ActionType actionType;

    private ChangeHistoryState state;

    private Date createdAt;

    private Date modifiedAt;

    private int taskId;

    private int activityId;

    private int orderNumber;

    private int mandantID;

    private int offlineConfirmationID;


    public ChangeHistory getChangeHistory() {
        return changeHistory;
    }
}
