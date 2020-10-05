package com.abona_erp.driver.app.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.DialogFragment;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.feature.main.Constants;
import com.abona_erp.driver.app.util.dynamiclanguage.DynamicLanguageContextWrapper;

import org.jetbrains.annotations.NotNull;

import static com.abona_erp.driver.app.util.Util.updatePreferenceFlags;

public class CustomDialogFragment extends DialogFragment {

    private String TAG = CustomDialogFragment.class.getCanonicalName();
    private CustomDialogListener customDialogListener;
    private Context context;
    private DialogType dialogType;
    private @Nullable int orderNo;
    private String message;


    public CustomDialogFragment() { } //fragment must implement empty constructor if use constructor overload

    public static CustomDialogFragment newInstance(DialogType dialogType, @Nullable int orderNo) {
        CustomDialogFragment f = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putInt("ordinal", dialogType.ordinal());
        args.putInt("orderNo", orderNo);
        f.setArguments(args);
        return f;
    }

    public static CustomDialogFragment newInstance(DialogType dialogType, String message) {
        CustomDialogFragment f = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putInt("ordinal", dialogType.ordinal());
        args.putString("message", message);
        f.setArguments(args);
        return f;
    }

    public static CustomDialogFragment newInstance(DialogType dialogType) {
        CustomDialogFragment f = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putInt("ordinal", dialogType.ordinal());
        f.setArguments(args);
        return f;
    }

    public enum DialogType{
        PERMISSION,
        LOGIN_NOT_ACTIVE,
        LOGIN_ERROR,
        REGISTRATION_SUCCESS,//"Successful Registrated"
        TASK_NOT_FOUND, //"Not Found", "Task existiert nicht mehr!"
        NO_CONNECTION,
        AIRPLANE_MODE,
        SETTINGS,
        PROTOCOL,
        LANGUAGE,
        DEVICE_RESET,
        DOCUMENT,
        EXIT
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int ordinal = getArguments().getInt("ordinal");
        this.dialogType = DialogType.values()[ordinal];
        this.message =  getArguments().getString("message");
        this.orderNo =  getArguments().getInt("orderNo");
    }

//It can also be added as content in a view hierarchy:
//    @Override
//    public void show(FragmentManager manager, String tag) {
//        try {
//            FragmentTransaction ft = manager.beginTransaction();
//            ft.add(this, tag);
//            ft.commit(); //commitAllowingStateLoss
//        } catch (IllegalStateException e) {
//            Log.d(TAG, "Exception", e);
//        }
//    }

    public DialogType getDialogType() {
        return dialogType;
    }

    public interface CustomDialogListener {
         void onDialogPositiveClick(CustomDialogFragment dialog);
         void onDialogNegativeClick(CustomDialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        switch (dialogType){
            case PERMISSION:  return getPermissionErrorDialog();
            case LOGIN_NOT_ACTIVE:  return getLoginNotActiveDialog();
            case LOGIN_ERROR:  return getLoginErrorDialog();
            case REGISTRATION_SUCCESS:  return getRegistationSuccessDialog();
            case TASK_NOT_FOUND:  return getTaskNotFoundDialog();
            case NO_CONNECTION:  return getNoConnectionDialog();
            case AIRPLANE_MODE:  return getAirplaneDialog();
            case SETTINGS:  return getSettingsDialog();
            case LANGUAGE:  return getLangDialog();
            case DEVICE_RESET:  //same dialog from PROTOCOL
            case PROTOCOL:  return getPasswordDialog();
            case DOCUMENT:  return getDocumentDialog();
            case EXIT:  return getExitDialog();
            default: return getBaseDialogBuilder().create();
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            customDialogListener = (CustomDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement CustomDialogListener");
        }
        if(customDialogListener == null)  throw new IllegalStateException(" customDialogListener must be set");

//        switch (dialogType){
//            case DOCUMENT: if(taskData == null) throw new IllegalStateException(" must use constructor with Data for DOCUMENT dialog.");
//            case REGISTRATION_SUCCESS: if(message == null) throw new IllegalStateException(" must use constructor with message for REGISTRATION_SUCCESS dialog.");
//            case LOGIN_ERROR: if(message == null) throw new IllegalStateException(" must use constructor with message for LOGIN_ERROR dialog.");
//                break;
//        }
    }

    private AlertDialog.Builder getBaseDialogBuilder() {
        return new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AbonaDialog))
        .setPositiveButton(R.string.action_ok, (dialog, id) -> { // id = which button click
            // Send the positive button event back to the host activity
            customDialogListener.onDialogPositiveClick(CustomDialogFragment.this);
        });
    }


    private Dialog getPermissionErrorDialog() {
        return getBaseDialogBuilder().setTitle(R.string.need_permissions_title)
                .setMessage(R.string.need_permissions_message).create();
    }

    private Dialog getTaskNotFoundDialog() {
         return getBaseDialogBuilder().setTitle(R.string.dialog_not_found_title).setMessage(R.string.dialog_not_found_message).create();
    }

    private Dialog getRegistationSuccessDialog() {
        return getBaseDialogBuilder().setTitle(R.string.dialog_register).setMessage(message).create();
    }

    private Dialog getLoginErrorDialog() {
        return getBaseDialogBuilder().setTitle(R.string.action_warning_notice).setMessage(getResources().getString(R.string.dialog_login_error) + " " + message).create();
    }

    private Dialog getLoginNotActiveDialog() {
        return getBaseDialogBuilder()
                .setTitle(context.getResources().getString(R.string.action_warning_notice))
                .setMessage(context.getResources().getString(R.string.client_id_is_not_active)).create();
    }

    private AlertDialog getAirplaneDialog() {
        return getBaseDialogBuilder().setTitle(R.string.action_warning)
                .setMessage(R.string.airplane_warning).create();
    }

    private Dialog getExitDialog() {
        return  getBaseDialogBuilder().setTitle(context.getResources().getString(R.string.action_exit))
                .setMessage(context.getResources().getString(R.string.exit_message))
                .setPositiveButton(R.string.action_quit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customDialogListener.onDialogPositiveClick(CustomDialogFragment.this);
                    }
                }).create();
    }

    private Dialog getNoConnectionDialog() {
        return   getBaseDialogBuilder().setTitle(context.getResources().getString(R.string.action_warning_notice))
                .setMessage(context.getResources().getString(R.string.no_internet)).create();
    }


    private Dialog getSettingsDialog() {
        return getBaseDialogBuilder()
                .setTitle(context.getResources().getString(R.string.need_permissions_title))
                .setMessage(context.getResources().getString(R.string.permission_message_settings))
                .setNegativeButton(R.string.cancel, (dialog, id) -> customDialogListener.onDialogNegativeClick(CustomDialogFragment.this))
                .create();
    }

    private  Dialog getDocumentDialog() {
        return   getBaseDialogBuilder().setTitle(context.getResources().getString(R.string.new_document))
                .setMessage(context.getResources().getString(R.string.new_document_message)
                        + "\n"
                        + (context.getResources().getString(R.string.order_no) + ": " +
                        AppUtils.parseOrderNo(orderNo))
                ).create();
    }

    private Dialog getLangDialog() {
        String[] listItems = { //order matter
                context.getResources().getString(R.string.preference_language_eng),
                context.getResources().getString(R.string.preference_language_ger),
                context.getResources().getString(R.string.preference_language_rus),
                context.getResources().getString(R.string.preference_language_pol),
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AbonaDialog));
        builder.setTitle(R.string.dialog_language_title);
        String currentLanguage = TextSecurePreferences.getLanguage(getContext());
        int currentPosition =  0;
        switch (currentLanguage){
            case Constants.LANG_TO_SERVER_ENGLISH: currentPosition = 0; break;
            case Constants.LANG_TO_SERVER_GERMAN:currentPosition = 1; break;
            case Constants.LANG_TO_SERVER_RUSSIAN:currentPosition = 2; break;
            case Constants.LANG_TO_SERVER_POLISH:currentPosition = 3; break;
        }
        builder.setSingleChoiceItems(listItems, currentPosition, getListener());
        return  builder.create();
    }

    @NotNull
    private DialogInterface.OnClickListener getListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0: // ENGLISCH
                        if(TextSecurePreferences.getLanguage(getContext()) !=  Constants.LANG_TO_SERVER_ENGLISH){
                            TextSecurePreferences.setLanguage(getContext(), Constants.LANG_TO_SERVER_ENGLISH);
                            updateLanguage();
                            sendItemClickAsNegative();
                        }
                        break;
                    case 1: // DEUTSCH
                        if(TextSecurePreferences.getLanguage(getContext()) !=  Constants.LANG_TO_SERVER_GERMAN) {
                            TextSecurePreferences.setLanguage(getContext(), Constants.LANG_TO_SERVER_GERMAN);
                            updateLanguage();
                            sendItemClickAsNegative();
                        }
                        break;

                    case 2: // RUSSISCH
                        if(TextSecurePreferences.getLanguage(getContext()) !=  Constants.LANG_TO_SERVER_RUSSIAN) {
                            TextSecurePreferences.setLanguage(getContext(), Constants.LANG_TO_SERVER_RUSSIAN);
                            updateLanguage();
                            sendItemClickAsNegative();
                        }
                        break;

                    case 3: // POLNISCH
                        if(TextSecurePreferences.getLanguage(getContext()) !=  Constants.LANG_TO_SERVER_POLISH) {
                            TextSecurePreferences.setLanguage(getContext(), Constants.LANG_TO_SERVER_POLISH);
                            updateLanguage();
                            sendItemClickAsNegative();
                        }
                        break;
                }
            }
        };
    }

    private void sendItemClickAsNegative() {
        customDialogListener.onDialogNegativeClick(CustomDialogFragment.this);
    }

    private void updateLanguage() {
        DynamicLanguageContextWrapper.updateContext(getContext(),
                TextSecurePreferences.getLanguage(getContext()));
        getActivity().recreate();
        updatePreferenceFlags();
    }



    private Dialog getPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getContext().getResources().getString(R.string.action_security_code))
                .setMessage(getContext().getResources().getString(R.string.action_security_code_message));

        final EditText input = new EditText(getActivity().getBaseContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setPositiveButton(R.string.action_ok,
                (dialog, which) -> {
                        String pass = input.getText().toString();
                        if (Constants.SECURITY_CODE.equals(pass))
                        customDialogListener.onDialogPositiveClick(CustomDialogFragment.this);
                });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> customDialogListener.onDialogNegativeClick(CustomDialogFragment.this));
       return  builder.create();
    }
}