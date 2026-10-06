package com.tripdirect.network

import com.tripdirect.data.UserPreferencesManager
import com.tripdirect.data.network.ApiClient
import com.tripdirect.data.network.BackendUrlValidator
import com.tripdirect.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BackendUrlConfigurationUnitTest {

    @Test
    fun testServerSettingsRoute_isDefinedCorrectly() {
        assertEquals("serverSettings", Screen.ServerSettings.route)
    }

    @Test
    fun testBackendUrlValidator_acceptsValidHttpAndHttpsUrls() {
        val emulator = BackendUrlValidator.validateAndNormalize("http://10.0.2.2:8000/")
        assertTrue(emulator is BackendUrlValidator.ValidationResult.Valid)
        assertEquals("http://10.0.2.2:8000/", (emulator as BackendUrlValidator.ValidationResult.Valid).normalizedUrl)

        val httpsDomain = BackendUrlValidator.validateAndNormalize("https://api.tripdirect.com/v1/")
        assertTrue(httpsDomain is BackendUrlValidator.ValidationResult.Valid)
        assertEquals("https://api.tripdirect.com/v1/", (httpsDomain as BackendUrlValidator.ValidationResult.Valid).normalizedUrl)
    }

    @Test
    fun testBackendUrlValidator_normalizesMissingTrailingSlash() {
        val missingSlash = BackendUrlValidator.validateAndNormalize("http://192.168.1.105:8000")
        assertTrue(missingSlash is BackendUrlValidator.ValidationResult.Valid)
        assertEquals("http://192.168.1.105:8000/", (missingSlash as BackendUrlValidator.ValidationResult.Valid).normalizedUrl)

        val httpsMissingSlash = BackendUrlValidator.validateAndNormalize("https://staging.tripdirect.io")
        assertTrue(httpsMissingSlash is BackendUrlValidator.ValidationResult.Valid)
        assertEquals("https://staging.tripdirect.io/", (httpsMissingSlash as BackendUrlValidator.ValidationResult.Valid).normalizedUrl)
    }

    @Test
    fun testBackendUrlValidator_rejectsEmptyAndMalformedUrls() {
        val empty = BackendUrlValidator.validateAndNormalize("")
        assertTrue(empty is BackendUrlValidator.ValidationResult.Invalid)

        val blank = BackendUrlValidator.validateAndNormalize("   ")
        assertTrue(blank is BackendUrlValidator.ValidationResult.Invalid)

        val noScheme = BackendUrlValidator.validateAndNormalize("192.168.1.100:8000")
        assertTrue(noScheme is BackendUrlValidator.ValidationResult.Invalid)

        val unsupportedScheme = BackendUrlValidator.validateAndNormalize("ftp://myserver.org:8000/")
        assertTrue(unsupportedScheme is BackendUrlValidator.ValidationResult.Invalid)

        val noHost = BackendUrlValidator.validateAndNormalize("http://")
        assertTrue(noHost is BackendUrlValidator.ValidationResult.Invalid)

        val malformed = BackendUrlValidator.validateAndNormalize("http://   :8000/")
        assertTrue(malformed is BackendUrlValidator.ValidationResult.Invalid)
    }

    @Test
    fun testApiClient_managesBaseUrlDynamically() {
        // Reset to default
        ApiClient.setBaseUrl(UserPreferencesManager.DEFAULT_BACKEND_URL)
        assertEquals("http://10.0.2.2:8000/", ApiClient.getBaseUrl())

        // Change to local LAN address (simulating physical device on Wi-Fi)
        ApiClient.setBaseUrl("http://192.168.1.42:8000")
        assertEquals("http://192.168.1.42:8000/", ApiClient.getBaseUrl())

        // Verify API client can be retrieved with the new base URL
        val api = ApiClient.getAiPlannerApi()
        assertNotNull(api)

        // Change to remote staging domain
        ApiClient.setBaseUrl("https://staging-api.tripdirect.net")
        assertEquals("https://staging-api.tripdirect.net/", ApiClient.getBaseUrl())

        // Reset back to default emulator URL
        ApiClient.setBaseUrl(UserPreferencesManager.DEFAULT_BACKEND_URL)
        assertEquals("http://10.0.2.2:8000/", ApiClient.getBaseUrl())
    }
}
