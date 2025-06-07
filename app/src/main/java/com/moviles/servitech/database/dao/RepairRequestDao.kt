package com.moviles.servitech.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.moviles.servitech.common.Constants.REPAIR_REQ_TABLE
import com.moviles.servitech.database.entities.repairRequest.RepairRequestEntity
import com.moviles.servitech.database.entities.repairRequest.RepairRequestWithImagesEntity

@Dao
interface RepairRequestDao {

    @Query("SELECT * FROM $REPAIR_REQ_TABLE")
    suspend fun getAllRepairRequests(): List<RepairRequestEntity>

    @Transaction
    @Query("SELECT * FROM $REPAIR_REQ_TABLE")
    suspend fun getAllRepairRequestsWithImages(): List<RepairRequestWithImagesEntity>

    @Query("SELECT * FROM $REPAIR_REQ_TABLE WHERE receiptNumber = :receiptNumber")
    suspend fun getRepairRequestByReceiptNumber(receiptNumber: String): RepairRequestEntity

    @Transaction
    @Query("SELECT * FROM $REPAIR_REQ_TABLE WHERE receiptNumber = :receiptNumber")
    suspend fun getRepairRequestWithImagesByReceiptNumber(receiptNumber: String)
            : RepairRequestWithImagesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepairRequest(repairRequest: RepairRequestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepairRequests(repairRequests: List<RepairRequestEntity>): List<Long>

    @Update
    suspend fun updateRepairRequest(repairRequest: RepairRequestEntity): Int

    @Delete
    suspend fun deleteRepairRequest(repairRequest: RepairRequestEntity)

    @Query("DELETE FROM $REPAIR_REQ_TABLE WHERE receiptNumber = :receiptNumber")
    suspend fun deleteRepairRequestByReceiptNumber(receiptNumber: String)

    @Query("DELETE FROM $REPAIR_REQ_TABLE")
    suspend fun deleteAllRepairRequests()

}