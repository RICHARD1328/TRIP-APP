package com.tripdirect.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import com.tripdirect.data.model.Hotel
import kotlinx.coroutines.delay

interface HotelRepository {
    suspend fun searchHotels(
        destination: String,
        checkIn: String,
        checkOut: String,
        adults: Int,
        children: Int,
        rooms: Int
    ): List<Hotel>

    suspend fun getHotelById(id: String): Hotel?
}

class MockHotelRepository : HotelRepository {

    private val mockHotels = listOf(
        Hotel(
            id = "1", name = "Mountain View Hotel", destination = "Ooty", location = "Ooty", 
            description = "A comfortable stay located near major attractions in Ooty, suitable for travelers looking for convenient access and affordable accommodation.", 
            rating = 4.3, reviewCount = 120, pricePerNight = 1500, 
            imageRes = null, vectorIcon = Icons.Default.Hotel, distanceFromCenter = 1.2, 
            facilities = listOf("Free Wi-Fi", "Breakfast", "Parking", "Room Service", "Restaurant"), roomType = "Deluxe Double Room", 
            cancellationPolicy = "Free cancellation up to 48 hours before check-in.", breakfastIncluded = true, isFavorite = false
        ),
        Hotel(
            id = "2", name = "Ooty Lake Resort", destination = "Ooty", location = "Ooty", 
            description = "A luxurious resort with a beautiful view of Ooty Lake. Perfect for couples and families.", 
            rating = 4.6, reviewCount = 200, pricePerNight = 2500, 
            imageRes = null, vectorIcon = Icons.Default.Hotel, distanceFromCenter = 0.8, 
            facilities = listOf("Lake View", "Breakfast", "Wi-Fi", "Parking"), roomType = "Lake View Suite", 
            cancellationPolicy = "Free cancellation up to 24 hours before check-in.", breakfastIncluded = true, isFavorite = true
        ),
        Hotel(
            id = "3", name = "Green Valley Stay", destination = "Ooty", location = "Ooty", 
            description = "Budget-friendly stay surrounded by nature.", 
            rating = 4.1, reviewCount = 80, pricePerNight = 1000, 
            imageRes = null, vectorIcon = Icons.Default.Hotel, distanceFromCenter = 2.4, 
            facilities = listOf("Wi-Fi", "Parking"), roomType = "Standard Room", 
            cancellationPolicy = "Non-refundable.", breakfastIncluded = false, isFavorite = false
        ),
        Hotel(
            id = "4", name = "Tea Garden Residency", destination = "Munnar", location = "Munnar", 
            description = "Stay amidst the lush green tea gardens of Munnar.", 
            rating = 4.4, reviewCount = 150, pricePerNight = 1800, 
            imageRes = null, vectorIcon = Icons.Default.Hotel, distanceFromCenter = 1.5, 
            facilities = listOf("Breakfast", "Wi-Fi", "Garden"), roomType = "Double Room", 
            cancellationPolicy = "Free cancellation up to 48 hours before check-in.", breakfastIncluded = true, isFavorite = false
        ),
        Hotel(
            id = "5", name = "Munnar Hills Hotel", destination = "Munnar", location = "Munnar", 
            description = "Premium hotel offering the best mountain views.", 
            rating = 4.6, reviewCount = 110, pricePerNight = 2500, 
            imageRes = null, vectorIcon = Icons.Default.Hotel, distanceFromCenter = 0.9, 
            facilities = listOf("Breakfast", "Parking", "Restaurant", "Wi-Fi"), roomType = "Premium Suite", 
            cancellationPolicy = "Free cancellation up to 7 days before check-in.", breakfastIncluded = true, isFavorite = false
        )
    )

    override suspend fun searchHotels(
        destination: String,
        checkIn: String,
        checkOut: String,
        adults: Int,
        children: Int,
        rooms: Int
    ): List<Hotel> {
        delay(600) // Mock network delay
        val lowerDest = destination.lowercase().trim()
        if (lowerDest.isEmpty()) return emptyList()

        return mockHotels.filter {
            it.destination.lowercase().contains(lowerDest) ||
            it.location.lowercase().contains(lowerDest) ||
            it.name.lowercase().contains(lowerDest)
        }
    }

    override suspend fun getHotelById(id: String): Hotel? {
        delay(300)
        return mockHotels.find { it.id == id }
    }
}
