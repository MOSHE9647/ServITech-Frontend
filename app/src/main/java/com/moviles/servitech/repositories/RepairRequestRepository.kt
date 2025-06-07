package com.moviles.servitech.repositories

import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.database.dao.ImageDao
import com.moviles.servitech.database.dao.RepairRequestDao
import com.moviles.servitech.model.RepairRequest
import com.moviles.servitech.model.mappers.entityToModelList
import com.moviles.servitech.model.mappers.responseToModelList
import com.moviles.servitech.network.handlers.handleApiCall
import com.moviles.servitech.network.services.RepairRequestApiService
import com.moviles.servitech.repositories.helpers.DataSource
import com.moviles.servitech.repositories.helpers.Result
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
    private val imageDao: ImageDao,
    private val repRequestDao: RepairRequestDao,
    private val repReqApiService: RepairRequestApiService,
    private val stringProvider: AndroidStringProvider
) {

    // The name of the class for logging purposes
    private val className = this::class.java.simpleName

    suspend fun getAllRepairRequests(
        source: DataSource,
        authToken: String
    ): RepairRequestResult<List<RepairRequest>> {
        return when (source) {
            DataSource.Remote -> handleApiCall(
                remoteCall = { repReqApiService.getAllRepairRequests(authToken) },
                localCall = suspend {
                    RepairRequestResult.Success(
                        repRequestDao.getAllRepairRequests().entityToModelList()
                    )
                },
                onError = { msg, fields -> RepairRequestResult.Error(msg, fields) },
                onSuccess = { RepairRequestResult.Success(it) },
                logClass = className,
                transform = { it.responseToModelList() },
            )

            DataSource.Local -> {
                RepairRequestResult.Success(
                    repRequestDao.getAllRepairRequests().entityToModelList()
                )
            }
        }
    }

}