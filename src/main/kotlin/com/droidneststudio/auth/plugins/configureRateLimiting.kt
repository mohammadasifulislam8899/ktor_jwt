package com.droidneststudio.auth.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun Application.configureRateLimiting() {
    install(RateLimit) {
        // Auth endpoints - stricter limits
        register(RateLimitName("auth")) {
            rateLimiter(limit = 10, refillPeriod = 1.minutes)
        }
        
        // General API endpoints
        register(RateLimitName("api")) {
            rateLimiter(limit = 100, refillPeriod = 1.minutes)
        }
        
        // OTP endpoints - very strict
        register(RateLimitName("otp")) {
            rateLimiter(limit = 5, refillPeriod = 1.minutes)
        }
    }
}