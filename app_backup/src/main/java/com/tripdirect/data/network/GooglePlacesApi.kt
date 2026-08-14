package com.tripdirect.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class TextSearchRequest(
    @SerializedName("textQuery") val textQuery: String
)

data class TextSearchResponse(
    @SerializedName("places") val places: List<PlaceDto>?
)

data class PlaceDto(
    @SerializedName("id") val id: String?,
    @SerializedName("displayName") val displayName: DisplayNameDto?,
    @SerializedName("formattedAddress") val formattedAddress: String?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("userRatingCount") val userRatingCount: Int?,
    @SerializedName("photos") val photos: List<PhotoDto>?,
    @SerializedName("googleMapsUri") val googleMapsUri: String?,
    @SerializedName("priceLevel") val priceLevel: String?
)

data class DisplayNameDto(
    @SerializedName("text") val text: String?,
    @SerializedName("languageCode") val languageCode: String? = "en"
)

data class PhotoDto(
    @SerializedName("name") val name: String?, // e.g. "places/ChIJ.../photos/AUjq..."
    @SerializedName("widthPx") val widthPx: Int?,
    @SerializedName("heightPx") val heightPx: Int?,
    @SerializedName("authorAttributions") val authorAttributions: List<AuthorAttributionDto>?
)

data class AuthorAttributionDto(
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("uri") val uri: String?,
    @SerializedName("photoUri") val photoUri: String?
)

interface GooglePlacesApi {
    @POST("v1/places:searchText")
    suspend fun searchPlaces(
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String = "places.id,places.displayName,places.formattedAddress,places.rating,places.userRatingCount,places.photos,places.googleMapsUri",
        @Body request: TextSearchRequest
    ): Response<TextSearchResponse>
}

object GooglePlacesPhotoHelper {
    /**
     * Builds Google Places API (New) Place Photo URI.
     * Format: https://places.googleapis.com/v1/{photo_name}/media?key={apiKey}&maxWidthPx={maxWidthPx}
     */
    fun buildPhotoUrl(photoName: String, apiKey: String, maxWidthPx: Int = 800): String {
        val cleanName = photoName.trimStart('/')
        return "https://places.googleapis.com/v1/$cleanName/media?key=$apiKey&maxWidthPx=$maxWidthPx"
    }

    /**
     * Formats Google photo author attribution text.
     */
    fun formatAttribution(authorAttributions: List<AuthorAttributionDto>?): String? {
        val author = authorAttributions?.firstOrNull()?.displayName ?: return null
        return if (author.isNotBlank()) "Photo: $author" else null
    }
}

