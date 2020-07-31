package com.abona_erp.driver.app.util;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

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
  public static void showSettingsDialog(Context context, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListeneer) {
      AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AbonaDialog));
      builder.setTitle(context.getResources().getString(R.string.need_permissions));
      builder.setMessage(context.getResources().getString(R.string.permission_message_settings));
      builder.setPositiveButton(context.getResources().getString(R.string.menu_settings), positiveListener);
      builder.setNegativeButton(context.getResources().getString(R.string.action_cancel), negativeListeneer);
      builder.show();
  }


  public static void showPermissionErrorMessageAndFinish(Context context, String title, String message, DialogInterface.OnClickListener positiveListener ) {
    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AbonaDialog));
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setPositiveButton(context.getResources().getString(R.string.action_ok), positiveListener);
    builder.show();
  }

  public static void askNeedExit(AppCompatActivity currentActivity) {
    Context context = currentActivity.getBaseContext();
    MessageDialog.build(currentActivity)
            .setStyle(DialogSettings.STYLE.STYLE_IOS)
            .setTheme(DialogSettings.THEME.LIGHT)
            .setTitle(context.getResources().getString(R.string.action_exit))
            .setMessage(context.getResources().getString(R.string.exit_message))
            .setOkButton(context.getResources().getString(R.string.action_quit),
                    new OnDialogButtonClickListener() {
                      @Override
                      public boolean onClick(BaseDialog baseDialog, View v) {
                        currentActivity.finish();
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
