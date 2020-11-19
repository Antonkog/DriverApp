package com.abona_erp.driverapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.abona_erp.driverapp.data.local.db.ChangeHistory
import com.abona_erp.driverapp.data.local.db.DelayReasonEntity

/**
 * Created by Anton Kogan email: Akogan777@gmail.com on 11/9/2020
 */
@Dao
interface DelayReasonsDao {
    @Query("SELECT * FROM delay_reason")
    fun observeDelayReasons(): LiveData<List<DelayReasonEntity>>

    @Insert
    suspend fun insert(change: List<DelayReasonEntity>)

    @Insert
    suspend fun insert(change: DelayReasonEntity): Long

    @Update
    suspend fun update(change: DelayReasonEntity): Int

    @Query("DELETE FROM delay_reason")
    suspend fun deleteAll()
}