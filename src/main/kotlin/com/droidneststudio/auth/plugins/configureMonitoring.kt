package com.droidneststudio.auth.plugins

import io.ktor.http.HttpHeaders
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import org.slf4j.event.Level
import java.util.*

fun Application.configureMonitoring() {
    // Call ID for request tracing
    install(CallId) {
        header(HttpHeaders.XRequestId)
        generate { UUID.randomUUID().toString() }
        verify { it.isNotEmpty() }
    }
    
    // Call Logging
    install(CallLogging) {
        level = Level.INFO
        callIdMdc("call-id")
        
        filter { call ->
            call.request.path().startsWith("/")
        }
        
        format { call ->
            val status = call.response.status()
            val method = call.request.httpMethod.value
            val path = call.request.path()
            val duration = call.processingTimeMillis()
            val clientIp = call.request.header("X-Forwarded-For") 
                ?: call.request.local.remoteHost
            
            "$method $path - $status (${duration}ms) - $clientIp"
        }
    }
}