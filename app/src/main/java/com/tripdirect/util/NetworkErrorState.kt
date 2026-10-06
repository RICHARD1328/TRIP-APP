package com.tripdirect.util

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

enum class NetworkErrorType {
    DEVICE_OFFLINE,
    NETWORK_TIMEOUT,
    SERVER_UNAVAILABLE,
    CLIENT_ERROR,
    SERVER_ERROR,
    INVALID_RESPONSE,
    UNKNOWN
}

data class NetworkErrorState(
    val type: NetworkErrorType,
    val message: String,
    val isOffline: Boolean = (type == NetworkErrorType.DEVICE_OFFLINE),
    val statusCode: Int? = null,
    val rawException: Throwable? = null
) {
    companion object {
        fun fromThrowable(throwable: Throwable, isDeviceOnline: Boolean = true): NetworkErrorState {
            if (!isDeviceOnline) {
                return NetworkErrorState(
                    type = NetworkErrorType.DEVICE_OFFLINE,
                    message = "No internet connection. Check your connection and try again.",
                    isOffline = true,
                    rawException = throwable
                )
            }

            return when (throwable) {
                is UnknownHostException, is ConnectException -> NetworkErrorState(
                    type = NetworkErrorType.DEVICE_OFFLINE,
                    message = "No internet connection. Check your connection and try again.",
                    isOffline = true,
                    rawException = throwable
                )
                is SocketTimeoutException -> NetworkErrorState(
                    type = NetworkErrorType.NETWORK_TIMEOUT,
                    message = "Request timed out. Check your connection and try again.",
                    isOffline = false,
                    rawException = throwable
                )
                is SocketException -> NetworkErrorState(
                    type = NetworkErrorType.SERVER_UNAVAILABLE,
                    message = "Network connection failed. Please try again.",
                    isOffline = false,
                    rawException = throwable
                )
                is HttpException -> {
                    val code = throwable.code()
                    when {
                        code in 500..599 -> NetworkErrorState(
                            type = NetworkErrorType.SERVER_ERROR,
                            message = "Server is currently unavailable (Error $code). Please try again later.",
                            isOffline = false,
                            statusCode = code,
                            rawException = throwable
                        )
                        code in 400..499 -> NetworkErrorState(
                            type = NetworkErrorType.CLIENT_ERROR,
                            message = "Unable to process request (Error $code). Please verify details and try again.",
                            isOffline = false,
                            statusCode = code,
                            rawException = throwable
                        )
                        else -> NetworkErrorState(
                            type = NetworkErrorType.UNKNOWN,
                            message = "Unable to complete request ($code). Please try again.",
                            isOffline = false,
                            statusCode = code,
                            rawException = throwable
                        )
                    }
                }
                is IOException -> NetworkErrorState(
                    type = NetworkErrorType.SERVER_UNAVAILABLE,
                    message = "Unable to connect to the server. Please try again.",
                    isOffline = false,
                    rawException = throwable
                )
                else -> NetworkErrorState(
                    type = NetworkErrorType.UNKNOWN,
                    message = throwable.localizedMessage?.takeIf { it.isNotBlank() }
                        ?: "Unable to load this information right now. Please try again.",
                    isOffline = false,
                    rawException = throwable
                )
            }
        }

        fun offline(customMessage: String? = null): NetworkErrorState {
            return NetworkErrorState(
                type = NetworkErrorType.DEVICE_OFFLINE,
                message = customMessage ?: "No internet connection. Check your connection and try again.",
                isOffline = true
            )
        }

        fun serverError(customMessage: String? = null, code: Int? = null): NetworkErrorState {
            return NetworkErrorState(
                type = NetworkErrorType.SERVER_ERROR,
                message = customMessage ?: "Unable to load this information right now. Please try again.",
                isOffline = false,
                statusCode = code
            )
        }
    }
}
