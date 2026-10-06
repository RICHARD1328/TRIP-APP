package com.tripdirect.data.repository

import com.tripdirect.data.UserPreferencesManager
import com.tripdirect.data.model.User
import kotlinx.coroutines.delay

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun isLoggedIn(): Boolean
    suspend fun logout()
    suspend fun clearAccountData()
}

class DataStoreAuthRepository(
    private val preferencesManager: UserPreferencesManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        delay(400) // UI responsiveness feel

        val savedUser = preferencesManager.getRegisteredUser()

        if (savedUser == null) {
            return Result.failure(Exception("No account found. Please create an account first."))
        }

        val enteredEmail = email.trim().lowercase()
        val enteredPassword = password

        val savedEmail = savedUser.email.trim().lowercase()
        val savedPassword = savedUser.password

        if (enteredEmail == savedEmail && enteredPassword == savedPassword) {
            preferencesManager.setLoggedIn(true)
            return Result.success(User(id = "101", name = savedUser.fullName, email = savedUser.email))
        }

        return Result.failure(Exception("Invalid email or password"))
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        delay(400) // UI responsiveness feel
        val normalizedEmail = email.trim().lowercase()
        val trimmedName = name.trim()

        preferencesManager.saveUser(
            name = trimmedName,
            email = normalizedEmail,
            password = password
        )

        return Result.success(User(id = System.currentTimeMillis().toString(), name = trimmedName, email = normalizedEmail))
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        delay(300)
        return Result.success(Unit)
    }

    override suspend fun isLoggedIn(): Boolean {
        return preferencesManager.isLoggedIn()
    }

    override suspend fun logout() {
        preferencesManager.setLoggedIn(false)
    }

    override suspend fun clearAccountData() {
        preferencesManager.clearAccountData()
    }
}
