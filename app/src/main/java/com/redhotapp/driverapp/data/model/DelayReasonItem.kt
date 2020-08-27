package com.redhotapp.driverapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class DelayReasonItem {
    /** WaitingReason.Oid  */
    @SerializedName("WaitingReasonId")
    @Expose
    var waitingReasongId: Int? = null

    /** TransportOrderStatusValue.Oid  */
    @SerializedName("ActivityId")
    @Expose
    var activityId: Int? = null

    /** WaitingReason.Name  */
    @SerializedName("ReasonText")
    @Expose
    var reasonText: String? = null

    /** WaitingReason Text  */
    @SerializedName("TranslatedReasonText")
    @Expose
    var translatedReasonText: String? = null

    /** WaitingReason.Code  */
    @SerializedName("Code")
    @Expose
    var code: Int? = null

    /** WaitingReason.SubCode  */
    @SerializedName("SubCode")
    @Expose
    var subCode: Int? = null

    /** From driver app  */
    @SerializedName("MandantId")
    @Expose
    var mandantId: Int? = null

    /** From driver app  */
    @SerializedName("TaskId")
    @Expose
    var taskId: Int? = null

    /** From driver app  */
    @SerializedName("TimestampUtc")
    @Expose
    var timestampUtc: Date? = null

    /** From driver app  */
    @SerializedName("DelayInMinutes")
    @Expose
    var delayInMinutes: Int? = null

    /** From driver app  */
    @SerializedName("DelaySource")
    @Expose
    var delaySource: DelaySource? = null

    /** From driver app  */
    @SerializedName("Comment")
    @Expose
    var comment: String? = null

}