package com.moviles.servitech.repositories

import com.moviles.servitech.database.dao.PendingOperationDao
import com.moviles.servitech.database.entities.PendingOperationEntity
import javax.inject.Inject

/**
 * Repository for managing pending operations in the database.
 *
 * This repository provides methods to interact with the PendingOperationDao,
 * allowing for retrieval, addition, and removal of pending operations.
 *
 * @property pendingOperationDao The DAO for accessing pending operations in the database.
 */
class PendingOperationRepository @Inject constructor(
    private val pendingOperationDao: PendingOperationDao
) {

    /**
     * Retrieves all pending operations from the database.
     *
     * @return A list of all pending operation entities.
     */
    suspend fun getPendingOperationsByEntity(className: String): List<PendingOperationEntity> {
        return pendingOperationDao.getPendingOperationsByEntity(className)
    }

    /**
     * Adds a new pending operation to the database.
     *
     * @param pendingOperation The pending operation entity to be added.
     */
    suspend fun addPendingOperation(pendingOperation: PendingOperationEntity) {
        pendingOperationDao.addPendingOperation(pendingOperation)
    }

    /**
     * Removes a pending operation from the database.
     *
     * @param pendingOperation The pending operation entity to be removed.
     */
    suspend fun removePendingOperation(pendingOperation: PendingOperationEntity) {
        pendingOperationDao.removePendingOperation(pendingOperation)
    }

}