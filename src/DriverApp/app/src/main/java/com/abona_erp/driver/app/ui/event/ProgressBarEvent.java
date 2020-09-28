package com.abona_erp.driver.app.ui.event;

import javax.annotation.Nullable;

public class ProgressBarEvent implements BaseEvent {
    private boolean showProgress = false;
    int maxDuration = 0;

    @Nullable
    private String message;

    public ProgressBarEvent(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public ProgressBarEvent(boolean showProgress, String message) {
        this.showProgress = showProgress;
        this.message = message;
    }

    public ProgressBarEvent(boolean showProgress, int maxDuration, @Nullable String message) {
        this.showProgress = showProgress;
        this.maxDuration = maxDuration;
        this.message = message;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public boolean isShowProgress() {
        return showProgress;
    }
}
