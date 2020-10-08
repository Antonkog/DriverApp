package com.abona_erp.driver.app.ui.home

import android.app.Dialog
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Created by Anton Kogan on 10/8/2020
 *
 */

class DialogBuilder(val context: Context) {

    public fun getBaseDialogBuilder() : MaterialAlertDialogBuilder {
        return  MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog)
    }


    public fun getTaskInfoDialog(task: TaskEntity): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.dialog_server_error_title)
            .setMessage(task.orderDetails?.customerName).create()
    }


    public fun getTaskUpdateDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.action_update)
            .setMessage(R.string.action_update_message).create()
    }

    public fun getErrorDialog(message: String): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.dialog_server_error_title)
            .setMessage(message).create()
    }

    public fun getPermissionErrorDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.need_permissions_title)
            .setMessage(R.string.need_permissions_message).create()
    }

    public fun getTaskNotFoundDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.dialog_not_found_title)
            .setMessage(R.string.dialog_not_found_message).create()
    }

    public fun getRegistationSuccessDialog(message: String): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.dialog_register).setMessage(message)
            .create()
    }

    public fun getLoginErrorDialog(message: String): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.action_warning_notice)
            .setMessage(context.resources.getString(R.string.dialog_login_error) + " " + message).create()
    }

    public fun getLoginNotActiveDialog(): Dialog {
        return getBaseDialogBuilder()
            .setTitle(context.resources.getString(R.string.action_warning_notice))
            .setMessage(context.resources.getString(R.string.client_id_is_not_active)).create()
    }

    public fun getAirplaneDialog(): AlertDialog {
        return getBaseDialogBuilder().setTitle(R.string.action_warning)
            .setMessage(R.string.airplane_warning).create()
    }
//
//    public fun getExitDialog(): Dialog {
//        return getBaseDialogBuilder().setTitle(context.resources.getString(R.string.action_exit))
//            .setMessage(context.resources.getString(R.string.exit_message))
//            .setPositiveButton(R.string.action_quit) { dialog, which ->
//                customDialogListener!!.onDialogPositiveClick(
//                    this@CustomDialogFragment
//                )
//            }
//            .create()
//    }
//
//    public fun getNoConnectionDialog(): Dialog {
//        return getBaseDialogBuilder().setTitle(context.resources.getString(R.string.action_warning_notice))
//            .setMessage(context.resources.getString(R.string.no_internet)).create()
//    }
//
//    public fun getSettingsDialog(): Dialog {
//        return getBaseDialogBuilder()
//            .setTitle(context.resources.getString(R.string.need_permissions_title))
//            .setMessage(context.resources.getString(R.string.permission_message_settings))
//            .setNegativeButton(R.string.cancel) { dialog: DialogInterface?, id: Int ->
//                customDialogListener!!.onDialogNegativeClick(
//                    this@CustomDialogFragment
//                )
//            }
//            .create()
//    }
//
//    public fun getDocumentDialog(): Dialog {
//        return getBaseDialogBuilder().setTitle(context.resources.getString(R.string.new_document))
//            .setMessage(
//                """
//    ${context.resources.getString(R.string.new_document_message)}
//
//    """.trimIndent()
//                        + (context.resources.getString(R.string.order_no) + ": " +
//                        parseOrderNo(orderNo))
//            ).create()
//    }
//
//
//    public fun getPasswordDialog(): Dialog {
//        val builder = getBaseDialogBuilder().setTitle(context.resources.getString(R.string.action_security_code))
//            .setMessage(context.resources.getString(R.string.action_security_code_message))
//        val input = EditText(context)
//        val lp = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.MATCH_PARENT
//        )
//        input.layoutParams = lp
//        builder.setView(input)
//        builder.setPositiveButton(
//            R.string.action_ok
//        ) { dialog: DialogInterface?, which: Int ->
//            val pass = input.text.toString()
//            if ("0000" == pass) customDialogListener!!.onDialogPositiveClick(this@CustomDialogFragment)
//        }
//        builder.setNegativeButton(R.string.cancel) { dialog: DialogInterface?, which: Int ->
//            customDialogListener!!.onDialogNegativeClick(
//                this@CustomDialogFragment
//            )
//        }
//        return builder.create()
//    }


    fun parseOrderNo(orderNo: Int): String {
        return if (orderNo > 0) {
            try {
                val numString = orderNo.toString().toCharArray()
                val parsedNum = StringBuilder()
                for (counter in numString.indices) {
                    if (counter == 4 || counter == 6) {
                        parsedNum.append("/")
                    }
                    parsedNum.append(numString[counter])
                }
                parsedNum.toString()
            } catch (e: RuntimeException) {
                Log.e(
                    DialogBuilder::class.java.canonicalName,
                    " parsing exception : string from server based on :$orderNo"
                )
                "-"
            }
        } else "-"
    }
}