package com.tripdirect.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NominatimApiService {

    @GET("search")
    suspend fun searchLocations(
        @Query("q") query: String,
        @Query("format") format: String = "jsonv2",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("limit") limit: Int = 10,
        @Header("User-Agent") userAgent: String = "TripDirect-AndroidApp/1.0 (contact: tripdirect@app.local)"
    ): Response<List<NominatimPlaceDto>>
}
