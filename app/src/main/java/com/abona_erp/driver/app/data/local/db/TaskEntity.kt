package com.abona_erp.driver.app.data.local.db

import androidx.room.*
import com.abona_erp.driver.app.data.model.Address
import com.abona_erp.driver.app.data.model.Contact
import com.abona_erp.driver.app.data.model.OrderDetails
import com.abona_erp.driver.app.data.model.PalletExchange
import com.google.gson.annotations.SerializedName

@Entity (tableName = "task_entity",
    primaryKeys = ["taskId", "mandantId"]
)
data class TaskEntity(
    @ColumnInfo  val taskId: Int,
    @ColumnInfo val actionType: ActionType,
    @ColumnInfo val status: TaskStatus,
    val activityIds: List<Int>,
    @ColumnInfo val changeReason: Int,
    @Embedded val address : Address?,
//    @TypeConverters(GsonToDbConverter::class) val constacts: List<Contact>?,
//    @TypeConverters(GsonToDbConverter::class) val dangerousGoods : List<DangerousGoods>?,
    @Embedded   val orderDetails : OrderDetails?,
    @Embedded   val palletExchange: PalletExchange?,
    @ColumnInfo val finished: Boolean,
    @ColumnInfo val taskDueDateStart: String?,
    @ColumnInfo val taskDueDateFinish: String?,
    @ColumnInfo  val mandantId: Int,
    @ColumnInfo val kundenName: String?,
    @ColumnInfo  val confirmationType: ConfirmationType //used for saving ui state - based on user actions
)