package com.abona_erp.driver.app.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.data.local.db.ConfirmationType
import com.abona_erp.driver.app.data.local.db.DocumentEntity
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.data.model.CommResponseItem

@Dao
interface DocumentsDao {
    @Query("SELECT * FROM documents")
    fun observeDocuments(): LiveData<List<DocumentEntity>>

    /**
     * Select all Documents from the documents table.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM documents")
    suspend fun getDocuments(): List<DocumentEntity>

    /**
     * Delete all Documents.
     */
    @Query("DELETE FROM documents")
    suspend fun deleteDocuments()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(docs: List<DocumentEntity?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(docs: DocumentEntity)

    @Delete
    fun delete(user: DocumentEntity)

}