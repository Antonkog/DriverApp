package com.abona_erp.driver.app.data.local.db

import androidx.room.*
import com.abona_erp.driver.app.data.model.*

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
    @ColumnInfo val constacts: List<Contact>?,
    @Embedded val dangerousGoods : DangerousGoods?,
    @Embedded   val orderDetails : OrderDetails?,
    @Embedded   val palletExchange: PalletExchange?,
    @ColumnInfo val taskDueDateStart: String?,
    @ColumnInfo val taskDueDateFinish: String?,
    @ColumnInfo  val mandantId: Int,
    @ColumnInfo val kundenName: String?,
    @ColumnInfo  val confirmationType: ConfirmationType //used for saving ui state - based on user actions
)