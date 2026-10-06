package com.tripdirect.network

import com.tripdirect.data.model.TripPlanRequest
import com.tripdirect.util.NetworkErrorState
import com.tripdirect.util.NetworkErrorType
import com.tripdirect.viewmodel.ExplorePlacesViewModel
import com.tripdirect.viewmodel.HotelSearchViewModel
import com.tripdirect.viewmodel.LocationSearchViewModel
import com.tripdirect.viewmodel.TransportSearchViewModel
import com.tripdirect.viewmodel.TripPlannerViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDate

class NetworkRetryUnitTest {

    @Test
    fun testErrorClassifier_offlineExceptionsClassifiedCorrectly() {
        val unknownHost = NetworkErrorState.fromThrowable(UnknownHostException("Unable to resolve host"))
        assertEquals(NetworkErrorType.DEVICE_OFFLINE, unknownHost.type)
        assertTrue(unknownHost.isOffline)
        assertTrue(unknownHost.message.contains("No internet connection"))

        val connectException = NetworkErrorState.fromThrowable(ConnectException("Connection refused"))
        assertEquals(NetworkErrorType.DEVICE_OFFLINE, connectException.type)
        assertTrue(connectException.isOffline)

        // When device is detected offline, all errors map to DEVICE_OFFLINE
        val forcedOffline = NetworkErrorState.fromThrowable(RuntimeException("Random error"), isDeviceOnline = false)
        assertEquals(NetworkErrorType.DEVICE_OFFLINE, forcedOffline.type)
        assertTrue(forcedOffline.isOffline)
    }

    @Test
    fun testErrorClassifier_timeoutClassifiedCorrectly() {
        val timeout = NetworkErrorState.fromThrowable(SocketTimeoutException("Read timed out"))
        assertEquals(NetworkErrorType.NETWORK_TIMEOUT, timeout.type)
        assertFalse(timeout.isOffline)
        assertTrue(timeout.message.contains("timed out"))
    }

    @Test
    fun testErrorClassifier_httpExceptionsClassifiedCorrectly() {
        // HTTP 503 Server Unavailable
        val errorBody503 = "Service Unavailable".toResponseBody("text/plain".toMediaTypeOrNull())
        val http503 = HttpException(Response.error<String>(503, errorBody503))
        val state503 = NetworkErrorState.fromThrowable(http503)
        assertEquals(NetworkErrorType.SERVER_ERROR, state503.type)
        assertFalse(state503.isOffline)
        assertEquals(503, state503.statusCode)

        // HTTP 404 Not Found
        val errorBody404 = "Not Found".toResponseBody("text/plain".toMediaTypeOrNull())
        val http404 = HttpException(Response.error<String>(404, errorBody404))
        val state404 = NetworkErrorState.fromThrowable(http404)
        assertEquals(NetworkErrorType.CLIENT_ERROR, state404.type)
        assertFalse(state404.isOffline)
        assertEquals(404, state404.statusCode)
    }

    @Test
    fun testErrorClassifier_genericIoExceptionClassifiedAsServerUnavailable() {
        val ioException = NetworkErrorState.fromThrowable(IOException("Broken pipe"))
        assertEquals(NetworkErrorType.SERVER_UNAVAILABLE, ioException.type)
        assertFalse(ioException.isOffline)
    }

    @Test
    fun testLocationSearchViewModel_preservesQueryAcrossRetry() {
        val viewModel = LocationSearchViewModel()
        viewModel.onSearchQueryChange("Ooty Botanical Garden")

        assertEquals("Ooty Botanical Garden", viewModel.searchQuery.value)

        // Retry preserves user typed query
        viewModel.retrySearch()
        assertEquals("Ooty Botanical Garden", viewModel.searchQuery.value)
    }

    @Test
    fun testTripPlannerViewModel_preservesInputsAcrossRetry() {
        val viewModel = TripPlannerViewModel()
        viewModel.setFromLocation("Bengaluru")
        viewModel.setDestination("Munnar")
        val start = LocalDate.now().plusDays(2)
        val end = LocalDate.now().plusDays(5)
        viewModel.setDates(start, end)
        viewModel.setTravelers(4)
        viewModel.setBudget("₹25,000")
        viewModel.toggleInterest("Nature")
        viewModel.setTravelStyle("Relaxed")

        // Assert all parameters preserved
        assertEquals("Bengaluru", viewModel.fromLocation.value)
        assertEquals("Munnar", viewModel.destination.value)
        assertEquals(start, viewModel.startDate.value)
        assertEquals(end, viewModel.endDate.value)
        assertEquals(4, viewModel.travelers.value)
        assertEquals("₹25,000", viewModel.budget.value)
        assertTrue(viewModel.interests.value.contains("Nature"))
        assertEquals("Relaxed", viewModel.travelStyle.value)

        // Simulate retry call
        viewModel.retryGenerateTrip()

        // Inputs must remain unchanged after retry
        assertEquals("Bengaluru", viewModel.fromLocation.value)
        assertEquals("Munnar", viewModel.destination.value)
        assertEquals(4, viewModel.travelers.value)
        assertEquals("₹25,000", viewModel.budget.value)
    }

    @Test
    fun testHotelSearchViewModel_preservesCriteriaAcrossRetry() {
        val viewModel = HotelSearchViewModel()
        viewModel.setDestination("Goa")
        val checkIn = LocalDate.now().plusDays(3)
        val checkOut = LocalDate.now().plusDays(7)
        viewModel.setCheckInDate(checkIn)
        viewModel.setCheckOutDate(checkOut)
        viewModel.setGuests(adults = 3, children = 1, rooms = 2)

        assertEquals("Goa", viewModel.destination.value)
        assertEquals(checkIn, viewModel.checkInDate.value)
        assertEquals(checkOut, viewModel.checkOutDate.value)
        assertEquals(3, viewModel.adults.value)
        assertEquals(1, viewModel.children.value)
        assertEquals(2, viewModel.rooms.value)

        viewModel.retrySearch()

        assertEquals("Goa", viewModel.destination.value)
        assertEquals(checkIn, viewModel.checkInDate.value)
        assertEquals(checkOut, viewModel.checkOutDate.value)
        assertEquals(3, viewModel.adults.value)
    }

    @Test
    fun testTransportSearchViewModel_preservesCriteriaAcrossRetry() {
        val viewModel = TransportSearchViewModel()
        viewModel.setFromLocation("Chennai")
        viewModel.setToLocation("Kodaikanal")
        val travelDate = LocalDate.now().plusDays(5)
        viewModel.setTravelDate(travelDate)
        viewModel.setTravelers(2, 1)

        assertEquals("Chennai", viewModel.fromLocation.value)
        assertEquals("Kodaikanal", viewModel.toLocation.value)
        assertEquals(travelDate, viewModel.travelDate.value)
        assertEquals(2, viewModel.travelers.value)
        assertEquals(1, viewModel.children.value)

        viewModel.retrySearch()

        assertEquals("Chennai", viewModel.fromLocation.value)
        assertEquals("Kodaikanal", viewModel.toLocation.value)
        assertEquals(travelDate, viewModel.travelDate.value)
    }

    @Test
    fun testExplorePlacesViewModel_preservesQueryAcrossRetry() {
        val viewModel = ExplorePlacesViewModel()
        viewModel.onSearchQueryChange("Beach")
        assertEquals("Beach", viewModel.searchQuery.value)

        viewModel.retry()
        assertEquals("Beach", viewModel.searchQuery.value)
    }
}
