package com.moviles.servitech.repositories

import android.content.Context
import androidx.room.withTransaction
import com.moviles.servitech.R
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.database.AppDatabase
import com.moviles.servitech.database.dao.ImageDao
import com.moviles.servitech.database.dao.RepairRequestDao
import com.moviles.servitech.database.entities.repairRequest.RepairRequestEntity
import com.moviles.servitech.model.RepairRequest
import com.moviles.servitech.model.mappers.modelToEntityList
import com.moviles.servitech.model.mappers.responseToModelList
import com.moviles.servitech.model.mappers.toCreateRequest
import com.moviles.servitech.model.mappers.toEntity
import com.moviles.servitech.model.mappers.toEntityList
import com.moviles.servitech.model.mappers.toModel
import com.moviles.servitech.model.mappers.toUpdateRequest
import com.moviles.servitech.model.mappers.withImagesToModelList
import com.moviles.servitech.network.handlers.ApiHandler.handleActionSafely
import com.moviles.servitech.network.handlers.ApiHandler.handleApiCall
import com.moviles.servitech.network.requests.repairRequest.UpdateRepairRequest
import com.moviles.servitech.network.services.RepairRequestApiService
import com.moviles.servitech.repositories.helpers.DataSource
import com.moviles.servitech.repositories.helpers.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Sealed class representing the result of an repair request CRUD operation.
 *
 * It implements the [Result] interface and can either be a success with data of type T,
 * or an error with a message and optional field errors.
 *
 * @param T The type of data returned on success.
 */
sealed class RepairRequestResult<out T> : Result<T> {
    /**
     * Represents a successful repair request CRUD operation.
     * Contains the repair request data.
     *
     * @param T The type of data returned on success.
     * @property data The repair request data.
     */
    data class Success<out T>(val data: T) : RepairRequestResult<T>()

    /**
     * Represents an error that occurred during repair request CRUD operation.
     * Contains an error message and optional field errors.
     *
     * @property message The error message describing the issue.
     * @property fieldErrors A map of field names to error messages, if applicable.
     */
    data class Error(
        val message: String,
        val fieldErrors: Map<String, String> = emptyMap()
    ) : RepairRequestResult<Nothing>()
}

/**
 * Repository for handling repair request CRUD operations.
 * It provides methods for creating, reading, updating, and deleting repair requests,
 * and handles both remote and local data sources.
 *
 * @property imageDao The DAO for image operations related to repair requests.
 * @property repRequestDao The DAO for repair request operations.
 * @property repReqApiService The API service for repair request operations.
 * @property stringProvider Provides string resources for error messages.
 */
class RepairRequestRepository @Inject constructor(
    private val db: AppDatabase,
    private val imageDao: ImageDao,
    private val repRequestDao: RepairRequestDao,
    private val repReqApiService: RepairRequestApiService,
    private val stringProvider: AndroidStringProvider,
    @ApplicationContext private val context: Context
) {

    // The name of the class for logging purposes
    private val className = this::class.java.simpleName

    // The type of the imageable entity for images related to repair requests
    private val imageableType = RepairRequest::class.java.simpleName

    /**
     * Retrieves all repair requests from the specified data source.
     *
     * @param source The data source to retrieve the repair requests from (Remote or Local).
     * @param authToken The authentication token for remote API calls.
     * @return A [RepairRequestResult] containing a list of repair requests or an error.
     */
    suspend fun getAllRepairRequests(
        source: DataSource,
        authToken: String
    ): RepairRequestResult<List<RepairRequest>> {
        // Handle the API call based on the data source
        return when (source) {
            // If the source is remote, make an API call to get all repair requests
            DataSource.Remote -> handleApiCall(
                // API call to get all repair requests from the server
                remoteCall = { repReqApiService.getAllRepairRequests(authToken) },
                // Local call to get all repair requests from the local database
                localCall = { this.getAllRepairRequestsFromDB() },
                // Handle errors from the API call
                onCallError = { msg, fields -> RepairRequestResult.Error(msg, fields) },
                // Handle successful response from the API call
                onRemoteSuccess = {
                    handleActionSafely(
                        onSuccess = {
                            // Clear existing repair requests in the local database
                            // and insert the new ones from the API response
                            repRequestDao.deleteAllRepairRequests()
                            repRequestDao.insertRepairRequests(it.modelToEntityList())
                            RepairRequestResult.Success(it)
                        },
                        onError = { msg, fields -> RepairRequestResult.Error(msg, fields) },
                        logClass = className,
                        errorMessage = stringProvider.getString(R.string.unknown_error)
                    )
                },
                // Transform the API response to a list of RepairRequest models
                transformTo = { it.responseToModelList() },
                // Log class name for debugging purposes
                logClass = className,
                // Error message to return in case of an unknown error
                errorMessage = stringProvider.getString(R.string.unknown_error)
            )

            // If the source is local, retrieve all repair requests from the local database
            DataSource.Local -> this.getAllRepairRequestsFromDB()
        }
    }

    /**
     * Retrieves a repair request by its receipt number or ID from the specified data source.
     *
     * @param source The data source to retrieve the repair request from (Remote or Local).
     * @param authToken The authentication token for remote API calls.
     * @param receiptNumber The optional receipt number of the repair request to retrieve.
     * @param repairRequestID The optional ID of the repair request to retrieve.
     * @return A [RepairRequestResult] containing the repair request or an error.
     */
    suspend fun getRepairRequestByReceiptNumberOrID(
        source: DataSource,
        authToken: String,
        receiptNumber: String? = null,
        repairRequestID: Long? = null
    ): RepairRequestResult<RepairRequest?> {
        return when (source) {
            DataSource.Remote -> handleApiCall(
                remoteCall = {
                    repReqApiService.getRepairRequestByReceiptNumber(authToken, receiptNumber!!)
                },
                localCall = {
                    this.getRepairRequestByReceiptNumberOrIdFromDB(receiptNumber, repairRequestID)
                },
                onCallError = { msg, fields -> RepairRequestResult.Error(msg, fields) },
                onRemoteSuccess = {
                    // Create the repair request in the local database after a successful API call
                    this.createRepairRequestInDB(it as RepairRequest)
                    RepairRequestResult.Success(it)
                },
                transformTo = { it.toModel() },
                logClass = className,
                errorMessage = stringProvider.getString(R.string.unknown_error)
            )

            DataSource.Local -> {
                this.getRepairRequestByReceiptNumberOrIdFromDB(receiptNumber, repairRequestID)
            }
        }
    }

    /**
     * Creates a new repair request in the specified data source.
     *
     * @param source The data source to create the repair request in (Remote or Local).
     * @param authToken The authentication token for remote API calls.
     * @param repairRequest The repair request to create.
     * @return A [RepairRequestResult] containing the created repair request or an error.
     */
    suspend fun createRepairRequest(
        source: DataSource,
        authToken: String,
        repairRequest: RepairRequest
    ): RepairRequestResult<Any> {
        return when (source) {
            DataSource.Remote -> handleApiCall(
                remoteCall = {
                    val createRepRequest = repairRequest.toCreateRequest()
                    repReqApiService.createRepairRequest(
                        authToken,
                        createRepRequest.customerName,
                        createRepRequest.customerPhone,
                        createRepRequest.customerEmail,
                        createRepRequest.articleName,
                        createRepRequest.articleType,
                        createRepRequest.articleBrand,
                        createRepRequest.articleModel,
                        createRepRequest.articleSerialNumber,
                        createRepRequest.articleAccesories,
                        createRepRequest.articleProblem,
                        createRepRequest.repairStatus,
                        createRepRequest.repairDetails,
                        createRepRequest.repairPrice,
                        createRepRequest.receivedAt,
                        createRepRequest.repairedAt,
                        createRepRequest.images
                    )
                },
                localCall = { this.createRepairRequestInDB(repairRequest) },
                onCallError = { msg, fields -> RepairRequestResult.Error(msg, fields) },
                onRemoteSuccess = {
                    this.createRepairRequestInDB(it as RepairRequest)
                    RepairRequestResult.Success(it)
                },
                transformTo = { it.toModel() },
                logClass = className,
                errorMessage = stringProvider.getString(R.string.unknown_error)
            )

            DataSource.Local -> this.createRepairRequestInDB(repairRequest)
        }
    }

    /**
     * Updates an existing repair request in the specified data source.
     *
     * @param source The data source to update the repair request in (Remote or Local).
     * @param authToken The authentication token for remote API calls.
     * @param repairRequest The repair request to update.
     * @return A [RepairRequestResult] containing the updated repair request or an error.
     */
    suspend fun updateRepairRequest(
        source: DataSource,
        authToken: String,
        repairRequest: RepairRequest
    ): RepairRequestResult<Any> {
        return when (source) {
            DataSource.Remote -> handleApiCall(
                remoteCall = {
                    val updateRepRequest: UpdateRepairRequest = repairRequest.toUpdateRequest()
                    repReqApiService.updateRepairRequest(authToken, updateRepRequest)
                },
                localCall = { this.updateRepairRequestInDB(repairRequest) },
                onCallError = { msg, fields -> RepairRequestResult.Error(msg, fields) },
                onRemoteSuccess = {
                    this.updateRepairRequestInDB(repairRequest)
                    RepairRequestResult.Success(it)
                },
                transformTo = { it.toModel() },
                logClass = className,
                errorMessage = stringProvider.getString(R.string.unknown_error)
            )

            DataSource.Local -> this.updateRepairRequestInDB(repairRequest)
        }
    }

    /**
     * Deletes a repair request by its receipt number or ID from the specified data source.
     *
     * @param source The data source to delete the repair request from (Remote or Local).
     * @param authToken The authentication token for remote API calls.
     * @param receiptNumber The optional receipt number of the repair request to delete.
     * @param repairRequestID The optional ID of the repair request to delete.
     * @return A [RepairRequestResult] indicating success or an error.
     */
    suspend fun deleteRepairRequestByReceiptNumberOrID(
        source: DataSource,
        authToken: String,
        receiptNumber: String? = null,
        repairRequestID: Long? = null
    ): RepairRequestResult<Unit> {
        return when (source) {
            DataSource.Remote -> handleApiCall(
                remoteCall = { repReqApiService.deleteRepairRequest(authToken, receiptNumber!!) },
                localCall = { this.deleteRepairRequestInDB(receiptNumber, repairRequestID) },
                onCallError = { msg, fields -> RepairRequestResult.Error(msg, fields) },
                onRemoteSuccess = {
                    this.deleteRepairRequestInDB(receiptNumber, repairRequestID)
                    RepairRequestResult.Success(it)
                },
                transformTo = { it },
                logClass = className,
                errorMessage = stringProvider.getString(R.string.unknown_error)
            )

            DataSource.Local -> this.deleteRepairRequestInDB(receiptNumber, repairRequestID)
        }
    }

    /*************************************************************************************
     ******************************** AUXILIARY FUNCTIONS ********************************
     *************************************************************************************/

    /**
     * Auxiliary private function for avoiding boilerplate code in the public methods.
     * This function retrieves all repair requests from the local database.
     *
     * @return A [RepairRequestResult] indicating success or an error.
     */
    private suspend fun getAllRepairRequestsFromDB(): RepairRequestResult<List<RepairRequest>> {
        return handleActionSafely(
            onSuccess = {
                val repairRequestList = repRequestDao.getAllRepairRequests()
                    .withImagesToModelList(context)
                RepairRequestResult.Success(repairRequestList)
            },
            onError = { msg, fields -> RepairRequestResult.Error(msg, fields) },
            logClass = className,
            errorMessage = stringProvider.getString(R.string.unknown_error)
        )
    }

    /**
     * Auxiliary private function for avoiding boilerplate code in the public methods.
     * This function retrieves a repair request by its receipt number or ID from the local database.
     *
     * @param receiptNumber The optional receipt number of the repair request to retrieve.
     * @param repairRequestID The optional ID of the repair request to retrieve.
     * @return A [RepairRequestResult] containing the repair request or an error.
     */
    private suspend fun getRepairRequestByReceiptNumberOrIdFromDB(
        receiptNumber: String? = null,
        repairRequestID: Long? = null
    ): RepairRequestResult<RepairRequest?> {
        return handleActionSafely(
            onSuccess = {
                var repairRequestFromDB = when {
                    receiptNumber != null -> {
                        repRequestDao.getRepairRequestWithImagesByReceiptNumber(receiptNumber)
                    }

                    repairRequestID != null -> {
                        repRequestDao.getRepairRequestById(repairRequestID)
                    }

                    else -> null
                }
                RepairRequestResult.Success(repairRequestFromDB?.toModel(context))
            },
            onError = { msg, fields -> RepairRequestResult.Error(msg, fields) },
            logClass = className,
            errorMessage = stringProvider.getString(R.string.unknown_error)
        )
    }

    /**
     * Auxiliary private function for avoiding boilerplate code in the public methods.
     * This function creates a new repair request in the local database.
     *
     * @param repairRequest The repair request to create.
     * @return A [RepairRequestResult] indicating success or an error.
     */
    private suspend fun createRepairRequestInDB(
        repairRequest: RepairRequest
    ): RepairRequestResult<Any> {
        return handleActionSafely(
            onSuccess = {
                db.withTransaction {
                    val repReqId = repRequestDao.insertRepairRequest(repairRequest.toEntity())
                    imageDao.insertImages(
                        repairRequest.images?.toEntityList(
                            imageableType = imageableType,
                            imageableId = repReqId
                        ) ?: emptyList()
                    )
                }
                RepairRequestResult.Success(Unit)
            },
            onError = { msg, fields -> RepairRequestResult.Error(msg, fields) },
            logClass = className,
            errorMessage = stringProvider.getString(R.string.unknown_error)
        )
    }

    /**
     * Auxiliary private function for avoiding boilerplate code in the public methods.
     * This function updates an existing repair request in the local database.
     *
     * @param repairRequest The repair request to update.
     * @return A [RepairRequestResult] indicating success or an error.
     */
    private suspend fun updateRepairRequestInDB(
        repairRequest: RepairRequest
    ): RepairRequestResult<Any> {
        return handleActionSafely(
            onSuccess = {
                repRequestDao.updateRepairRequest(repairRequest.toEntity())
                RepairRequestResult.Success(Unit)
            },
            onError = { msg, fields -> RepairRequestResult.Error(msg, fields) },
            logClass = className,
            errorMessage = stringProvider.getString(R.string.unknown_error)
        )
    }

    /**
     * Auxiliary private function for avoiding boilerplate code in the public methods.
     * This function deletes a repair request by its receipt number or ID from the local database.
     *
     * @param receiptNumber The optional receipt number of the repair request to delete.
     * @param repairRequestID The optional ID of the repair request to delete.
     * @return A [RepairRequestResult] indicating success or an error.
     */
    private suspend fun deleteRepairRequestInDB(
        receiptNumber: String? = null,
        repairRequestID: Long? = null
    ): RepairRequestResult<Unit> {
        return handleActionSafely(
            onSuccess = {
                db.withTransaction {
                    val repRequest: RepairRequestEntity? = when {
                        receiptNumber != null -> {
                            repRequestDao.getRepairRequestByReceiptNumber(receiptNumber)
                        }

                        repairRequestID != null -> {
                            repRequestDao.getRepairRequestById(repairRequestID)?.toEntity()
                        }

                        else -> null
                    } ?: return@withTransaction RepairRequestResult.Error(
                        stringProvider.getString(R.string.request_not_found)
                    )

                    repRequestDao.deleteRepairRequest(repRequest!!)
                    repRequestDao.deleteRepairRequestImagesById(
                        type = imageableType,
                        repRequestId = repRequest.id!!.toLong()
                    )
                }
                RepairRequestResult.Success(Unit)
            },
            onError = { msg, fields -> RepairRequestResult.Error(msg, fields) },
            logClass = className,
            errorMessage = stringProvider.getString(R.string.unknown_error)
        )
    }

}