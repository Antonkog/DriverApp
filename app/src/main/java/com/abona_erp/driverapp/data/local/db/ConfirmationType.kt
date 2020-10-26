package com.abona_erp.driverapp.data.local.db

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.abona_erp.driverapp.R

enum class ConfirmationType(//green
    val code: Int
) {
    RECEIVED(0),  //gray
    READ(1),  //orange, app to server flow doesn't has read
    SYNCED_WITH_SERVER(2);

    companion object {
        private val values = values()
        fun getByCode(code: Int) = values.firstOrNull { it.code == code } ?: RECEIVED
        fun getColor(context: Context, confirm: ConfirmationType): Int {
            return when (confirm) {
                RECEIVED -> ResourcesCompat.getColor(context.resources, R.color.confirm_gray, null)
                READ -> ResourcesCompat.getColor(context.resources, R.color.confirm_orange, null)
                SYNCED_WITH_SERVER -> ResourcesCompat.getColor(
                    context.resources,
                    R.color.confirm_green,
                    null
                )
            }
        }
    }
}