package com.abona_erp.driverapp.data.local.db

import androidx.room.*

@Entity(tableName = "request_entity")

data class RequestEntity(
    @PrimaryKey var timeCreated: Long,
    @ColumnInfo val requestJsonBody: String,
    @ColumnInfo val responseJsonBody: String?,
    @ColumnInfo val completedSuccess: Boolean
)