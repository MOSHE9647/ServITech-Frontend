package com.moviles.servitech.core.session

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.moviles.servitech.R
import com.moviles.servitech.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _sessionMessage = MutableStateFlow<String?>(null)
    val sessionMessage: StateFlow<String?> = _sessionMessage.asStateFlow()

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val EXPIRES_AT = longPreferencesKey("expires_at")
        private val USER_ID = intPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_LAST_NAME = stringPreferencesKey("user_last_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_PHONE = stringPreferencesKey("user_phone")
        private val USER_ROLE = stringPreferencesKey("user_role")
        private const val DEFAULT_ROLE = "guest"
    }

    suspend fun saveSession(token: String, expiresIn: Long, user: User) {
        val expiresAt = System.currentTimeMillis() + (expiresIn * 1000)
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = token
            prefs[EXPIRES_AT] = expiresAt
            prefs[USER_ID] = user.id ?: 0
            prefs[USER_ROLE] = user.role
            prefs[USER_NAME] = user.name
            prefs[USER_LAST_NAME] = user.lastName
            prefs[USER_EMAIL] = user.email
            prefs[USER_PHONE] = user.phone ?: ""
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    fun clearSessionMessage() {
        _sessionMessage.value = null
    }

    val token: Flow<String?> = context.dataStore.data.map { it[ACCESS_TOKEN] }
    val expiresAt: Flow<Long?> = context.dataStore.data.map { it[EXPIRES_AT] }

    val user: Flow<User?> = context.dataStore.data.map { prefs ->
        prefs.toUser()
    }

    val hasSession: Flow<Boolean> = token.map { it != null && it.isNotEmpty() }

    val isSessionValid: Flow<Boolean> = combine(token, expiresAt) { accessToken, expires ->
        val isValid = !accessToken.isNullOrEmpty() && (expires != null && System.currentTimeMillis() < expires)
        if (!isValid) {
            _sessionMessage.value = context.getString(R.string.session_expired)
        }
        isValid
    }

    private fun Preferences.toUser(): User? {
        val id = this[USER_ID]
        val role = this[USER_ROLE]
        val name = this[USER_NAME]
        val lastName = this[USER_LAST_NAME]
        val email = this[USER_EMAIL]
        val phone = this[USER_PHONE]

        return if (id != null && name != null && email != null) {
            User(
                id = id,
                role = role ?: DEFAULT_ROLE,
                name = name,
                lastName = lastName.orEmpty(),
                email = email,
                phone = phone
            )
        } else null
    }
}