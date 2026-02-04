package com.droidneststudio.auth.plugins

import com.droidneststudio.auth.data.response.ApiResponse
import com.droidneststudio.auth.data.response.ErrorDetails
import com.droidneststudio.auth.exception.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.slf4j.LoggerFactory

fun Application.configureStatusPages() {
    val logger = LoggerFactory.getLogger("StatusPages")
    
    install(StatusPages) {
        // Handle ApiException
        exception<ApiException> { call, cause ->
            logger.warn("API Exception: ${cause.errorCode.code} - ${cause.message}")
            
            call.respond(
                cause.statusCode,
                ApiResponse<Unit>(
                    success = false,
                    message = cause.message,
                    error = ErrorDetails(
                        code = cause.errorCode.code,
                        message = cause.message
                    )
                )
            )
        }
        
        // Handle validation exceptions
        exception<ValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse<Unit>(
                    success = false,
                    message = cause.message,
                    error = ErrorDetails(
                        code = cause.errorCode.code,
                        message = cause.message
                    )
                )
            )
        }
        
        // Handle general exceptions
        exception<Throwable> { call, cause ->
            logger.error("Unhandled exception", cause)
            
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse<Unit>(
                    success = false,
                    message = "An unexpected error occurred",
                    error = ErrorDetails(
                        code = "GEN001",
                        message = "Internal server error"
                    )
                )
            )
        }
        
        // Status-based responses
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                status,
                ApiResponse<Unit>(
                    success = false,
                    message = "Resource not found",
                    error = ErrorDetails(
                        code = "GEN003",
                        message = "The requested resource was not found"
                    )
                )
            )
        }
        
        status(HttpStatusCode.TooManyRequests) { call, status ->
            call.respond(
                status,
                ApiResponse<Unit>(
                    success = false,
                    message = "Too many requests",
                    error = ErrorDetails(
                        code = "GEN005",
                        message = "Rate limit exceeded. Please slow down."
                    )
                )
            )
        }
    }
}