package com.moviles.servitech.services.helpers

import android.util.Log
import com.google.gson.Gson
import com.moviles.servitech.R
import com.moviles.servitech.common.Utils.logError
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.core.session.SessionManager
import com.moviles.servitech.model.enums.OperationType
import com.moviles.servitech.model.enums.UserRole
import com.moviles.servitech.network.NetworkStatusTracker
import com.moviles.servitech.repositories.helpers.DataSource
import kotlin.reflect.full.callSuspend

object ServicesHelper {

    /**
     * Retrieves the authentication token from the session manager.
     * If the token is not available, it returns null.
     *
     * @return The authentication token as a String, or null if not available.
     */
    suspend fun getAuthTokenOrError(sessionManager: SessionManager): String? =
        sessionManager.getToken()

    /**
     * Retrieves the currently logged-in user from the session manager.
     * If no user is logged in, it returns null.
     *
     * @return The User object representing the logged-in user, or null if no user is logged in.
     */
    suspend fun getLoggedUser(sessionManager: SessionManager) =
        sessionManager.getLoggedUser()


    /**
     * Checks if the logged-in user has the specified role.
     * If the user is not logged in or does not have the role,
     * it returns false.
     *
     * @param sessionManager The SessionManager instance to retrieve the logged user.
     * @param role The UserRole to check against the logged user's role.
     * @return True if the user has the specified role, false otherwise.
     */
    suspend fun checkRoleOrError(sessionManager: SessionManager, role: UserRole): Boolean {
        val userRole = getLoggedUser(sessionManager)?.role.orEmpty()
        return userHaveRole(userRole, role)
    }

    /**
     * Checks if the provided role matches the specified user role.
     * This method compares the roleToCompare with the provided UserRole.
     *
     * @param roleToCompare The role to compare, represented as a String.
     * @param role The UserRole to compare against.
     */
    fun userHaveRole(roleToCompare: String, role: UserRole): Boolean {
        try {
            val userRole = UserRole.valueOf(roleToCompare.uppercase())
            return when (userRole) {
                role -> true
                else -> false
            }
        } catch (e: Exception) {
            Log.e("ServicesHelper", "Error checking user role: ${e.message}")
            return false // If an error occurs, assume the user does not have the role
        }
    }

    /**
     * Determines the current data source based on network connectivity.
     */
    fun currentDataSource(networkStatusTracker: NetworkStatusTracker): DataSource =
        if (networkStatusTracker.isConnected.value) DataSource.Remote else DataSource.Local

    /**
     * Synchronizes a specific operation (insert, update, delete) with the server.
     * This method dynamically calls the appropriate method on the target class
     *
     * It uses reflection to find the method based on the operation type and the class of the operation data.
     * If the method is a suspend function, it calls it using `callSuspend`.
     *
     * If the operation is successful, it returns the result as a [Result<Any>].
     * If an error occurs, it logs the error and returns null.
     *
     * @param T The type of the operation data.
     * @param R The type of the result expected from the operation.
     * @param operationType The type of operation to perform (insert, update, delete).
     * @param operationData The data associated with the operation, which will be passed to the method.
     * @param targetClass The class containing the method to be called.
     * @param parameters The parameters to be passed to the method, excluding the instance of the target class.
     * @return A [Result<Any>] containing the result of the operation, or null if an error occurred.
     */
    suspend inline fun <reified T, R> syncOperation(
        stringProvider: AndroidStringProvider,
        className: String,
        operationType: OperationType,
        operationData: T,
        targetClass: Any,
        vararg parameters: Any?
    ): R? {
        return try {
            // Get the class of the operation data and the method name based on the operation type
            val className =
                T::class.simpleName?.replaceFirstChar { it.uppercaseChar() } ?: return null
            val methodName = when (operationType) {
                OperationType.INSERT -> "create$className"
                OperationType.UPDATE -> "update$className"
                OperationType.DELETE -> "delete$className"
            }

            // Find the method in the target class that matches the method name and parameter count
            val method = targetClass::class.members.firstOrNull {
                it.name == methodName && it.parameters.size == parameters.size + 1 // +1 for 'this' instance
            }
                ?: throw NoSuchMethodException("No method '$methodName' with ${parameters.size} parameter(s) found in ${targetClass::class.simpleName}")

            // Check if the method is a suspend function and call it accordingly
            @Suppress("UNCHECKED_CAST")
            val result = if (method.isSuspend) {
                method.callSuspend(targetClass, *parameters) as R
            } else {
                method.call(targetClass, *parameters) as R
            }

            result //<- Return the result of the operation
        } catch (e: Exception) {
            logError(
                stringProvider = stringProvider,
                className = className,
                throwable = e,
                messageResId = com.moviles.servitech.R.string.error_syncing_operation_for_msg,
                operationType.label(), T::class.simpleName ?: "Unknown", operationData
            )
            null
        }
    }

    /**
     * Retrieves an entity from a JSON string and transforms it using the provided function.
     * This method uses Gson to deserialize the JSON string into an entity of type [From],
     * and then applies the transformation function to convert it to type [To].
     *
     * @param From The type of the entity to be deserialized from JSON.
     * @param To The type of the entity to be returned after transformation.
     * @param data The JSON string to be deserialized.
     * @param transform A function that takes an entity of type [From] and returns an entity of type [To].
     * @return An entity of type [To] if successful, or null if an error occurs during deserialization.
     */
    inline fun <reified From, To> retrieveEntityFromJson(
        data: String,
        stringProvider: AndroidStringProvider,
        className: String,
        transform: (From) -> To?
    ): To? {
        return try {
            val entity = Gson().fromJson(data, From::class.java)
            transform(entity)
        } catch (e: Exception) {
            logError(
                stringProvider = stringProvider,
                className = className,
                throwable = e,
                messageResId = R.string.po_deserialization_error_msg
            )
            null
        }
    }

}