package com.abona_erp.driver.app.util.concurrent;

import android.os.Message;

/**
 * An interface for worker threads to send message to the UI thread.
 * MainActivity implemented this Interface in this app.
 */
public interface UiThreadCallback {
  void publishToUiThread(Message message);
}
