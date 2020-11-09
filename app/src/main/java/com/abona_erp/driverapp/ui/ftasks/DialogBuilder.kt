package com.abona_erp.driverapp.ui.ftasks

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.TaskEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Created by Anton Kogan on 10/8/2020
 *
 */

class DialogBuilder(val context: Context) {

    companion object {
        const val TAG = "DialogBuilder"

        fun getBaseDialogBuilder(context: Context): MaterialAlertDialogBuilder {
            return MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog)
        }

        fun getStartTaskDialog(
            taskName: String?,
            positiveListener: DialogInterface.OnClickListener,
            context: Context
        ): Dialog {
            return getBaseDialogBuilder(context)
                .setTitle(context.resources.getString(R.string.dialog_start_task_title))
                .setMessage(
                    String.format(
                        context.resources.getString(R.string.dialog_start_task),
                        taskName
                    )
                )
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    positiveListener.onClick(
                        dialog,
                        which
                    )
                }
                .create()
        }
    }

    fun getBaseDialogBuilder(): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog)
    }

    fun getTaskInfoDialog(task: TaskEntity): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.dialog_server_error_title)
            .setMessage(task.orderDetails?.customerName).create()
    }


    fun getTaskUpdateDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.action_update)
            .setMessage(R.string.action_update_message).create()
    }

    fun getErrorDialog(message: String): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.dialog_server_error_title)
            .setMessage(message).create()
    }

    fun getPermissionErrorDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.need_permissions_title)
            .setMessage(R.string.need_permissions_message).create()
    }

    fun getTaskNotFoundDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.dialog_not_found_title)
            .setMessage(R.string.dialog_not_found_message).create()
    }

    fun getRegistationSuccessDialog(message: String): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.dialog_register).setMessage(message)
            .create()
    }

    fun getLoginErrorDialog(message: String): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.action_warning_notice)
            .setMessage(context.resources.getString(R.string.dialog_login_error) + " " + message)
            .create()
    }

    fun getLoginNotActiveDialog(): Dialog {
        return getBaseDialogBuilder()
            .setTitle(context.resources.getString(R.string.action_warning_notice))
            .setMessage(context.resources.getString(R.string.client_id_is_not_active)).create()
    }

    fun getAirplaneDialog(): AlertDialog {
        return getBaseDialogBuilder().setTitle(R.string.action_warning)
            .setMessage(R.string.airplane_warning).create()
    }
//
//     fun getExitDialog(): Dialog {
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
//     fun getNoConnectionDialog(): Dialog {
//        return getBaseDialogBuilder().setTitle(context.resources.getString(R.string.action_warning_notice))
//            .setMessage(context.resources.getString(R.string.no_internet)).create()
//    }
//
//     fun getSettingsDialog(): Dialog {
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
//     fun getDocumentDialog(): Dialog {
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
//     fun getPasswordDialog(): Dialog {
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