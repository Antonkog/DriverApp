package com.abona_erp.driver.app.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.abona_erp.driver.app.BuildConfig;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.ui.feature.main.MainActivity;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.MessageDialog;

public class Util {
  private static final String TAG = Util.class.getSimpleName();
  
  /**
   * The app version.
   * <p>
   *   This code should be used in all places that compare app versions
   *   rather than {@link BuildConfig#VERSION_CODE}.
   * </p>
   */
  public static int getCanonicalVersionCode() {
    return BuildConfig.CANONICAL_VERSION_CODE;
  }

  public static boolean isAirplaneModeOn(Context context) {
    return Settings.Global.getInt(context.getContentResolver(),
            Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
  }

  public static boolean showAirplaneDialog(MainActivity mainActivity) {
    MessageDialog.build(mainActivity)
            .setStyle(DialogSettings.STYLE.STYLE_IOS)
            .setTheme(DialogSettings.THEME.LIGHT)
            .setTitle(R.string.action_warning)
            .setMessage(R.string.airplane_warning)
            .setOkButton(mainActivity.getApplicationContext().getResources().getString(R.string.action_ok))
            .show();
    return true;
  }


  /**
   * Showing Alert Dialog with Settings Option.
   * Navigates User to App Settings.
   */
  public static void showSettingsDialog(MainActivity mainActivity) {

    Context context = mainActivity.getBaseContext();
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(context.getResources().getString(R.string.need_permissions));
    builder.setMessage(context.getResources().getString(R.string.permission_message_settings));
    builder.setPositiveButton(context.getResources().getString(R.string.menu_settings),
            new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
                openSettings(mainActivity);
              }
            });
    builder.setNegativeButton(context.getResources().getString(R.string.action_cancel),
            new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
                mainActivity.finish();
              }
            });
    builder.show();
  }


  /**
   * Navigating User to App Settings.
   */
  private static void openSettings(MainActivity mainActivity) {
    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", mainActivity.getPackageName(), null);
    intent.setData(uri);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            | Intent.FLAG_ACTIVITY_NO_HISTORY
            | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    mainActivity.startActivityForResult(intent, MainActivity.REQUEST_APP_SETTINGS);
  }

  public static void showPermissionErrorMessageAndFinish(MainActivity mainActivity, String title, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity.getBaseContext());
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setPositiveButton(mainActivity.getBaseContext().getResources().getString(R.string.action_ok),
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                mainActivity.finish();
              }
            });
    builder.show();
  }

  public static void askNeedExit(MainActivity mainActivity) {
    Context context = mainActivity.getBaseContext();
    MessageDialog.build(mainActivity)
            .setStyle(DialogSettings.STYLE.STYLE_IOS)
            .setTheme(DialogSettings.THEME.LIGHT)
            .setTitle(context.getResources().getString(R.string.action_exit))
            .setMessage(context.getResources().getString(R.string.exit_message))
            .setOkButton(context.getResources().getString(R.string.action_quit),
                    new OnDialogButtonClickListener() {
                      @Override
                      public boolean onClick(BaseDialog baseDialog, View v) {
                        mainActivity.finish();
                        return false;
                      }
                    })
            .setCancelButton(context.getResources().getString(R.string.action_cancel),
                    new OnDialogButtonClickListener() {
                      @Override
                      public boolean onClick(BaseDialog baseDialog, View v) {
                        return false;
                      }
                    })
            .show();
  }


  public static void showDialog(AppCompatActivity activity, String title, String message) {
        MessageDialog.build(activity)
                .setStyle(DialogSettings.STYLE.STYLE_IOS)
                .setTheme(DialogSettings.THEME.LIGHT)
                .setTitle(title)
                .setMessage(message)
                .setOkButton(activity.getApplicationContext().getResources().getString(R.string.action_ok),
                        new OnDialogButtonClickListener() {
                          @Override
                          public boolean onClick(BaseDialog baseDialog, View v) {
                            return false;
                          }
                        })
                .show();
  }

  public static void sayNoConnection(MainActivity mainActivity) {
    Context context = mainActivity.getBaseContext();
    MessageDialog.build(mainActivity)
            .setStyle(DialogSettings.STYLE.STYLE_IOS)
            .setTheme(DialogSettings.THEME.LIGHT)
            .setTitle(context.getResources().getString(R.string.action_exit))
            .setMessage(context.getResources().getString(R.string.exit_message))
            .setOkButton(context.getResources().getString(R.string.action_quit),
                    new OnDialogButtonClickListener() {
                      @Override
                      public boolean onClick(BaseDialog baseDialog, View v) {
                        mainActivity.finish();
                        return false;
                      }
                    })
            .setCancelButton(context.getResources().getString(R.string.action_cancel),
                    new OnDialogButtonClickListener() {
                      @Override
                      public boolean onClick(BaseDialog baseDialog, View v) {
                        return false;
                      }
                    })
            .show();
  }

  public static void showDocumentDialog(MainActivity mainActivity, Notify notify) {
    Context context = mainActivity.getApplicationContext();
    MessageDialog.build(mainActivity)
            .setStyle(DialogSettings.STYLE.STYLE_IOS)
            .setTheme(DialogSettings.THEME.LIGHT)
            .setTitle(context.getResources().getString(R.string.new_document))
            .setMessage(context.getResources().getString(R.string.new_document_message)
                    + "\n"
                    + context.getResources().getString(R.string.order_no)
                    + ": "
                    + AppUtils.parseOrderNo(notify.getOrderNo()))
            .setOkButton(context.getResources().getString(R.string.action_ok),
                    new OnDialogButtonClickListener() {
                      @Override
                      public boolean onClick(BaseDialog baseDialog, View v) {
                        return false;
                      }
                    })
            .show();
  }
}
