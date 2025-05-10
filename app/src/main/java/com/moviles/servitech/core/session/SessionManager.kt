package com.moviles.servitech.core.session

import android.content.Context
import android.util.Log
import com.moviles.servitech.R
import com.moviles.servitech.database.dao.UserSessionDao
import com.moviles.servitech.database.entities.UserSessionEntity
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

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context, private val userSessionDao: UserSessionDao
) {

    private val _sessionMessage = MutableStateFlow<String?>(null)
    val sessionMessage: StateFlow<String?> = _sessionMessage.asStateFlow()

    suspend fun saveSession(token: String, expiresIn: Long, user: User) {
        val expiresAt = System.currentTimeMillis() + (expiresIn * 1000)
        val userSession = UserSessionEntity(
            id = 1, // Only one session is allowed
            user = user.toEntity(), token = token, expiresIn = expiresAt
        )
        userSessionDao.saveSession(userSession)
    }

    suspend fun clearSession() {
        try {
            userSessionDao.clearSession()
        } catch (e: Exception) {
            Log.e("SessionManager", "Error clearing session: ${e.message}")
            _sessionMessage.value = context.getString(R.string.session_clear_error)
        }
    }

    fun clearSessionMessage() {
        _sessionMessage.value = null
    }

    val user: Flow<User?> = flow {
        emit(userSessionDao.getSession()?.toUser())
    }

    val token: Flow<String?> = flow {
        emit(userSessionDao.getSession()?.token)
    }

    val expiresAt: Flow<Long?> = flow {
        emit(userSessionDao.getSession()?.expiresIn)
    }

    val hasSession: Flow<Boolean> = token.map { !it.isNullOrEmpty() }

    val isSessionValid: Flow<Boolean> = combine(token, expiresAt) { accessToken, expiresAt ->
        val isValid =
            !accessToken.isNullOrEmpty() && (expiresAt != null && System.currentTimeMillis() < expiresAt)
        if (!isValid) {
            _sessionMessage.value = context.getString(R.string.session_expired)
        }
        isValid
    }

}