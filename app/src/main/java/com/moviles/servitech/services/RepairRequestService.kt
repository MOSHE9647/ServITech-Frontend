package com.moviles.servitech.services

import android.content.Context
import com.google.gson.Gson
import com.moviles.servitech.R
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.core.session.SessionManager
import com.moviles.servitech.database.entities.PendingOperationEntity
import com.moviles.servitech.model.RepairRequest
import com.moviles.servitech.model.enums.OperationType
import com.moviles.servitech.model.mappers.toEntity
import com.moviles.servitech.network.NetworkStatusTracker
import com.moviles.servitech.repositories.RepairRequestRepository
import com.moviles.servitech.repositories.RepairRequestResult
import com.moviles.servitech.repositories.helpers.DataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Provider

class RepairRequestService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pendingOperationServiceProvider: Provider<PendingOperationService>,
    private val networkStatusTracker: NetworkStatusTracker,
    private val repairRequestRepo: RepairRequestRepository,
    private val stringProvider: AndroidStringProvider,
    private val sessionManager: SessionManager
) {

    // The name of the class for logging purposes
    private val className = this::class.java.simpleName

    // Pending operation service to handle operations that need to be retried later
    private val pendingOperationService: PendingOperationService
        get() = pendingOperationServiceProvider.get()

    // The class name of the object being managed by this service,
    // used for identifying pending operations related to repair requests
    private val objectClass = RepairRequest::class.java.simpleName

    /**
     * Fetches all repair requests from the repository.
     *
     * This method checks the network connectivity status and retrieves
     * repair requests from the appropriate data source (remote or local).
     *
     * If (somehow) the user is not authenticated, it returns an error message.
     *
     * @return A [RepairRequestResult] containing a list of [RepairRequest] or an error message.
     */
    suspend fun getAllRepairRequests(): RepairRequestResult<List<RepairRequest>> {
        // Get the authentication token from the session manager
        val authToken: String = sessionManager.getToken()
            ?: return RepairRequestResult.Error(
                stringProvider.getString(R.string.error_authentication_required)
            )

        // Check network connectivity and fetch repair requests
        return when (networkStatusTracker.isConnected.value) {
            true -> repairRequestRepo.getAllRepairRequests(DataSource.Remote, authToken)
            false -> {
                // If offline, fetch from local storage
                repairRequestRepo.getAllRepairRequests(DataSource.Local, authToken)
            }
        }
    }

    /**
     * Fetches a repair request by its receipt number or ID.
     *
     * This method checks the network connectivity status and retrieves
     * the repair request from the appropriate data source (remote or local).
     *
     * If the user is not authenticated, it returns an error message.
     *
     * @param receiptNumber The receipt number of the repair request.
     * @param repairRequestID The ID of the repair request.
     * @return A [RepairRequestResult] containing the [RepairRequest] or an error message.
     */
    suspend fun getRepairRequestByReceiptNumberOrID(
        receiptNumber: String? = null,
        repairRequestID: Long? = null
    ): RepairRequestResult<RepairRequest?> {
        // Get the authentication token from the session manager
        val authToken: String = sessionManager.getToken()
            ?: return RepairRequestResult.Error(
                stringProvider.getString(R.string.error_authentication_required)
            )

        // Validate input parameters
        require(receiptNumber != null) {
            stringProvider.getString(R.string.error_null_parameter_msg, "receiptNumber")
        }
        require(repairRequestID != null) {
            stringProvider.getString(R.string.error_null_parameter_msg, "id")
        }

        // Check network connectivity and fetch the repair request
        return when (networkStatusTracker.isConnected.value) {
            true -> repairRequestRepo.getRepairRequestByReceiptNumberOrID(
                DataSource.Remote, authToken, receiptNumber, repairRequestID
            )

            false -> {
                // If offline, fetch from local storage
                repairRequestRepo.getRepairRequestByReceiptNumberOrID(
                    DataSource.Local, authToken, receiptNumber, repairRequestID
                )
            }
        }
    }

    /**
     * Creates a new repair request.
     *
     * This method checks the network connectivity status and creates
     * the repair request in the appropriate data source (remote or local).
     *
     * If the user is not authenticated, it returns an error message.
     *
     * @param repairRequest The [RepairRequest] object to be created.
     * @return A [RepairRequestResult] indicating success or failure of the operation.
     */
    suspend fun createRepairRequest(repairRequest: RepairRequest): RepairRequestResult<Any> {
        // Get the authentication token from the session manager
        val authToken: String = sessionManager.getToken()
            ?: return RepairRequestResult.Error(
                stringProvider.getString(R.string.error_authentication_required)
            )

        // Validate the repair request object
        requireNotNull(repairRequest) {
            stringProvider.getString(R.string.error_null_parameter_msg, "repairRequest")
        }

        // Check network connectivity and create the repair request
        return when (networkStatusTracker.isConnected.value) {
            true -> {
                repairRequestRepo.createRepairRequest(DataSource.Remote, authToken, repairRequest)
            }

            false -> {
                // If offline, save the request for later processing
                repairRequestRepo.createRepairRequest(DataSource.Local, authToken, repairRequest)
                    .also { result ->
                        if (result is RepairRequestResult.Success) {
                            // If the request was saved successfully, add it to pending operations
                            pendingOperationService.addPendingOperation(
                                PendingOperationEntity(
                                    clazz = objectClass,
                                    type = OperationType.INSERT.label(),
                                    data = Gson().toJson(repairRequest.toEntity())
                                )
                            )
                        }
                    }
            }
        }
    }

    /**
     * Updates an existing repair request.
     *
     * This method checks the network connectivity status and updates
     * the repair request in the appropriate data source (remote or local).
     *
     * If the user is not authenticated, it returns an error message.
     *
     * @param repairRequest The [RepairRequest] object to be updated.
     * @return A [RepairRequestResult] indicating success or failure of the operation.
     */
    suspend fun updateRepairRequest(
        repairRequest: RepairRequest
    ): RepairRequestResult<Any> {
        // Get the authentication token from the session manager
        val authToken: String = sessionManager.getToken()
            ?: return RepairRequestResult.Error(
                stringProvider.getString(R.string.error_authentication_required)
            )

        // Validate the repair request object
        requireNotNull(repairRequest) {
            stringProvider.getString(R.string.error_null_parameter_msg, "repairRequest")
        }

        // Check network connectivity and update the repair request
        return when (networkStatusTracker.isConnected.value) {
            true -> {
                repairRequestRepo.updateRepairRequest(DataSource.Remote, authToken, repairRequest)
            }

            false -> {
                // If offline, save the request for later processing
                repairRequestRepo.updateRepairRequest(DataSource.Local, authToken, repairRequest)
                    .also { result ->
                        if (result is RepairRequestResult.Success) {
                            // If the request was saved successfully, add it to pending operations
                            pendingOperationService.addPendingOperation(
                                PendingOperationEntity(
                                    clazz = objectClass,
                                    type = OperationType.UPDATE.label(),
                                    data = Gson().toJson(repairRequest.toEntity())
                                )
                            )
                        }
                    }

            }
        }
    }

    /**
     * Deletes a repair request by its receipt number or ID.
     *
     * This method checks the network connectivity status and deletes
     * the repair request in the appropriate data source (remote or local).
     *
     * If the user is not authenticated, it returns an error message.
     *
     * @param receiptNumber The receipt number of the repair request.
     * @param repairRequestID The ID of the repair request.
     * @return A [RepairRequestResult] indicating success or failure of the operation.
     */
    suspend fun deleteRepairRequestByReceiptNumberOrID(
        receiptNumber: String? = null,
        repairRequestID: Long? = null
    ): RepairRequestResult<Unit> {
        // Get the authentication token from the session manager
        val authToken: String = sessionManager.getToken()
            ?: return RepairRequestResult.Error(
                stringProvider.getString(R.string.error_authentication_required)
            )

        // Validate input parameters
        require(receiptNumber != null) {
            stringProvider.getString(R.string.error_null_parameter_msg, "receiptNumber")
        }
        require(repairRequestID != null) {
            stringProvider.getString(R.string.error_null_parameter_msg, "id")
        }

        // Check network connectivity and delete the repair request
        return when (networkStatusTracker.isConnected.value) {
            true -> repairRequestRepo.deleteRepairRequestByReceiptNumberOrID(
                DataSource.Remote, authToken, receiptNumber, repairRequestID
            )

            false -> {
                // If offline, save the request for later processing
                repairRequestRepo.deleteRepairRequestByReceiptNumberOrID(
                    DataSource.Local, authToken, receiptNumber, repairRequestID
                ).also { result ->
                    if (result is RepairRequestResult.Success) {
                        // If the request was saved successfully, add it to pending operations
                        pendingOperationService.addPendingOperation(
                            PendingOperationEntity(
                                clazz = objectClass,
                                type = OperationType.DELETE.label(),
                                data = Gson().toJson(
                                    when {
                                        receiptNumber.isNotEmpty() -> receiptNumber
                                        else -> repairRequestID
                                    }
                                )
                            )
                        )
                    }
                }
            }
        }
    }

}