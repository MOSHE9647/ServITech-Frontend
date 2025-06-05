package com.moviles.servitech.services

import com.moviles.servitech.database.entities.PendingOperationEntity
import com.moviles.servitech.network.NetworkStatusTracker
import com.moviles.servitech.repositories.PendingOperationRepository
import javax.inject.Inject

/**
 * Service for managing pending operations.
 *
 * This service provides methods to interact with pending operations,
 * including adding, removing, and retrieving pending operations by entity type.
 *
 * @property pendingOperationRepo Repository for accessing pending operations data.
 * @property networkStatusTracker Tracks the network status to determine connectivity.
 */
class PendingOperationService @Inject constructor(
    private val pendingOperationRepo: PendingOperationRepository,
    private val networkStatusTracker: NetworkStatusTracker,
) {

    /**
     * Retrieves all pending operations.
     *
     * @return A list of all pending operations.
     */
    suspend fun getPendingOperationsByEntity(className: String): List<PendingOperationEntity> {
        return pendingOperationRepo.getPendingOperationsByEntity(className)
    }

    /**
     * Adds a new pending operation to the repository.
     *
     * @param pendingOperation The pending operation to be added.
     * @return Unit
     */
    suspend fun addPendingOperation(pendingOperation: PendingOperationEntity) {
        return pendingOperationRepo.addPendingOperation(pendingOperation)
    }

    /**
     * Removes a pending operation from the repository.
     *
     * @param pendingOperation The pending operation to be removed.
     * @return Unit
     */
    suspend fun removePendingOperation(pendingOperation: PendingOperationEntity) {
        return pendingOperationRepo.removePendingOperation(pendingOperation)
    }

    /**
     * Synchronizes pending operations with the server.
     *
     * This method checks the network status and calls the syncPendingOperations function
     * in the service of each entity if connected.
     */
    fun syncPendingOperations() {
        // TODO: Implement the logic to sync pending operations with the server
//        if (networkStatusTracker.isConnected.value) {
//            // Calls the syncPendingOperations function in the service of each entity
//            courseService.syncPendingOperations()
//        }
    }

}