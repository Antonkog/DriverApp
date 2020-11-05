package com.abona_erp.driverapp.data.model

import com.abona_erp.driverapp.data.local.db.ConfirmationType
import com.google.gson.annotations.SerializedName
import java.util.*

data class ConfirmationItem(
    @SerializedName("ConfirmationType")
    val confirmationType: ConfirmationType? = null,

    @SerializedName("TimeStampConfirmationUTC")
    val timeStampConfirmationUTC: Date? = null,

    @SerializedName("MandantId")
    val mandantId: Int? = null,

    @SerializedName("TaskId")
    val taskId: Int? = null,

    @SerializedName("TaskChangeId")
    val taskChangeId: Int? = null,

    @SerializedName("Text")
    val text: String?
) {
    data class Builder(
        var confirmationType: ConfirmationType? = null,

        var timeStampConfirmationUTC: Date? = null,

        var mandantId: Int? = null,

        var taskId: Int? = null,

        var taskChangeId: Int? = null,

        var text: String?
    ) {
        fun confirmationType(confirmType: ConfirmationType?) = apply { this.confirmationType = confirmType }
        fun timeStampConfirmationUTC(confirmType: Date?) = apply { this.timeStampConfirmationUTC = confirmType }
        fun mandantId(mandantId: Int?) = apply { this.mandantId = mandantId }
        fun taskId(taskId: Int?) = apply { this.taskId = taskId }
        fun taskChangeId(taskChangeId: Int?) = apply { this.taskChangeId = taskChangeId }
        fun text(text: String?) = apply { this.text = text }

        fun build() = ConfirmationItem(
            confirmationType,
            timeStampConfirmationUTC,
            mandantId,
            taskId,
            taskChangeId,
            text
        )
    }

}