package com.tripdirect.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tripdirect.data.security.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class RegisteredUser(
    val fullName: String,
    val email: String,
    val passwordHash: String? = null,
    val passwordSalt: String? = null,
    val legacyPlainPassword: String? = null
)

class UserPreferencesManager(private val context: Context) {

    companion object {
        private val KEY_REGISTERED_NAME = stringPreferencesKey("registered_name")
        private val KEY_REGISTERED_EMAIL = stringPreferencesKey("registered_email")
        private val KEY_REGISTERED_PASSWORD_HASH = stringPreferencesKey("registered_password_hash")
        private val KEY_REGISTERED_PASSWORD_SALT = stringPreferencesKey("registered_password_salt")
        private val KEY_REGISTERED_PASSWORD = stringPreferencesKey("registered_password") // Legacy plain text key
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_BACKEND_URL = stringPreferencesKey("backend_base_url")

        const val DEFAULT_BACKEND_URL = "http://10.0.2.2:8000/"

        @Volatile
        private var INSTANCE: UserPreferencesManager? = null

        fun getInstance(context: Context): UserPreferencesManager {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferencesManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    val registeredUserFlow: Flow<RegisteredUser?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val name = preferences[KEY_REGISTERED_NAME]
            val email = preferences[KEY_REGISTERED_EMAIL]
            val hash = preferences[KEY_REGISTERED_PASSWORD_HASH]
            val salt = preferences[KEY_REGISTERED_PASSWORD_SALT]
            val legacyPass = preferences[KEY_REGISTERED_PASSWORD]

            if (!name.isNullOrBlank() && !email.isNullOrBlank() && (!hash.isNullOrBlank() || !legacyPass.isNullOrBlank())) {
                RegisteredUser(
                    fullName = name,
                    email = email,
                    passwordHash = hash,
                    passwordSalt = salt,
                    legacyPlainPassword = if (hash.isNullOrBlank()) legacyPass else null
                )
            } else {
                null
            }
        }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_IS_LOGGED_IN] ?: false
        }

    suspend fun getRegisteredUser(): RegisteredUser? {
        return try {
            registeredUserFlow.first()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return try {
            isLoggedInFlow.first()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun saveUser(name: String, email: String, password: String) {
        try {
            val salt = PasswordHasher.generateSalt()
            val hash = PasswordHasher.hashPassword(password, salt)

            context.dataStore.edit { preferences ->
                preferences[KEY_REGISTERED_NAME] = name.trim()
                preferences[KEY_REGISTERED_EMAIL] = email.trim().lowercase()
                preferences[KEY_REGISTERED_PASSWORD_HASH] = hash
                preferences[KEY_REGISTERED_PASSWORD_SALT] = salt
                preferences.remove(KEY_REGISTERED_PASSWORD) // Purge plain text password
                preferences[KEY_IS_LOGGED_IN] = false
            }
        } catch (e: Exception) {
            // Handled gracefully
        }
    }

    suspend fun migrateLegacyUser(hash: String, salt: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[KEY_REGISTERED_PASSWORD_HASH] = hash
                preferences[KEY_REGISTERED_PASSWORD_SALT] = salt
                preferences.remove(KEY_REGISTERED_PASSWORD) // Purge plain text password
            }
        } catch (e: Exception) {
            // Handled gracefully
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[KEY_IS_LOGGED_IN] = isLoggedIn
            }
        } catch (e: Exception) {
            // Handled gracefully
        }
    }

    suspend fun clearAccountData() {
        try {
            context.dataStore.edit { preferences ->
                preferences.remove(KEY_REGISTERED_NAME)
                preferences.remove(KEY_REGISTERED_EMAIL)
                preferences.remove(KEY_REGISTERED_PASSWORD_HASH)
                preferences.remove(KEY_REGISTERED_PASSWORD_SALT)
                preferences.remove(KEY_REGISTERED_PASSWORD)
                preferences[KEY_IS_LOGGED_IN] = false
            }
        } catch (e: Exception) {
            // Handled gracefully
        }
    }

    val backendUrlFlow: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_BACKEND_URL] ?: DEFAULT_BACKEND_URL
        }

    suspend fun getBackendUrl(): String {
        return try {
            backendUrlFlow.first()
        } catch (e: Exception) {
            DEFAULT_BACKEND_URL
        }
    }

    suspend fun saveBackendUrl(url: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[KEY_BACKEND_URL] = url
            }
        } catch (e: Exception) {
            // Handled gracefully
        }
    }

    suspend fun resetBackendUrl() {
        try {
            context.dataStore.edit { preferences ->
                preferences.remove(KEY_BACKEND_URL)
            }
        } catch (e: Exception) {
            // Handled gracefully
        }
    }
}
