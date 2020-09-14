package com.abona_erp.driver.app.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResultOfAction {
    @SerializedName("IsSuccess")
    var isSuccess = false

    @SerializedName("IsException")
    var isException = false

    @SerializedName("Text")
    var text: String? = null

    @SerializedName("CommunicationItem")
    var commItem: CommItem? = null

    @SerializedName("AllTask")
    var allTask: List<TaskItem>? = null

    @SerializedName("DelayReasons")
    var delayReasonItems: List<DelayReasonItem>? = null

    override fun toString(): String {
        return "ResultOfAction{" +
                "isSuccess=" + isSuccess +
                ", isException=" + isException +
                ", text='" + text + '\'' +
                ", commItem=" + commItem +
                ", allTask=" + allTask +
                ", delayReasonItems=" + delayReasonItems +
                '}'
    }
}