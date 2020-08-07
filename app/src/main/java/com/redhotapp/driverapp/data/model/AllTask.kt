package com.redhotapp.driverapp.data.model


import com.google.gson.annotations.SerializedName

data class AllTask(
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
    @SerializedName("ChangedItems")
    val changedItems: List<Int>,
    @SerializedName("Contacts")
    val contacts: List<Contact>,
    @SerializedName("DangerousGoods")
    val dangerousGoods: DangerousGoods,
    @SerializedName("Description")
    val description: Any,
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
    @SerializedName("Notes")
    val notes: Any,
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
    @SerializedName("ReferenceIdCustomer2")
    val referenceIdCustomer2: Any,
    @SerializedName("Status")
    val status: Int,
    @SerializedName("SwapInfoItem")
    val swapInfoItem: Any,
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