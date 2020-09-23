package com.abona_erp.driver.app.data.model


import com.abona_erp.driver.app.data.local.db.TaskStatus
import com.google.gson.annotations.SerializedName

data class TaskItem(
    @SerializedName("AbonaTransferNr")
    val abonaTransferNr: String,
    @SerializedName("ActionType")
    val actionType: Int,
    @SerializedName("Activities")
    val activities: List<Activity>,
    @SerializedName("Address")
    val address: Address,
    @SerializedName("ChangeReason")
    val changeReason: Int,
    @SerializedName("Contacts")
    val contacts: List<Contact>,
    @SerializedName("DangerousGoods")
    val dangerousGoods: DangerousGoods,
    @SerializedName("KundenName")
    val kundenName: String,
    @SerializedName("KundenNr")
    val kundenNr: Int,
    @SerializedName("MandantId")
    val mandantId: Int,
    @SerializedName("MandantName")
    val mandantName: String,
    @SerializedName("NextTaskId")
    val nextTaskId: Int,
    @SerializedName("OrderDetails")
    val orderDetails: OrderDetails,
    @SerializedName("OrderNo")
    val orderNo: Int,
    @SerializedName("PalletExchange")
    val palletExchange: PalletExchange,
    @SerializedName("PalletsAmount")
    val palletsAmount: Int,
    @SerializedName("PercentFinishedActivities")
    val percentFinishedActivities: Int,
    @SerializedName("PreviousTaskId")
    val previousTaskId: Int,
    @SerializedName("ReferenceIdCustomer1")
    val referenceIdCustomer1: String,
    @SerializedName("Status")
    val status: TaskStatus,
    @SerializedName("TaskChangeId")
    val taskChangeId: Int,
    @SerializedName("TaskDetails")
    val taskDetails: TaskDetails,
    @SerializedName("TaskDueDateFinish")
    val taskDueDateFinish: String,
    @SerializedName("TaskDueDateStart")
    val taskDueDateStart: String,
    @SerializedName("TaskId")
    val taskId: Int,
    @SerializedName("VehicleNextTaskId")
    val vehicleNextTaskId: Int,
    @SerializedName("VehiclePreviousTaskId")
    val vehiclePreviousTaskId: Int
)