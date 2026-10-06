package com.tripdirect.data.network

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

object BackendUrlValidator {

    sealed class ValidationResult {
        data class Valid(val normalizedUrl: String) : ValidationResult()
        data class Invalid(val errorMessage: String) : ValidationResult()
    }

    /**
     * Validates and normalizes candidate backend base URLs according to RFC rules and Retrofit requirements.
     * Enforces:
     * 1. Non-empty trimmed string.
     * 2. Scheme must be http or https.
     * 3. Valid host identifier (IP or domain name).
     * 4. Automatic trailing slash normalization (required by Retrofit base URLs).
     */
    fun validateAndNormalize(rawUrl: String): ValidationResult {
        val trimmed = rawUrl.trim()
        if (trimmed.isBlank()) {
            return ValidationResult.Invalid("Backend URL cannot be empty.")
        }

        val lower = trimmed.lowercase()
        if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
            return ValidationResult.Invalid("URL must begin with 'http://' or 'https://'")
        }

        // Ensure trailing slash for Retrofit base URL compatibility
        val withSlash = if (!trimmed.endsWith("/")) "$trimmed/" else trimmed

        val httpUrl = withSlash.toHttpUrlOrNull()
            ?: return ValidationResult.Invalid("Invalid URL syntax or characters.")

        if (httpUrl.host.isBlank()) {
            return ValidationResult.Invalid("URL must contain a valid host address.")
        }

        return ValidationResult.Valid(httpUrl.toString())
    }
}
