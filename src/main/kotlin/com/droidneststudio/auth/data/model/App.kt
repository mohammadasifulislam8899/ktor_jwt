package com.droidneststudio.auth.data.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.Instant
import java.util.*

@Serializable
data class App(
    @BsonId
    @Contextual
    val id: ObjectId = ObjectId(),
    
    // App Information
    val name: String,
    val description: String = "",
    val apiKey: String = generateApiKey(),
    val apiSecret: String = generateApiSecret(),
    
    // Status
    val isActive: Boolean = true,
    
    // Configuration
    val config: AppConfig = AppConfig(),
    
    // Allowed Origins (for CORS)
    val allowedOrigins: List<String> = listOf("*"),
    
    // Rate Limiting
    val rateLimit: RateLimitConfig = RateLimitConfig(),
    
    // Metadata
    val ownerId: String = "",
    val createdAt: Long = Instant.now().toEpochMilli(),
    val updatedAt: Long = Instant.now().toEpochMilli()
) {
    companion object {
        fun generateApiKey(): String = "ak_${UUID.randomUUID().toString().replace("-", "")}"
        fun generateApiSecret(): String = "as_${UUID.randomUUID().toString().replace("-", "")}${UUID.randomUUID().toString().replace("-", "")}"
    }
}

@Serializable
data class AppConfig(
    // Token Configuration
    val accessTokenExpiryMinutes: Int = 15,
    val refreshTokenExpiryDays: Int = 30,
    
    // Features
    val emailVerificationRequired: Boolean = true,
    val phoneVerificationRequired: Boolean = false,
    val allowSocialLogin: Boolean = true,
    val allowMultipleSessions: Boolean = true,
    val maxActiveSessions: Int = 5,
    
    // Password Policy
    val minPasswordLength: Int = 8,
    val requireUppercase: Boolean = true,
    val requireLowercase: Boolean = true,
    val requireNumbers: Boolean = true,
    val requireSpecialChars: Boolean = false,
    
    // Account Lockout
    val maxFailedLoginAttempts: Int = 5,
    val lockoutDurationMinutes: Int = 15,
    
    // Custom Fields (app can add their own fields)
    val customUserFields: Map<String, String> = emptyMap()
)

@Serializable
data class RateLimitConfig(
    val requestsPerMinute: Int = 60,
    val requestsPerHour: Int = 1000,
    val signUpPerHour: Int = 10,
    val signInPerMinute: Int = 5,
    val otpPerHour: Int = 5
)