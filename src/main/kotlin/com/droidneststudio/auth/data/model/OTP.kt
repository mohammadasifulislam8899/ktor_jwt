package com.droidneststudio.auth.data.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.Instant

@Serializable
data class OTP(
    @BsonId
    @Contextual
    val id: ObjectId = ObjectId(),
    
    val code: String,
    val userId: String? = null,
    val email: String,
    val appId: String,
    val type: OTPType,
    
    val expiresAt: Long,
    val isUsed: Boolean = false,
    val usedAt: Long? = null,
    
    val attempts: Int = 0,
    val maxAttempts: Int = 3,
    
    val createdAt: Long = Instant.now().toEpochMilli(),
    val ipAddress: String = ""
)

@Serializable
enum class OTPType {
    EMAIL_VERIFICATION,
    PHONE_VERIFICATION,
    PASSWORD_RESET,
    LOGIN_VERIFICATION,
    TWO_FACTOR
}