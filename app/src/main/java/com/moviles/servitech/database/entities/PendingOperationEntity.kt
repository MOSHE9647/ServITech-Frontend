package com.moviles.servitech.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moviles.servitech.common.Constants.PEND_OP_TABLE

/**
 * Represents a pending operation in the database.
 * This entity is used to store operations that need to be
 * synchronized with the server.
 */
@Entity(tableName = PEND_OP_TABLE)
data class PendingOperationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null, // Unique identifier for the pending operation
    val clazz: String, // Class name of the object
    val type: String,  // Type of operation (e.g., "insert", "update", "delete")
    val data: String,  // The data to be processed (SupportRequests, Articles, etc.)
)
