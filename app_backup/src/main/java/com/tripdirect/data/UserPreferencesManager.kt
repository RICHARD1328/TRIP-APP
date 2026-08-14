package com.tripdirect.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class RegisteredUser(
    val fullName: String,
    val email: String,
    val password: String
)

class UserPreferencesManager(private val context: Context) {

    companion object {
        private val KEY_REGISTERED_NAME = stringPreferencesKey("registered_name")
        private val KEY_REGISTERED_EMAIL = stringPreferencesKey("registered_email")
        private val KEY_REGISTERED_PASSWORD = stringPreferencesKey("registered_password")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

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
            val password = preferences[KEY_REGISTERED_PASSWORD]

            if (!name.isNullOrBlank() && !email.isNullOrBlank() && !password.isNullOrBlank()) {
                RegisteredUser(name, email, password)
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
            context.dataStore.edit { preferences ->
                preferences[KEY_REGISTERED_NAME] = name.trim()
                preferences[KEY_REGISTERED_EMAIL] = email.trim().lowercase()
                preferences[KEY_REGISTERED_PASSWORD] = password
                preferences[KEY_IS_LOGGED_IN] = false
            }
        } catch (e: Exception) {
            // Logged or handled gracefully
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[KEY_IS_LOGGED_IN] = isLoggedIn
            }
        } catch (e: Exception) {
            // Logged or handled gracefully
        }
    }

    suspend fun clearAccountData() {
        try {
            context.dataStore.edit { preferences ->
                preferences.remove(KEY_REGISTERED_NAME)
                preferences.remove(KEY_REGISTERED_EMAIL)
                preferences.remove(KEY_REGISTERED_PASSWORD)
                preferences[KEY_IS_LOGGED_IN] = false
            }
        } catch (e: Exception) {
            // Logged or handled gracefully
        }
    }
}
