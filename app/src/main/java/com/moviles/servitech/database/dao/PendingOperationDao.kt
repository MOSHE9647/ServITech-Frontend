package com.moviles.servitech.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.moviles.servitech.common.Constants.PEND_OP_TABLE
import com.moviles.servitech.database.entities.PendingOperationEntity

@Dao
interface PendingOperationDao {

    @Query("SELECT * FROM $PEND_OP_TABLE")
    suspend fun getAllPendingOperations(): List<PendingOperationEntity>

    @Query("SELECT * FROM $PEND_OP_TABLE WHERE clazz = :className")
    suspend fun getPendingOperationsByEntity(className: String): List<PendingOperationEntity>

    @Insert
    suspend fun addPendingOperation(pendingOperation: PendingOperationEntity)

    @Delete
    suspend fun removePendingOperation(pendingOperation: PendingOperationEntity)

}