package com.droidneststudio.auth.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.droidneststudio.auth.security.token.TokenConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val tokenConfig by inject<TokenConfig>()
    
    // CORS
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader("X-API-Key")
        
        allowCredentials = true
        anyHost() // Configure for production!
    }
    
    // JWT Authentication
    authentication {
        jwt("auth-jwt") {
            realm = tokenConfig.realm
            
            verifier(
                JWT.require(Algorithm.HMAC256(tokenConfig.secret))
                    .withAudience(tokenConfig.audience)
                    .withIssuer(tokenConfig.issuer)
                    .build()
            )
            
            validate { credential ->
                if (credential.payload.audience.contains(tokenConfig.audience)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf(
                        "success" to false,
                        "message" to "Token is invalid or expired",
                        "error" to mapOf(
                            "code" to "AUTH004",
                            "message" to "Invalid or expired token"
                        )
                    )
                )
            }
        }
    }
}