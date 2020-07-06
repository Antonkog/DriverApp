package com.redhotapp.driverapp.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "tasks")
data class Task (@PrimaryKey var taskId : Int, var status: String?)