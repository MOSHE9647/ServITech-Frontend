package com.moviles.servitech.services

import android.content.Context
import com.google.gson.Gson
import com.moviles.servitech.R
import com.moviles.servitech.common.Utils.logError
import com.moviles.servitech.common.Utils.logInfo
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.core.session.SessionManager
import com.moviles.servitech.database.entities.PendingOperationEntity
import com.moviles.servitech.database.entities.repairRequest.RepairRequestWithImagesEntity
import com.moviles.servitech.model.RepairRequest
import com.moviles.servitech.model.enums.OperationType
import com.moviles.servitech.model.enums.UserRole
import com.moviles.servitech.model.mappers.toEntity
import com.moviles.servitech.model.mappers.toModel
import com.moviles.servitech.network.NetworkStatusTracker
import com.moviles.servitech.repositories.RepairRequestRepository
import com.moviles.servitech.repositories.RepairRequestResult
import com.moviles.servitech.repositories.helpers.DataSource
import com.moviles.servitech.services.helpers.ServicesHelper.checkRoleOrError
import com.moviles.servitech.services.helpers.ServicesHelper.currentDataSource
import com.moviles.servitech.services.helpers.ServicesHelper.getAuthTokenOrError
import com.moviles.servitech.services.helpers.ServicesHelper.retrieveEntityFromJson
import com.moviles.servitech.services.helpers.ServicesHelper.syncOperation
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
    val objectClass: String = RepairRequest::class.java.simpleName

    // The class name of the service, used for logging and debugging purposes.
    private val className = RepairRequestService::class.java.simpleName

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
        val token = getAuthTokenOrError(sessionManager)
            ?: return error(R.string.error_authentication_required)

        // Check if the user has admin role before proceeding
        if (!checkRoleOrError(sessionManager, UserRole.ADMIN)) {
            return error(R.string.error_user_not_authorized_msg)
        }

        // Call the repository method to get all repair requests
        return repairRequestRepo.getAllRepairRequests(
            currentDataSource(networkStatusTracker),
            token
        )
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
        val token = getAuthTokenOrError(sessionManager)
            ?: return error(R.string.error_authentication_required)

        // Check if the user has admin role before proceeding
        if (!checkRoleOrError(sessionManager, UserRole.ADMIN)) {
            return error(R.string.error_user_not_authorized_msg)
        }

        // Validate that at least one of receiptNumber or repairRequestID is provided
        if (receiptNumber == null && repairRequestID == null) {
            return error(R.string.error_null_parameter_msg, "[receiptNumber, id]")
        }

        // Call the repository method to get the repair request by receipt number or ID
        return repairRequestRepo.getRepairRequestByReceiptNumberOrID(
            currentDataSource(networkStatusTracker), token, receiptNumber, repairRequestID
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
        val token = getAuthTokenOrError(sessionManager)
            ?: return error(R.string.error_authentication_required)
        val authToken = "Bearer $token"

        // Check if the user has admin role before proceeding
        if (!checkRoleOrError(sessionManager, UserRole.ADMIN)) {
            return error(R.string.error_user_not_authorized_msg)
        }

        val dataSource = currentDataSource(networkStatusTracker)
        val result = repairRequestRepo.createRepairRequest(dataSource, authToken, repairRequest)
        if (dataSource == DataSource.Local && result is RepairRequestResult.Success) {
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
        val token = getAuthTokenOrError(sessionManager)
            ?: return error(R.string.error_authentication_required)

        // Check if the user has admin role before proceeding
        if (!checkRoleOrError(sessionManager, UserRole.ADMIN)) {
            return error(R.string.error_user_not_authorized_msg)
        }

        val dataSource = currentDataSource(networkStatusTracker)
        val result = repairRequestRepo.updateRepairRequest(dataSource, token, repairRequest)
        if (dataSource == DataSource.Local && result is RepairRequestResult.Success) {
            handleOfflineOperation(OperationType.UPDATE, repairRequest.toEntity())
        }
        return result
    }

    /**
     * Deletes a repair request by its receipt number or ID.
     * If the user is not authenticated or the parameters are null, it returns an error.
     * If the operation is performed while offline, it queues the operation for later synchronization.
     *
     * @param repairRequest The [RepairRequest] object to be deleted and which contains
     *                      either a receipt number or an ID.
     * @return A [RepairRequestResult] indicating success or failure of the operation.
     */
    suspend fun deleteRepairRequest(repairRequest: RepairRequest): RepairRequestResult<Unit> {
        val token = getAuthTokenOrError(sessionManager)
            ?: return error(R.string.error_authentication_required)

        // Check if the user has admin role before proceeding
        if (!checkRoleOrError(sessionManager, UserRole.ADMIN)) {
            return error(R.string.error_user_not_authorized_msg)
        }

        // Validate that at least one of receiptNumber or id is provided
        val dataSource = currentDataSource(networkStatusTracker)
        val result = repairRequestRepo.deleteRepairRequestByReceiptNumberOrID(
            dataSource, token, repairRequest.receiptNumber, repairRequest.id?.toLong()
        )

        // If the operation is successful and performed offline, handle the offline operation
        if (dataSource == DataSource.Local && result is RepairRequestResult.Success) {
            val identifier = repairRequest.receiptNumber ?: repairRequest.id ?: return error(
                R.string.error_null_parameter_msg, "[receiptNumber, id]"
            )
            handleOfflineOperation(OperationType.DELETE, identifier)
        }

        return result
    }

    /**
     * Synchronizes all pending operations for the current object class.
     * This method retrieves all pending operations from the database and attempts to sync them with the server.
     * If the device is offline or the authentication token is not available, it logs an error.
     *
     * It processes each pending operation by calling the appropriate syncOperation method
     * based on the operation type (insert, update, delete).
     */
    suspend fun syncPendingOperations() {
        // Check if the current data source is Remote, if not, return early
        if (currentDataSource(networkStatusTracker) != DataSource.Remote) return

        // Ensure the user is authenticated and has a valid token
        val token = getAuthTokenOrError(sessionManager)
        if (token.isNullOrEmpty()) {
            logError(
                messageResId = R.string.error_authentication_required,
                stringProvider = stringProvider,
                className = className
            )
            return
        }

        // Check if the user has admin role before proceeding
        if (!checkRoleOrError(sessionManager, UserRole.ADMIN)) {
            logError(
                messageResId = R.string.error_user_not_authorized_msg,
                stringProvider = stringProvider,
                className = className
            )
            return
        }

        // Retrieve all pending operations for the current object class
        val pendOperations = pendingOperationService.getPendingOperationsByEntity(objectClass)
        if (pendOperations.isEmpty()) {
            logInfo(
                messageResId = R.string.no_pending_operations_msg,
                stringProvider = stringProvider,
                className = className
            )
            return
        }

        // Log the start of the synchronization process
        logInfo(
            stringProvider = stringProvider,
            className = className,
            messageResId = R.string.syncing_pending_operations_msg,
            objectClass
        )

        // Iterate through each pending operation and attempt to sync it
        pendOperations.forEach { operation ->
            // Deserialize the operation data into a RepairRequest object
            val repairRequest: RepairRequest =
                retrieveEntityFromJson<RepairRequestWithImagesEntity, RepairRequest>(
                    data = operation.data,
                    stringProvider = stringProvider,
                    className = className,
                    transform = { repReqWithImages -> repReqWithImages.toModel() }
                ) ?: return@forEach

            // Try to synchronize the operation using the syncOperation method
            val syncResult: RepairRequestResult<Any> =
                syncOperation(
                    stringProvider = stringProvider,
                    className = className,
                    operationType = OperationType.valueOf(operation.type),
                    operationData = repairRequest,
                    targetClass = this,
                    repairRequest //<- Parameter to be passed to the method
                ) ?: return@forEach

            // Handle the result of the synchronization
            when (syncResult) {
                is RepairRequestResult.Success<*> -> {
                    logInfo(
                        stringProvider = stringProvider,
                        className = className,
                        messageResId = R.string.success_syncing_operation_msg,
                        operation.type, objectClass, repairRequest
                    )
                    pendingOperationService.removePendingOperation(operation)
                }

                is RepairRequestResult.Error -> {
                    logError(
                        stringProvider = stringProvider,
                        className = className,
                        throwable = null,
                        messageResId = R.string.error_syncing_operation_msg,
                        operation.type, objectClass, repairRequest, syncResult.message
                    )
                }
            }
        }
    }
}