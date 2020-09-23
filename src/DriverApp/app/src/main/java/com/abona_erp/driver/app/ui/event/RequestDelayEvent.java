package com.abona_erp.driver.app.ui.event;

public class RequestDelayEvent {
   private long delay;

    public RequestDelayEvent(long delayInMills) {
        this.delay = delayInMills;
    }

    public long getDelay() {
        return delay;
    }
}
