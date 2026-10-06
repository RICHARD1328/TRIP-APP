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
            try {
                val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                val hash = skf.generateSecret(spec).encoded
                hash.toHexString()
            } finally {
                spec.clearPassword()
            }
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
        val computedBytes = computedHash.toByteArray(Charsets.UTF_8)
        val storedBytes = storedHash.toByteArray(Charsets.UTF_8)
        return MessageDigest.isEqual(computedBytes, storedBytes)
    }

    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }

    private fun String.hexToByteArray(): ByteArray {
        val len = length
        if (len % 2 != 0) return ByteArray(0)
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            val high = Character.digit(this[i], 16)
            val low = Character.digit(this[i + 1], 16)
            if (high == -1 || low == -1) return ByteArray(0)
            data[i / 2] = ((high shl 4) + low).toByte()
            i += 2
        }
        return data
    }
}
