package com.droidneststudio.auth.routes

import com.droidneststudio.auth.data.request.user.*
import com.droidneststudio.auth.exception.*
import com.droidneststudio.auth.service.*
import com.droidneststudio.auth.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val userService by inject<UserService>()
    val appService by inject<AppService>()
    
    route("/api/v1/user") {
        
        // Validate API Key
        intercept(ApplicationCallPipeline.Plugins) {
            val apiKey = call.apiKey()
                ?: throw ApiException(ErrorCode.INVALID_APP_KEY, HttpStatusCode.Unauthorized)
            
            appService.validateApiKey(apiKey)
                ?: throw ApiException(ErrorCode.INVALID_APP_KEY, HttpStatusCode.Unauthorized)
        }
        
        authenticate("auth-jwt") {
            rateLimit(RateLimitName("api")) {
                
                // Get Profile
                get("/profile") {
                    val userId = call.userId() ?: throw UnauthorizedException()
                    val response = userService.getProfile(userId)
                    call.respond(HttpStatusCode.OK, response)
                }
                
                // Update Profile
                put("/profile") {
                    val userId = call.userId() ?: throw UnauthorizedException()
                    val request = call.receive<UpdateProfileRequest>()
                    val response = userService.updateProfile(userId, request)
                    call.respond(HttpStatusCode.OK, response)
                }
                
                // Change Password
                post("/change-password") {
                    val userId = call.userId() ?: throw UnauthorizedException()
                    val appId = call.appId() ?: throw UnauthorizedException()
                    val request = call.receive<ChangePasswordRequest>()
                    val response = userService.changePassword(userId, request, appId)
                    call.respond(HttpStatusCode.OK, response)
                }
                
                // Get Active Sessions
                get("/sessions") {
                    val userId = call.userId() ?: throw UnauthorizedException()
                    val response = userService.getActiveSessions(userId)
                    call.respond(HttpStatusCode.OK, response)
                }
                
                // Revoke Session
                delete("/sessions/{sessionId}") {
                    val userId = call.userId() ?: throw UnauthorizedException()
                    val sessionId = call.parameters["sessionId"] 
                        ?: throw ApiException(ErrorCode.BAD_REQUEST)
                    val response = userService.revokeSession(userId, sessionId)
                    call.respond(HttpStatusCode.OK, response)
                }
                
                // Delete Account
                post("/delete-account") {
                    val userId = call.userId() ?: throw UnauthorizedException()
                    val request = call.receive<DeleteAccountRequest>()
                    val response = userService.deleteAccount(userId, request.password)
                    call.respond(HttpStatusCode.OK, response)
                }
            }
        }
    }
}

@Serializable
data class DeleteAccountRequest(
    val password: String
)