package com.tripdirect.data.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun generateSalt_returnsValid32CharHexSalt() {
        val salt = PasswordHasher.generateSalt()
        assertNotNull(salt)
        assertEquals(32, salt.length) // 16 bytes = 32 hex characters
        assertTrue(salt.matches(Regex("^[0-9a-f]{32}$")))
    }

    @Test
    fun generateSalt_generatesUniqueSalts() {
        val salt1 = PasswordHasher.generateSalt()
        val salt2 = PasswordHasher.generateSalt()
        assertNotEquals(salt1, salt2)
    }

    @Test
    fun hashPassword_isDeterministicForSamePasswordAndSalt() {
        val salt = PasswordHasher.generateSalt()
        val password = "SuperSecretPassword123!"

        val hash1 = PasswordHasher.hashPassword(password, salt)
        val hash2 = PasswordHasher.hashPassword(password, salt)

        assertEquals(hash1, hash2)
        assertEquals(64, hash1.length) // 256 bits = 64 hex characters
    }

    @Test
    fun hashPassword_producesDifferentHashesForDifferentSalts() {
        val password = "MySecurePassword2026!"
        val salt1 = PasswordHasher.generateSalt()
        val salt2 = PasswordHasher.generateSalt()

        val hash1 = PasswordHasher.hashPassword(password, salt1)
        val hash2 = PasswordHasher.hashPassword(password, salt2)

        assertNotEquals(hash1, hash2)
    }

    @Test
    fun hashPassword_producesDifferentHashesForDifferentPasswords() {
        val salt = PasswordHasher.generateSalt()
        val hash1 = PasswordHasher.hashPassword("PasswordOne123!", salt)
        val hash2 = PasswordHasher.hashPassword("PasswordTwo123!", salt)

        assertNotEquals(hash1, hash2)
    }

    @Test
    fun verifyPassword_returnsTrueForCorrectPassword() {
        val password = "CorrectHorseBatteryStaple!"
        val salt = PasswordHasher.generateSalt()
        val storedHash = PasswordHasher.hashPassword(password, salt)

        val isValid = PasswordHasher.verifyPassword(password, storedHash, salt)
        assertTrue(isValid)
    }

    @Test
    fun verifyPassword_returnsFalseForWrongPassword() {
        val correctPassword = "CorrectPassword123!"
        val wrongPassword = "WrongPassword123!"
        val salt = PasswordHasher.generateSalt()
        val storedHash = PasswordHasher.hashPassword(correctPassword, salt)

        val isValid = PasswordHasher.verifyPassword(wrongPassword, storedHash, salt)
        assertFalse(isValid)
    }

    @Test
    fun verifyPassword_returnsFalseForBlankInputs() {
        assertFalse(PasswordHasher.verifyPassword("test", "", "salt"))
        assertFalse(PasswordHasher.verifyPassword("test", "hash", ""))
        assertFalse(PasswordHasher.verifyPassword("test", "   ", "   "))
    }
}
