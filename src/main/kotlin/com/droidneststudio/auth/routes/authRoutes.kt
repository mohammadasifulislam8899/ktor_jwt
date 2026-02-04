package com.droidneststudio.auth.routes

import com.droidneststudio.auth.data.request.auth.*
import com.droidneststudio.auth.exception.*
import com.droidneststudio.auth.service.*
import com.droidneststudio.auth.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val authService by inject<AuthService>()
    val appService by inject<AppService>()
    
    route("/api/v1/auth") {
        
        // Validate API Key for all auth routes
        intercept(ApplicationCallPipeline.Plugins) {
            val apiKey = call.apiKey()
                ?: throw ApiException(ErrorCode.INVALID_APP_KEY, HttpStatusCode.Unauthorized)
            
            val app = appService.validateApiKey(apiKey)
                ?: throw ApiException(ErrorCode.INVALID_APP_KEY, HttpStatusCode.Unauthorized)
            
            call.attributes.put(AppIdKey, app.id.toString())
        }
        
        rateLimit(RateLimitName("auth")) {
            
            // Sign Up
            post("/signup") {
                val request = call.receive<SignUpRequest>()
                val appId = call.attributes[AppIdKey]
                val response = authService.signUp(request, appId, call.clientIp())
                call.respond(HttpStatusCode.Created, response)
            }
            
            // Sign In
            post("/signin") {
                val request = call.receive<SignInRequest>()
                val appId = call.attributes[AppIdKey]
                val response = authService.signIn(request, appId, call.clientIp())
                call.respond(HttpStatusCode.OK, response)
            }
            
            // Refresh Token
            post("/refresh") {
                val request = call.receive<RefreshTokenRequest>()
                val appId = call.attributes[AppIdKey]
                val response = authService.refreshToken(request, appId)
                call.respond(HttpStatusCode.OK, response)
            }
        }
        
        rateLimit(RateLimitName("otp")) {
            
            // Verify Email
            post("/verify-email") {
                val request = call.receive<VerifyOtpRequest>()
                val appId = call.attributes[AppIdKey]
                val response = authService.verifyEmail(request, appId)
                call.respond(HttpStatusCode.OK, response)
            }
            
            // Resend Verification OTP
            post("/resend-verification") {
                val email = call.receive<ForgotPasswordRequest>().email
                val appId = call.attributes[AppIdKey]
                val response = authService.resendVerificationOtp(email, appId, call.clientIp())
                call.respond(HttpStatusCode.OK, response)
            }
            
            // Forgot Password
            post("/forgot-password") {
                val request = call.receive<ForgotPasswordRequest>()
                val appId = call.attributes[AppIdKey]
                val response = authService.forgotPassword(request, appId, call.clientIp())
                call.respond(HttpStatusCode.OK, response)
            }
            
            // Reset Password
            post("/reset-password") {
                val request = call.receive<ResetPasswordRequest>()
                val appId = call.attributes[AppIdKey]
                val response = authService.resetPassword(request, appId)
                call.respond(HttpStatusCode.OK, response)
            }
        }
        
        // Protected routes
        authenticate("auth-jwt") {
            
            // Logout
            post("/logout") {
                val userId = call.userId() ?: throw UnauthorizedException()
                val refreshToken = call.request.header("X-Refresh-Token")
                val response = authService.logout(refreshToken, userId)
                call.respond(HttpStatusCode.OK, response)
            }
            
            // Logout from all devices
            post("/logout-all") {
                val userId = call.userId() ?: throw UnauthorizedException()
                val response = authService.logoutAll(userId)
                call.respond(HttpStatusCode.OK, response)
            }
        }
    }
}

// Attribute key for storing App ID
val AppIdKey = io.ktor.util.AttributeKey<String>("appId")