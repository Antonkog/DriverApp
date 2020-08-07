package com.abona_erp.driver.app.ui.event;


import com.abona_erp.driver.app.data.converters.LogLevel;
import com.abona_erp.driver.app.data.converters.LogType;
import com.abona_erp.driver.app.data.entity.LogItem;

import java.util.Date;

public class LogEvent {
    private LogItem log;

    public LogEvent(LogItem log) {
        this.log = log;
    }

    public LogEvent(String message, LogType type, LogLevel level, String title, int idOptional) {
        LogItem item = new LogItem();
        item.setLevel(level);
        item.setType(type);
        item.setTitle(title);
        if(idOptional > 0)
            item.setMessage(message + " " + idOptional);
        else
            item.setMessage(message);
        item.setCreatedAt(new Date());
        log = item;
    }

    public LogItem getLog() {
        return log;
    }
}
