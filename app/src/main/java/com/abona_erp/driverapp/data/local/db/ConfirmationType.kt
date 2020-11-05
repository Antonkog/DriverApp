package com.abona_erp.driverapp.data.local.db

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.abona_erp.driverapp.R

enum class ConfirmationType(// we sync task with server by this model.
    val code: Int
) {
    RECEIVED(0),  //gray default value
    TASK_CONFIRMED_BY_DEVICE(1),  //orange, //when firebase receive it, task app to server flow doesn't has read in UI.
    TASK_CONFIRMED_BY_USER(2); //green, when user opens task and it is synced with Abona

    companion object {
        private val values = values()
        fun getByCode(code: Int) = values.firstOrNull { it.code == code } ?: RECEIVED
        fun getColor(context: Context, confirm: ConfirmationType): Int {
            return when (confirm) {
                RECEIVED -> ResourcesCompat.getColor(context.resources, R.color.confirm_gray, null)
                TASK_CONFIRMED_BY_DEVICE -> ResourcesCompat.getColor(context.resources, R.color.confirm_orange, null)
                TASK_CONFIRMED_BY_USER -> ResourcesCompat.getColor(
                    context.resources,
                    R.color.confirm_green,
                    null
                )
            }
        }
    }
}