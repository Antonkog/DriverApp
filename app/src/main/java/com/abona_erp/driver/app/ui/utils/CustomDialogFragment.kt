package com.abona_erp.driver.app.ui.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.data.model.DataType
import com.abona_erp.driver.app.ui.utils.CustomDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomDialogFragment   : DialogFragment() {
    private val TAG = CustomDialogFragment::class.java.canonicalName
    private var customDialogListener: CustomDialogListener? = null
    private var dialogType: DialogType? = null
    private var orderNo = 0
    private var message: String? = null
    enum class DialogType {
        PERMISSION, LOGIN_NOT_ACTIVE, LOGIN_ERROR, REGISTRATION_SUCCESS,  //"Successful Registrated"
        TASK_NOT_FOUND, SERVER_ERROR, TASKS_UPDATE_COMPLETE, //"Not Found", "Task existiert nicht mehr!"
        NO_CONNECTION, AIRPLANE_MODE, SETTINGS, PROTOCOL, LANGUAGE, DEVICE_RESET, DOCUMENT, EXIT
    }

    val args: CustomDialogFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogType = DialogType.values()[args.ordinal]
        message = args.message
        orderNo = args.orderNo
    }

    interface CustomDialogListener {
        fun onDialogPositiveClick(dialog: CustomDialogFragment?)
        fun onDialogNegativeClick(dialog: CustomDialogFragment?)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return when (dialogType) {
            DialogType.PERMISSION -> getPermissionErrorDialog()
            DialogType.LOGIN_NOT_ACTIVE -> getLoginNotActiveDialog()
            DialogType.LOGIN_ERROR -> getLoginErrorDialog()
            DialogType.REGISTRATION_SUCCESS -> getRegistationSuccessDialog()
            DialogType.TASK_NOT_FOUND -> getTaskNotFoundDialog()
            DialogType.NO_CONNECTION -> getNoConnectionDialog()
            DialogType.AIRPLANE_MODE -> getAirplaneDialog()
            DialogType.SETTINGS -> getSettingsDialog()
            DialogType.DEVICE_RESET, DialogType.PROTOCOL -> getPasswordDialog()
            DataType.DOCUMENT -> getDocumentDialog()
            DialogType.EXIT -> getExitDialog()
            DialogType.SERVER_ERROR -> getErrorDialog()
            DialogType.TASKS_UPDATE_COMPLETE -> getTaskUpdateDialog()
            else -> getBaseDialogBuilder().create()
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        customDialogListener = try {
            context as CustomDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                context.toString()
                        + " must implement CustomDialogListener"
            )
        }
        checkNotNull(customDialogListener) { " customDialogListener must be set" }

    }

    private fun getBaseDialogBuilder(): AlertDialog.Builder {
        return AlertDialog.Builder(ContextThemeWrapper(context, R.style.AbonaDialog))
            .setPositiveButton(R.string.action_ok) { dialog: DialogInterface?, id: Int ->  // id = which button click
                // Send the positive button event back to the host activity
                customDialogListener!!.onDialogPositiveClick(this@CustomDialogFragment)
            }
    }


    private fun getTaskUpdateDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.action_update)
            .setMessage(R.string.action_update_message).create()
    }

    private fun getErrorDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.dialog_server_error_title)
            .setMessage(message).create()
    }

    private fun getPermissionErrorDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.need_permissions_title)
            .setMessage(R.string.need_permissions_message).create()
    }

    private fun getTaskNotFoundDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.dialog_not_found_title)
            .setMessage(R.string.dialog_not_found_message).create()
    }

    private fun getRegistationSuccessDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.dialog_register).setMessage(message)
            .create()
    }

    private fun getLoginErrorDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(R.string.action_warning_notice)
            .setMessage(resources.getString(R.string.dialog_login_error) + " " + message).create()
    }

    private fun getLoginNotActiveDialog(): Dialog {
        return getBaseDialogBuilder()
            .setTitle(resources.getString(R.string.action_warning_notice))
            .setMessage(resources.getString(R.string.client_id_is_not_active)).create()
    }

    private fun getAirplaneDialog(): AlertDialog {
        return getBaseDialogBuilder().setTitle(R.string.action_warning)
            .setMessage(R.string.airplane_warning).create()
    }

    private fun getExitDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(resources.getString(R.string.action_exit))
            .setMessage(resources.getString(R.string.exit_message))
            .setPositiveButton(R.string.action_quit) { dialog, which ->
                customDialogListener!!.onDialogPositiveClick(
                    this@CustomDialogFragment
                )
            }
            .create()
    }

    private fun getNoConnectionDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(resources.getString(R.string.action_warning_notice))
            .setMessage(resources.getString(R.string.no_internet)).create()
    }

    private fun getSettingsDialog(): Dialog {
        return getBaseDialogBuilder()
            .setTitle(resources.getString(R.string.need_permissions_title))
            .setMessage(resources.getString(R.string.permission_message_settings))
            .setNegativeButton(R.string.cancel) { dialog: DialogInterface?, id: Int ->
                customDialogListener!!.onDialogNegativeClick(
                    this@CustomDialogFragment
                )
            }
            .create()
    }

    private fun getDocumentDialog(): Dialog {
        return getBaseDialogBuilder().setTitle(resources.getString(R.string.new_document))
            .setMessage(
                """
    ${resources.getString(R.string.new_document_message)}

    """.trimIndent()
                        + (resources.getString(R.string.order_no) + ": " +
                        parseOrderNo(orderNo))
            ).create()
    }

    private fun sendItemClickAsNegative() {
        customDialogListener!!.onDialogNegativeClick(this@CustomDialogFragment)
    }

    private fun getPasswordDialog(): Dialog {
        val builder = AlertDialog.Builder(
            requireActivity()
        )
        builder.setTitle(resources.getString(R.string.action_security_code))
            .setMessage(resources.getString(R.string.action_security_code_message))
        val input = EditText(requireActivity().baseContext)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp
        builder.setView(input)
        builder.setPositiveButton(
            R.string.action_ok
        ) { dialog: DialogInterface?, which: Int ->
            val pass = input.text.toString()
            if ("0000" == pass) customDialogListener!!.onDialogPositiveClick(this@CustomDialogFragment)
        }
        builder.setNegativeButton(R.string.cancel) { dialog: DialogInterface?, which: Int ->
            customDialogListener!!.onDialogNegativeClick(
                this@CustomDialogFragment
            )
        }
        return builder.create()
    }

    companion object {
        fun newInstance(dialogType: DialogType, orderNo: Int): CustomDialogFragment {
            val f = CustomDialogFragment()
            val args = Bundle()
            args.putInt("ordinal", dialogType.ordinal)
            args.putInt("orderNo", orderNo)
            f.arguments = args
            return f
        }

        fun newInstance(dialogType: DialogType, message: String?): CustomDialogFragment {
            val f = CustomDialogFragment()
            val args = Bundle()
            args.putInt("ordinal", dialogType.ordinal)
            args.putString("message", message)
            f.arguments = args
            return f
        }

        fun newInstance(dialogType: DialogType): CustomDialogFragment {
            val f = CustomDialogFragment()
            val args = Bundle()
            args.putInt("ordinal", dialogType.ordinal)
            f.arguments = args
            return f
        }

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
                        CustomDialogFragment::class.java.canonicalName,
                        " parsing exception : string from server based on :$orderNo"
                    )
                    "-"
                }
            } else "-"
        }
    }
}