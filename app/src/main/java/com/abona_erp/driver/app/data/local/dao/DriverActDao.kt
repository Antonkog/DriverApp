package com.abona_erp.driver.app.data.local.dao

import android.provider.SyncStateContract.Helpers.insert
import androidx.lifecycle.LiveData
import androidx.room.*
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.data.model.CommResponseItem
import com.abona_erp.driver.app.ui.utils.DeviceUtils

@Dao
interface DriverActDao {
    @Query("SELECT * FROM activity_entity")
    fun getAll(): LiveData<List<ActivityEntity>>

    @Delete
    fun delete(activity: ActivityEntity)


    @Insert
    suspend fun insert(activities: List<ActivityEntity>)

    @Transaction
    suspend fun insertFromCommItem(commonItem: CommResponseItem) {
        if(commonItem.allTask.isNotEmpty()) {
            var strActList = commonItem.allTask.flatMap {
                it.activities.map {
                    ActivityEntity(it.activityId, it.mandantId, it.taskId, "" , it.started, it.finished, it.name)
                }
            }
            insert(strActList)
        }
    }
}