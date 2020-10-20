package com.abona_erp.driver.app.service;

import android.net.Uri;

public interface FCMParser {
   boolean parseCommonItem(String message);

   void startRingtone(Uri uri);

   void setRingtonePlayer();
}
