package com.moviles.servitech.services

import com.moviles.servitech.database.entities.PendingOperationEntity
import com.moviles.servitech.network.NetworkStatusTracker
import com.moviles.servitech.repositories.PendingOperationRepository
import javax.inject.Inject

class PendingOperationService @Inject constructor(
    private val pendingOperationRepo: PendingOperationRepository,
    private val networkStatusTracker: NetworkStatusTracker,
) {

    suspend fun getPendingOperationsByEntity(className: String): List<PendingOperationEntity> {
        return pendingOperationRepo.getPendingOperationsByEntity(className)
    }

    suspend fun addPendingOperation(pendingOperation: PendingOperationEntity) {
        return pendingOperationRepo.addPendingOperation(pendingOperation)
    }

    suspend fun removePendingOperation(pendingOperation: PendingOperationEntity) {
        return pendingOperationRepo.removePendingOperation(pendingOperation)
    }

    suspend fun syncPendingOperations() {
//        if (networkStatusTracker.isConnected.value) {
//            // Calls the syncPendingOperations function in the service of each entity
//            courseService.syncPendingOperations()
//        }
    }

}