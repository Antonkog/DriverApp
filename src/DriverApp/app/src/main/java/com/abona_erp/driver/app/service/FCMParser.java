package com.abona_erp.driver.app.service;

import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.model.CommItem;

public interface FCMParser {
    void showFCMNotification();

    boolean parseCommonItem(String message);

    void addTasksAndActivities(CommItem commItem, String raw);

    void updateFoundDbTask(Notify notify, CommItem commItem);

    void insertNewTask(CommItem commItem, Notify notify);

    void updateActivities(Notify notify, CommItem commItem);

    void updateDbActivities(LastActivity lastActivity, CommItem commItem, Notify notify);

    boolean vehicleExist(CommItem commItem);

    void addVehicle(CommItem commItem);


    void addDocument(CommItem commItem);

    void saveCommonTaskPercentage(CommItem commItem);

    void removeAllTasks(CommItem commItem);

    void hideFCMNotification();

    void postHistoryEvent(Notify item, OfflineConfirmation offlineConfirmation);
    
    void addDelayReason(CommItem commItem);
}
