package com.moviles.servitech.repositories

import com.moviles.servitech.database.dao.PendingOperationDao
import com.moviles.servitech.database.entities.PendingOperationEntity
import javax.inject.Inject

class PendingOperationRepository @Inject constructor(
    private val pendingOperationDao: PendingOperationDao
) {

    suspend fun getPendingOperationsByEntity(className: String): List<PendingOperationEntity> {
        return pendingOperationDao.getPendingOperationsByEntity(className)
    }

    suspend fun addPendingOperation(pendingOperation: PendingOperationEntity) {
        pendingOperationDao.addPendingOperation(pendingOperation)
    }

    suspend fun removePendingOperation(pendingOperation: PendingOperationEntity) {
        pendingOperationDao.removePendingOperation(pendingOperation)
    }

}