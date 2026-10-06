package com.tripdirect.data.network

import com.google.gson.annotations.SerializedName

data class NominatimPlaceDto(
    @SerializedName("place_id")
    val placeId: Long? = null,
    
    @SerializedName("licence")
    val licence: String? = null,
    
    @SerializedName("osm_type")
    val osmType: String? = null,
    
    @SerializedName("osm_id")
    val osmId: Long? = null,
    
    @SerializedName("lat")
    val lat: String? = null,
    
    @SerializedName("lon")
    val lon: String? = null,
    
    @SerializedName("category")
    val category: String? = null,
    
    @SerializedName("type")
    val type: String? = null,
    
    @SerializedName("place_rank")
    val placeRank: Int? = null,
    
    @SerializedName("importance")
    val importance: Double? = null,
    
    @SerializedName("addresstype")
    val addressType: String? = null,
    
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("display_name")
    val displayName: String? = null,
    
    @SerializedName("address")
    val address: Map<String, String>? = null
)

data class NominatimSearchResultItem(
    val id: String,
    val name: String,
    val fullAddress: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val type: String
)
