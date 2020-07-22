package com.abona_erp.driver.app.service;

import io.reactivex.Single;

public interface AlarmChecker {
    Single<Boolean> checkTaskExist(int taskId);
    void showExistNotification(int taskId);
    void showCheckingNotification();
    void removeCheckingNotification();
    void showNotExistNotification();
}
