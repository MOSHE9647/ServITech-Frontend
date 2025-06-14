package com.moviles.servitech.services

import android.content.Context
import android.widget.Toast
import com.moviles.servitech.R
import com.moviles.servitech.database.entities.PendingOperationEntity
import com.moviles.servitech.network.NetworkStatusTracker
import com.moviles.servitech.repositories.PendingOperationRepository
import com.moviles.servitech.repositories.helpers.DataSource
import com.moviles.servitech.services.helpers.ServicesHelper.currentDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Provider

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
    @ApplicationContext private val context: Context,
    private val repairRequestServiceProvider: Provider<RepairRequestService>,
    private val pendingOperationRepo: PendingOperationRepository,
    private val networkStatusTracker: NetworkStatusTracker,
) {

    /**
     * Provides access to the each entity service in the app.
     * Each of this services is used to handle offline synchronization of pending operations.
     *
     * For example, the RepairRequestService is used to handle
     * synchronization of pending operations related to repair requests.
     */
    private val repairRequestService get() = repairRequestServiceProvider.get()

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
    suspend fun syncPendingOperations() {
        if (currentDataSource(networkStatusTracker) == DataSource.Remote) {
            // Show a Toast message indicating that data is being synced with the server
            Toast.makeText(
                context,
                context.getString(R.string.syncing_data_with_server_msg),
                Toast.LENGTH_SHORT
            ).show()

            // Calls the syncPendingOperations function in the service of each entity
            repairRequestService.syncPendingOperations()
        }
    }

}