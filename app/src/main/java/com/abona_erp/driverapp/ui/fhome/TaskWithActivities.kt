package com.abona_erp.driverapp.ui.fhome

import androidx.room.Embedded
import androidx.room.Relation
import com.abona_erp.driverapp.data.local.db.ActivityEntity
import com.abona_erp.driverapp.data.local.db.TaskEntity

/**
 * Created by a.kogan on 10/7/2020
 */
data class TaskWithActivities(
    @Embedded var taskEntity: TaskEntity, @Relation(
        parentColumn = "taskId",
        entityColumn = "taskpId"
    ) var activities: List<ActivityEntity>
)