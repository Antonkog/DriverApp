package com.redhotapp.driverapp.data.model


import com.google.gson.annotations.SerializedName
import com.redhotapp.driverapp.data.model.abona.CommItem
import com.redhotapp.driverapp.data.model.abona.DelayReasonItem
import com.redhotapp.driverapp.data.model.abona.DocumentItem

data class CommResponseItem(
    @SerializedName("AllAppFileInterchangeItem")
    val allAppFileInterchangeItem: Any,
    @SerializedName("AllDocumentCommItem")
    val allDocumentCommItem: List<DocumentItem>,
    @SerializedName("AllTask")
    val allTask: List<AllTask>,
    @SerializedName("AllTaskCommItem")
    val allTaskCommItem: List<Any>,
    @SerializedName("CommunicationItem")
    val communicationItem: CommItem,
    @SerializedName("DelayReasons")
    val delayReasons: List<DelayReasonItem>,
    @SerializedName("ExceptionText")
    val exceptionText: Any,
    @SerializedName("HttpResponseMessage")
    val httpResponseMessage: Any,
    @SerializedName("IsException")
    val isException: Boolean,
    @SerializedName("IsSuccess")
    val isSuccess: Boolean,
    @SerializedName("LogText")
    val logText: Any,
    @SerializedName("Text")
    val text: String,
    @SerializedName("TransportAuftragOid")
    val transportAuftragOid: Int
)