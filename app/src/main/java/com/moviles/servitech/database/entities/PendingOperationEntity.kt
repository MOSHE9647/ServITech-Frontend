package com.moviles.servitech.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moviles.servitech.common.Constants.PEND_OP_TABLE

/**
 * Represents a pending operation in the database.
 * This entity is used to store operations that need to be
 * synchronized with the server.
 *
 * @param id Unique identifier for the pending operation.
 * @param clazz Class name of the object (e.g., "SupportRequest", "Article").
 * @param type Type of operation (e.g., "insert", "update", "delete").
 * @param data The data to be processed, serialized as a JSON string.
 */
@Entity(tableName = PEND_OP_TABLE)
data class PendingOperationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val clazz: String,
    val type: String,
    val data: String,  // (SupportRequests, Articles, etc.)
)
