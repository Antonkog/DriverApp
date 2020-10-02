package com.abona_erp.driver.app.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class DelayReasonItem (
    /** WaitingReason.Oid  */
    @SerializedName("WaitingReasonId")
    var waitingReasongId: Int? = null,

    /** TransportOrderStatusValue.Oid  */
    @SerializedName("ActivityId")
    var activityId: Int? = null,

    /** WaitingReason.Name  */
    @SerializedName("ReasonText")
    var reasonText: String? = null,

    /** WaitingReason Text  */
    @SerializedName("TranslatedReasonText")
    var translatedReasonText: String? = null,

    /** WaitingReason.Code  */
    @SerializedName("Code")
    var code: Int? = null,

    /** WaitingReason.SubCode  */
    @SerializedName("SubCode")
    var subCode: Int? = null,

    /** From driver app  */
    @SerializedName("MandantId")
    var mandantId: Int? = null,

    /** From driver app  */
    @SerializedName("TaskId")
    var taskId: Int? = null,

    /** From driver app  */
    @SerializedName("TimestampUtc")
    var timestampUtc: Date? = null,

    /** From driver app  */
    @SerializedName("DelayInMinutes")
    var delayInMinutes: Int? = null,

    /** From driver app  */
    @SerializedName("DelaySource")
    var delaySource: DelaySource? = null,

    /** From driver app  */
    @SerializedName("Comment")
    var comment: String? = null

)