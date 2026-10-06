package com.tripdirect.data.repository

import com.tripdirect.data.network.NominatimApiService
import com.tripdirect.data.network.NominatimPlaceDto
import com.tripdirect.data.network.NominatimSearchResultItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

sealed class NominatimResult {
    data class Success(val items: List<NominatimSearchResultItem>) : NominatimResult()
    data class Error(val message: String, val isOffline: Boolean = false) : NominatimResult()
}

class NominatimRepository {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "TripDirect-AndroidApp/1.0 (contact: tripdirect@app.local)")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    private val apiService: NominatimApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NominatimApiService::class.java)
    }

    private var lastQuery: String? = null
    private var lastResult: List<NominatimSearchResultItem> = emptyList()

    suspend fun searchPlace(query: String): NominatimResult = withContext(Dispatchers.IO) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) {
            return@withContext NominatimResult.Success(emptyList())
        }

        // Cache check to prevent duplicate API requests for identical submitted queries
        if (trimmedQuery.equals(lastQuery, ignoreCase = true) && lastResult.isNotEmpty()) {
            return@withContext NominatimResult.Success(lastResult)
        }

        try {
            val response = apiService.searchLocations(query = trimmedQuery)
            if (response.isSuccessful) {
                val body = response.body() ?: emptyList()
                val items = body.mapNotNull { it.toDomainItem() }
                lastQuery = trimmedQuery
                lastResult = items
                NominatimResult.Success(items)
            } else {
                NominatimResult.Error("OpenStreetMap service returned error ${response.code()}. Please try again.")
            }
        } catch (e: java.net.UnknownHostException) {
            NominatimResult.Error("No internet connection. Check your connection and try again.", isOffline = true)
        } catch (e: java.net.ConnectException) {
            NominatimResult.Error("No internet connection. Check your connection and try again.", isOffline = true)
        } catch (e: java.net.SocketTimeoutException) {
            NominatimResult.Error("Request timed out. Check connection and try again.", isOffline = false)
        } catch (e: java.io.IOException) {
            NominatimResult.Error("No internet connection. Check your connection and try again.", isOffline = true)
        } catch (e: Exception) {
            NominatimResult.Error("Unable to load destinations right now. Please try again.", isOffline = false)
        }
    }

    private fun NominatimPlaceDto.toDomainItem(): NominatimSearchResultItem? {
        val latVal = lat?.toDoubleOrNull() ?: 0.0
        val lonVal = lon?.toDoubleOrNull() ?: 0.0
        val fullDispName = displayName ?: name ?: return null

        // Extract primary place name
        val primaryName = name?.ifBlank { null }
            ?: fullDispName.split(",").firstOrNull()?.trim()
            ?: "Unknown Location"

        val categoryStr = when {
            category != null && type != null -> "${category.capitalizeWords()} / ${type.capitalizeWords()}"
            category != null -> category.capitalizeWords()
            type != null -> type.capitalizeWords()
            else -> "Tourist Spot"
        }

        return NominatimSearchResultItem(
            id = placeId?.toString() ?: "$latVal,$lonVal",
            name = primaryName,
            fullAddress = fullDispName,
            latitude = latVal,
            longitude = lonVal,
            category = categoryStr,
            type = type ?: "place"
        )
    }

    private fun String.capitalizeWords(): String {
        return this.replace("_", " ")
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }
}
