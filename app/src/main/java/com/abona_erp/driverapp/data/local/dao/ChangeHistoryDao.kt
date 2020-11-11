package com.abona_erp.driverapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.abona_erp.driverapp.data.local.db.ChangeHistory

/**
 * Created by Anton Kogan email: Akogan777@gmail.com on 11/9/2020
 */
@Dao
interface ChangeHistoryDao {
    @Query("SELECT * FROM change_history")
    fun observeCommunication(): LiveData<List<ChangeHistory>>

    /**
     * Select all Logs from the change_history table.
     * @return all logs.
     */
    @Query("SELECT * FROM change_history")
    suspend fun getChangeHistoryList(): List<ChangeHistory>


    @Query("SELECT * FROM change_history WHERE status == 0")
    suspend fun getOfflineHistoryList(): List<ChangeHistory>

    @Insert
    suspend fun insert(change: ChangeHistory): Long

    @Update
    suspend fun update(change: ChangeHistory): Int

    /**
     * Delete all history.
     */
    @Query("DELETE FROM change_history")
    suspend fun deleteChangeHistory()

}