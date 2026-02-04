package com.droidneststudio.auth.util

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*

// Get User ID from JWT
fun ApplicationCall.userId(): String? {
    return principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString()
}

// Get App ID from JWT
fun ApplicationCall.appId(): String? {
    return principal<JWTPrincipal>()?.payload?.getClaim("appId")?.asString()
}

// Get API Key from header
fun ApplicationCall.apiKey(): String? {
    return request.header("X-API-Key")
}

// Get Client IP
fun ApplicationCall.clientIp(): String {
    return request.header("X-Forwarded-For")?.split(",")?.firstOrNull()?.trim()
        ?: request.header("X-Real-IP")
        ?: request.local.remoteHost
}

// Get User Agent
fun ApplicationCall.userAgent(): String {
    return request.header("User-Agent") ?: "Unknown"
}