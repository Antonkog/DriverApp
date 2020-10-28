package com.abona_erp.driverapp.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import com.abona_erp.driverapp.data.model.ActivityStatus


data class ActivityWrapper(
    val activity: ActivityEntity,
    val buttonVisible: Boolean,
    val isLastActivity : Boolean
)