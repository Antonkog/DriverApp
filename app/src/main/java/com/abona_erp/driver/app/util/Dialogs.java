package com.abona_erp.driver.app.util;

import android.content.Context;

import com.developer.kalert.KAlertDialog;

public class Dialogs {
  
  public static void showWarningDialog(Context context, String title, String message) {
    
    new KAlertDialog(context, KAlertDialog.WARNING_TYPE)
      .setTitleText(title)
      .setContentText(message)
      .setConfirmText(context.getResources().getString(android.R.string.ok))
      .show();
  }
  
  public static void showInfoDialog(Context context, String title, String message) {
  
    new KAlertDialog(context, KAlertDialog.SUCCESS_TYPE)
      .setTitleText(title)
      .setContentText(message)
      .setConfirmText(context.getResources().getString(android.R.string.ok))
      .show();
  }
}
