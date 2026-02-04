package com.droidneststudio.auth.routes

import com.droidneststudio.auth.data.response.successResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Route.healthRoutes() {
    
    // Health check
    get("/health") {
        call.respond(
            HttpStatusCode.OK,
            successResponse(
                HealthResponse(
                    status = "healthy",
                    version = "1.0.0",
                    timestamp = System.currentTimeMillis()
                )
            )
        )
    }
    
    // Readiness check
    get("/ready") {
        // Add database connection check here
        call.respond(
            HttpStatusCode.OK,
            successResponse(
                mapOf("ready" to true)
            )
        )
    }
    
    // Liveness check
    get("/live") {
        call.respond(
            HttpStatusCode.OK,
            successResponse(
                mapOf("alive" to true)
            )
        )
    }
}

@Serializable
data class HealthResponse(
    val status: String,
    val version: String,
    val timestamp: Long
)