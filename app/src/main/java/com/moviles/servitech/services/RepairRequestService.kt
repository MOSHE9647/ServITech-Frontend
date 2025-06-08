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

/**
 * Service for managing repair requests.
 *
 * This service provides methods to create, update, delete, and retrieve repair requests,
 * handling both local and remote data sources.
 *
 * It also manages offline operations by storing pending operations
 * when the device is offline,
 * allowing synchronization with the server when connectivity is restored.
 *
 * @property context The application context, used for accessing resources and application-level operations.
 * @property pendingOperationServiceProvider Provides an instance of PendingOperationService for managing offline operations.
 * @property networkStatusTracker Tracks the network status to determine connectivity.
 * @property repairRequestRepo Repository for accessing repair request data.
 * @property stringProvider Provides string resources for error messages and other strings.
 * @property sessionManager Manages user sessions, including authentication tokens and user data.
 */
class RepairRequestService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pendingOperationServiceProvider: Provider<PendingOperationService>,
    private val networkStatusTracker: NetworkStatusTracker,
    private val repairRequestRepo: RepairRequestRepository,
    private val stringProvider: AndroidStringProvider,
    private val sessionManager: SessionManager
) {
    /**
     * Provides the current instance of PendingOperationService.
     * This is used to manage offline operations related to repair requests.
     */
    private val pendingOperationService get() = pendingOperationServiceProvider.get()

    // The class name of the object being managed by this service.
    private val objectClass = RepairRequest::class.java.simpleName

    /**
     * Retrieves the authentication token from the session manager.
     * If the token is not available, it returns an error message.
     *
     * @return The authentication token as a String, or null if not available.
     */
    private suspend fun getAuthTokenOrError(): String? =
        sessionManager.getToken()

    /**
     * Determines the current data source based on network connectivity.
     */
    private fun currentDataSource(): DataSource =
        if (networkStatusTracker.isConnected.value) DataSource.Remote else DataSource.Local

    /**
     * Creates an error result with a message from the string provider.
     *
     * @param messageResId The resource ID of the error message.
     * @param args Optional arguments to format the message.
     */
    private fun error(messageResId: Int, vararg args: Any?): RepairRequestResult.Error =
        RepairRequestResult.Error(stringProvider.getString(messageResId, args))

    /**
     * Handles offline operations by storing the operation in the pending operations database.
     * This method is called when the device is offline and an operation needs to be queued for later synchronization.
     *
     * @param type The type of operation being performed (insert, update, delete).
     * @param data The data associated with the operation, serialized to JSON.
     */
    private suspend fun handleOfflineOperation(type: OperationType, data: Any) {
        val jsonData = Gson().toJson(data)
        pendingOperationService.addPendingOperation(
            PendingOperationEntity(clazz = objectClass, type = type.label(), data = jsonData)
        )
    }

    /**
     * Retrieves all repair requests from the current data source.
     * If the user is not authenticated, it returns an error.
     *
     * @return A [RepairRequestResult] containing a list of [RepairRequest] objects
     */
    suspend fun getAllRepairRequests(): RepairRequestResult<List<RepairRequest>> {
        val token = getAuthTokenOrError() ?: return error(R.string.error_authentication_required)
        return repairRequestRepo.getAllRepairRequests(currentDataSource(), token)
    }

    /**
     * Retrieves a repair request by its receipt number or ID.
     * If the user is not authenticated or the parameters are null, it returns an error.
     *
     * @param receiptNumber The receipt number of the repair request.
     * @param repairRequestID The ID of the repair request.
     * @return A [RepairRequestResult] containing the [RepairRequest] object or an error.
     */
    suspend fun getRepairRequestByReceiptNumberOrID(
        receiptNumber: String?,
        repairRequestID: Long?
    ): RepairRequestResult<RepairRequest?> {
        val token = getAuthTokenOrError() ?: return error(R.string.error_authentication_required)
        if (receiptNumber == null) return error(R.string.error_null_parameter_msg, "receiptNumber")
        if (repairRequestID == null) return error(R.string.error_null_parameter_msg, "id")

        return repairRequestRepo.getRepairRequestByReceiptNumberOrID(
            currentDataSource(), token, receiptNumber, repairRequestID
        )
    }

    /**
     * Creates a new repair request.
     * If the user is not authenticated, it returns an error.
     * If the operation is performed while offline, it queues the operation for later synchronization.
     *
     * @param repairRequest The [RepairRequest] object to be created.
     * @return A [RepairRequestResult] indicating success or failure of the operation.
     */
    suspend fun createRepairRequest(repairRequest: RepairRequest): RepairRequestResult<Any> {
        val token = getAuthTokenOrError() ?: return error(R.string.error_authentication_required)

        val result =
            repairRequestRepo.createRepairRequest(currentDataSource(), token, repairRequest)
        if (currentDataSource() == DataSource.Local && result is RepairRequestResult.Success) {
            handleOfflineOperation(OperationType.INSERT, repairRequest.toEntity())
        }
        return result
    }

    /**
     * Updates an existing repair request.
     * If the user is not authenticated, it returns an error.
     * If the operation is performed while offline, it queues the operation for later synchronization.
     *
     * @param repairRequest The [RepairRequest] object to be updated.
     * @return A [RepairRequestResult] indicating success or failure of the operation.
     */
    suspend fun updateRepairRequest(repairRequest: RepairRequest): RepairRequestResult<Any> {
        val token = getAuthTokenOrError() ?: return error(R.string.error_authentication_required)

        val result =
            repairRequestRepo.updateRepairRequest(currentDataSource(), token, repairRequest)
        if (currentDataSource() == DataSource.Local && result is RepairRequestResult.Success) {
            handleOfflineOperation(OperationType.UPDATE, repairRequest.toEntity())
        }
        return result
    }

    /**
     * Deletes a repair request by its receipt number or ID.
     * If the user is not authenticated or the parameters are null, it returns an error.
     * If the operation is performed while offline, it queues the operation for later synchronization.
     *
     * @param receiptNumber The receipt number of the repair request.
     * @param repairRequestID The ID of the repair request.
     * @return A [RepairRequestResult] indicating success or failure of the operation.
     */
    suspend fun deleteRepairRequestByReceiptNumberOrID(
        receiptNumber: String?,
        repairRequestID: Long?
    ): RepairRequestResult<Unit> {
        val token = getAuthTokenOrError() ?: return error(R.string.error_authentication_required)
        if (receiptNumber == null) return error(R.string.error_null_parameter_msg, "receiptNumber")
        if (repairRequestID == null) return error(R.string.error_null_parameter_msg, "id")

        val result = repairRequestRepo.deleteRepairRequestByReceiptNumberOrID(
            currentDataSource(), token, receiptNumber, repairRequestID
        )

        if (currentDataSource() == DataSource.Local && result is RepairRequestResult.Success) {
            val identifier = receiptNumber.ifEmpty { repairRequestID }
            handleOfflineOperation(OperationType.DELETE, identifier)
        }

        return result
    }
}