package com.abona_erp.driver.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.abona_erp.driver.app.service.GeofenceService;

/**
 * Receiver for geofence transition changes.
 * <p>
 *   Receives geofence transition events from Location Services in the form
 *   of an Intent containing the transition type and geofence id(s) that
 *   triggered the transition. Creates a JobIntentService that will handle
 *   the intent in the background.
 * </p>
 */
public class GeofenceBroadcastReceiver extends BroadcastReceiver {
  
  /**
   * Receives incoming intents.
   *
   * @param context The application context.
   * @param intent sent by Location Services. This Intent is provided to
   *               Location Services (inside a PendingIntent) when
   *               addGeofences() is called.
   */
  @Override
  public void onReceive(Context context, Intent intent) {
    // Enqueues a JobIntentService passing the context and intent as parameters.
    GeofenceService.enqueueWork(context, intent);
  }
}
