package com.moviles.servitech.core.session

import android.content.Context
import android.util.Log
import com.moviles.servitech.R
import com.moviles.servitech.database.dao.UserSessionDao
import com.moviles.servitech.database.entities.user.UserSessionEntity
import com.moviles.servitech.model.User
import com.moviles.servitech.model.mappers.toEntity
import com.moviles.servitech.model.mappers.toUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SessionManager is responsible for managing user sessions in the application.
 * It provides methods to save, clear, and retrieve session information,
 * including user details, access tokens, and session validity.
 *
 * It uses a UserSessionDao to interact with the database
 * and stores session data in a UserSessionEntity.
 *
 * It also provides a StateFlow to observe session messages,
 * which can be used to display messages related to session management,
 * such as errors or session expiration notifications.
 *
 * This class is annotated with @Singleton to ensure that only one instance
 * of SessionManager exists throughout the application lifecycle.
 *
 * @param context The application context, used for accessing resources and application-level operations.
 * @param userSessionDao The DAO for accessing user session data in the database.
 */
@Singleton
class SessionManager @Inject constructor (
    @ApplicationContext private val context: Context,
    private val userSessionDao: UserSessionDao
) {

    /**
     * A StateFlow that holds session messages, which can be used to notify the user
     * about session-related events, such as errors or expiration.
     *
     * The initial value is null, indicating no message is set.
     */
    private val _sessionMessage = MutableStateFlow<String?>(null)
    val sessionMessage: StateFlow<String?> = _sessionMessage.asStateFlow()

    /**
     * Saves the user session information, including the access token,
     * expiration time, and user details.
     *
     * @param token The access token for the user session.
     * @param expiresIn The duration in seconds for which the session is valid.
     * @param user The User object containing user details.
     */
    suspend fun saveSession(token: String, expiresIn: Long, user: User) {
        val expiresAt = System.currentTimeMillis() + (expiresIn * 1000)
        val userSession = UserSessionEntity(
            id = 1, // Only one session is allowed
            user = user.toEntity(), token = token, expiresIn = expiresAt
        )
        userSessionDao.saveSession(userSession)
    }

    /**
     * Clears the current user session from the database.
     * If an error occurs during the clearing process,
     * it logs the error and sets a session message indicating the error.
     */
    suspend fun clearSession() {
        try {
            userSessionDao.clearSession()
        } catch (e: Exception) {
            Log.e("SessionManager", "Error clearing session: ${e.message}")
            _sessionMessage.value = context.getString(R.string.session_clear_error)
        }
    }

    /**
     * Clears the session message, setting it to null.
     * This can be used to reset the session message after it has been displayed.
     */
    fun clearSessionMessage() {
        _sessionMessage.value = null
    }

    /**
     * Retrieves the current user session as a Flow.
     * This Flow emits the User object if a session exists,
     * or null if no session is found.
     */
    val user: Flow<User?> = flow {
        emit(userSessionDao.getSession()?.toUser())
    }

    /**
     * Retrieves the access token of the current user session as a Flow.
     * This Flow emits the token string if a session exists,
     * or null if no session is found.
     */
    val token: Flow<String?> = flow {
        emit(userSessionDao.getSession()?.token)
    }

    /**
     * Retrieves the expiration time of the current user session as a Flow.
     * This Flow emits the expiration time in milliseconds if a session exists,
     * or null if no session is found.
     */
    val expiresAt: Flow<Long?> = flow {
        emit(userSessionDao.getSession()?.expiresIn)
    }

    /**
     * A Flow that emits a Boolean indicating whether a session exists.
     * It emits true if the access token is not null or empty, and false otherwise.
     */
    val hasSession: Flow<Boolean> = token.map { !it.isNullOrEmpty() }

    /**
     * A Flow that combines the access token and expiration time to determine
     * if the session is valid. It emits true if the access token is not null or empty
     * and the current time is less than the expiration time, otherwise it emits false.
     * If the session is invalid, it sets a session message indicating that the session has expired.
     */
    val isSessionValid: Flow<Boolean> = combine(token, expiresAt) { accessToken, expiresAt ->
        val isValid =
            !accessToken.isNullOrEmpty() && (expiresAt != null && System.currentTimeMillis() < expiresAt)
        if (!isValid) {
            _sessionMessage.value = context.getString(R.string.session_expired)
        }
        isValid
    }

}