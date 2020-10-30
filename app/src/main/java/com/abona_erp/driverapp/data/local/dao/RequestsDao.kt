package com.abona_erp.driverapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abona_erp.driverapp.data.local.db.DocumentEntity
import com.abona_erp.driverapp.data.local.db.RequestEntity

@Dao
interface  RequestsDao {
    @Query("SELECT * FROM request_entity")
    fun observeRequests(): LiveData<List<RequestEntity>>
    /**
     * Select all Documents from the documents table.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM request_entity")
    suspend fun getRequestList(): List<RequestEntity>


    @Insert
    suspend fun insert(request: RequestEntity)


    @Update
    suspend fun update(request: RequestEntity) : Int
}