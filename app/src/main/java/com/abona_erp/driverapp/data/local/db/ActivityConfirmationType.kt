package com.abona_erp.driverapp.data.local.db

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.abona_erp.driverapp.R


/**
 * that class is for internal usage only, as we don't use it in Activity.kt
 */
enum class ActivityConfirmationType(
    val code: Int
) {
    RECEIVED(0), // initial value
    CHANGED_BY_USER(1),
    SYNCED_WITH_ABONA(2);

    companion object {
        private val values = values()
        fun getByCode(code: Int) = values.firstOrNull { it.code == code } ?: RECEIVED
        fun getColor(context: Context, confirm: ActivityConfirmationType): Int {
            return when (confirm) {
                RECEIVED -> ResourcesCompat.getColor(context.resources, R.color.confirm_gray, null)
                CHANGED_BY_USER -> ResourcesCompat.getColor(context.resources, R.color.confirm_gray, null) //offline mode ,we dont show it to user
                SYNCED_WITH_ABONA -> ResourcesCompat.getColor(
                    context.resources,
                    R.color.confirm_green,
                    null
                )
            }
        }
    }
}