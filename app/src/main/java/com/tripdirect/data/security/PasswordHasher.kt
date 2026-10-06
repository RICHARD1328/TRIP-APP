package com.tripdirect.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
    private const val SALT_LENGTH = 16 // 128 bits
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256

    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return salt.toHexString()
    }

    fun hashPassword(password: String, saltHex: String): String {
        return try {
            val salt = saltHex.hexToByteArray()
            val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
            val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val hash = skf.generateSecret(spec).encoded
            hash.toHexString()
        } catch (e: Exception) {
            // Fallback: Salted SHA-256
            val md = MessageDigest.getInstance("SHA-256")
            md.update(saltHex.toByteArray(Charsets.UTF_8))
            val hash = md.digest(password.toByteArray(Charsets.UTF_8))
            hash.toHexString()
        }
    }

    fun verifyPassword(enteredPassword: String, storedHash: String, storedSalt: String): Boolean {
        if (storedHash.isBlank() || storedSalt.isBlank()) return false
        val computedHash = hashPassword(enteredPassword, storedSalt)
        return computedHash.equals(storedHash, ignoreCase = true)
    }

    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }

    private fun String.hexToByteArray(): ByteArray {
        val len = length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }
}
