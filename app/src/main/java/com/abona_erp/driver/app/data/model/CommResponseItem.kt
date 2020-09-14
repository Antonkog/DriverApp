package com.abona_erp.driver.app.data.model


import com.google.gson.annotations.SerializedName

data class CommResponseItem(
    @SerializedName("AllAppFileInterchangeItem")
    val allAppFileInterchangeItem: Any,
    @SerializedName("AllDocumentCommItem")
    val allDocumentCommItem: List<Any>,
    @SerializedName("AllTask")
    val allTask: List<AllTask>,
    @SerializedName("AllTaskCommItem")
    val allTaskCommItem: List<Any>,
    @SerializedName("CommunicationItem")
    val communicationItem: CommItem,
    @SerializedName("DelayReasons")
    val delayReasons: List<DelayReasonItem>,
    @SerializedName("ExceptionText")
    val exceptionText: String,
    @SerializedName("HttpResponseMessage")
    val httpResponseMessage: String,
    @SerializedName("IsException")
    val isException: Boolean,
    @SerializedName("IsSuccess")
    val isSuccess: Boolean,
    @SerializedName("LogText")
    val logText: String,
    @SerializedName("Text")
    val text: String,
    @SerializedName("TransportAuftragOid")
    val transportAuftragOid: Int
)