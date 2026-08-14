package com.tripdirect.data.repository

import com.tripdirect.data.model.PricingType
import com.tripdirect.data.model.TransportOption
import com.tripdirect.data.model.TransportType
import kotlinx.coroutines.delay

interface TransportRepository {
    suspend fun searchTransport(
        from: String,
        to: String,
        date: String,
        travelers: Int
    ): List<TransportOption>

    suspend fun getTransportById(id: String): TransportOption?
}

class MockTransportRepository : TransportRepository {

    private val mockTransports = listOf(
        TransportOption(
            id = "1", type = TransportType.BUS, providerName = "TN Hill Travels", 
            from = "Chennai", to = "Ooty", departureTime = "07:00 AM", arrivalTime = "01:30 PM", 
            durationMinutes = 390, price = 850, pricingType = PricingType.PER_PERSON, rating = 4.2, 
            availableSeats = 24, facilities = listOf("AC", "2+2 Seating", "Comfortable Seating"), 
            routeDescription = "Chennai -> Salem -> Coimbatore -> Ooty", isRecommended = true
        ),
        TransportOption(
            id = "2", type = TransportType.TRAIN, providerName = "Nilgiri Express Connection", 
            from = "Chennai", to = "Ooty", departureTime = "06:00 AM", arrivalTime = "02:00 PM", 
            durationMinutes = 480, price = 600, pricingType = PricingType.PER_PERSON, rating = 4.4, 
            availableSeats = 120, facilities = listOf("Seating", "Luggage Space"), 
            routeDescription = "Chennai -> Coimbatore (Train) -> Ooty (Bus)", isRecommended = false
        ),
        TransportOption(
            id = "3", type = TransportType.TRAIN, providerName = "Chennai Express", 
            from = "Chennai", to = "Coimbatore", departureTime = "06:00 AM", arrivalTime = "01:00 PM", 
            durationMinutes = 420, price = 750, pricingType = PricingType.PER_PERSON, rating = 4.5, 
            availableSeats = 80, facilities = listOf("AC", "Charging Point"), 
            routeDescription = "Chennai -> Salem -> Coimbatore", isRecommended = false
        ),
        TransportOption(
            id = "4", type = TransportType.TAXI, providerName = "Private Cab", 
            from = "Chennai", to = "Ooty", departureTime = "Flexible departure", arrivalTime = "Depends on departure", 
            durationMinutes = 360, price = 5500, pricingType = PricingType.PER_TRIP, rating = 4.6, 
            availableSeats = 4, facilities = listOf("AC", "Comfortable Seating", "Door-to-door"), 
            routeDescription = "Chennai -> Salem -> Coimbatore -> Ooty", isRecommended = false
        ),
        TransportOption(
            id = "5", type = TransportType.CAR_RENTAL, providerName = "Rental Car", 
            from = "Chennai", to = "Ooty", departureTime = "Self drive", arrivalTime = "Self drive", 
            durationMinutes = 360, price = 2500, pricingType = PricingType.PER_DAY, rating = 4.5, 
            availableSeats = 5, facilities = listOf("AC", "Self Drive"), 
            routeDescription = "Chennai -> Salem -> Coimbatore -> Ooty", isRecommended = false
        ),
        TransportOption(
            id = "6", type = TransportType.FLIGHT, providerName = "Air India", 
            from = "Chennai", to = "Coimbatore", departureTime = "09:00 AM", arrivalTime = "10:00 AM", 
            durationMinutes = 60, price = 3500, pricingType = PricingType.PER_PERSON, rating = 4.1, 
            availableSeats = 50, facilities = listOf("AC", "Snacks"), 
            routeDescription = "Direct Flight", isRecommended = false
        )
    )

    override suspend fun searchTransport(
        from: String,
        to: String,
        date: String,
        travelers: Int
    ): List<TransportOption> {
        delay(600)
        val lowerFrom = from.lowercase().trim()
        val lowerTo = to.lowercase().trim()
        
        if (lowerFrom.isEmpty() || lowerTo.isEmpty()) return emptyList()

        return mockTransports.filter {
            it.from.lowercase() == lowerFrom && (it.to.lowercase() == lowerTo || it.to.lowercase().contains(lowerTo))
        }
    }

    override suspend fun getTransportById(id: String): TransportOption? {
        delay(300)
        return mockTransports.find { it.id == id }
    }
}
