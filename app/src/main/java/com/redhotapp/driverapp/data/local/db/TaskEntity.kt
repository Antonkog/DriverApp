package com.redhotapp.driverapp.data.local.db

import androidx.room.*
import com.redhotapp.driverapp.data.model.Contact

@Entity (tableName = "task_entity")
data class TaskEntity(
    @PrimaryKey  val taskId: Int,
    @ColumnInfo val actionType: Int,
    val activityIds: List<Int>,
    @ColumnInfo val changeReason: Int,
    @Embedded val address : TaskAddress?,
//    @TypeConverters(GsonToDbConverter::class) val constacts: List<Contact>?,
//    @TypeConverters(GsonToDbConverter::class) val dangerousGoods : List<DangerousGoods>?,
    @Embedded   val orderDetails : OrderDetails?,
    @Embedded   val palletExchange: PalletExchangeEntity?,
    @ColumnInfo val finished: String?,
    @ColumnInfo val taskDueDateStart: String?,
    @ColumnInfo val taskDueDateFinish: String?,
    @ColumnInfo val name: String?
)

/*
@SerializedName("AbonaTransferNr")
    val abonaTransferNr: String,
    @SerializedName("Activities")
    val activities: List<Activity>,
    @SerializedName("Address")
    val address: Address,
    @SerializedName("ChangeReason")
    val changeReason: Int,
    @SerializedName("ChangedItems")
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

 */