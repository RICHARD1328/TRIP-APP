package com.tripdirect.data.repository

import com.tripdirect.data.UserPreferencesManager
import com.tripdirect.data.model.User
import com.tripdirect.data.security.PasswordHasher
import java.security.MessageDigest
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

        if (enteredEmail != savedEmail) {
            return Result.failure(Exception("Invalid email or password"))
        }

        // 1. Salted Hash Verification
        if (!savedUser.passwordHash.isNullOrBlank() && !savedUser.passwordSalt.isNullOrBlank()) {
            val isPasswordValid = PasswordHasher.verifyPassword(
                enteredPassword = enteredPassword,
                storedHash = savedUser.passwordHash,
                storedSalt = savedUser.passwordSalt
            )
            if (isPasswordValid) {
                preferencesManager.setLoggedIn(true)
                return Result.success(User(id = "101", name = savedUser.fullName, email = savedUser.email))
            }
        } 
        // 2. Legacy Plain-Text Migration Path
        else if (!savedUser.legacyPlainPassword.isNullOrBlank()) {
            val isLegacyMatch = MessageDigest.isEqual(
                enteredPassword.toByteArray(Charsets.UTF_8),
                savedUser.legacyPlainPassword.toByteArray(Charsets.UTF_8)
            )
            if (isLegacyMatch) {
                // Migrate user to salted PBKDF2 hash and purge plain text password
                val salt = PasswordHasher.generateSalt()
                val hash = PasswordHasher.hashPassword(enteredPassword, salt)
                preferencesManager.migrateLegacyUser(hash = hash, salt = salt)

                preferencesManager.setLoggedIn(true)
                return Result.success(User(id = "101", name = savedUser.fullName, email = savedUser.email))
            }
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
