package com.redhotapp.driverapp.data.local.db

import androidx.room.*
import com.redhotapp.driverapp.data.model.Address
import com.redhotapp.driverapp.data.model.Contact
import com.redhotapp.driverapp.data.model.OrderDetails
import com.redhotapp.driverapp.data.model.PalletExchange

@Entity (tableName = "task_entity")
data class TaskEntity(
    @PrimaryKey  val taskId: Int,
    @ColumnInfo val actionType: Int,
    val activityIds: List<Int>,
    @ColumnInfo val changeReason: Int,
    @Embedded val address : Address,
//    @TypeConverters(GsonToDbConverter::class) val constacts: List<Contact>?,
//    @TypeConverters(GsonToDbConverter::class) val dangerousGoods : List<DangerousGoods>?,
    @Embedded   val orderDetails : OrderDetails,
    @Embedded   val palletExchange: PalletExchange,
    @ColumnInfo val finished: Boolean,
    @ColumnInfo val taskDueDateStart: String,
    @ColumnInfo val taskDueDateFinish: String,
    @ColumnInfo val name: String
)
