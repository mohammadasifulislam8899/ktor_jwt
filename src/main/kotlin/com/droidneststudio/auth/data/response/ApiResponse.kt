package com.droidneststudio.auth.data.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val error: ErrorDetails? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class ErrorDetails(
    val code: String,
    val message: String,
    val field: String? = null,
    val details: Map<String, String>? = null
)

// Helper functions
fun <T> successResponse(data: T, message: String = "Success"): ApiResponse<T> {
    return ApiResponse(
        success = true,
        message = message,
        data = data
    )
}

fun <T> errorResponse(code: String, message: String, details: Map<String, String>? = null): ApiResponse<T> {
    return ApiResponse(
        success = false,
        message = message,
        error = ErrorDetails(code, message, details = details)
    )
}