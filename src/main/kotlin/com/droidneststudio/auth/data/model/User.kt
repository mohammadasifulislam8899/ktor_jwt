package com.droidneststudio.auth.data.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.Instant

@Serializable
data class User(
    @BsonId
    @Contextual
    val id: ObjectId = ObjectId(),
    
    // App Reference
    val appId: String,
    
    // Authentication
    val email: String,
    val username: String,
    val passwordHash: String,
    val salt: String,
    
    // Profile Information
    val profile: UserProfile = UserProfile(),
    
    // Account Status
    val status: AccountStatus = AccountStatus.PENDING_VERIFICATION,
    val role: UserRole = UserRole.USER,
    
    // Verification
    val emailVerified: Boolean = false,
    val phoneVerified: Boolean = false,
    
    // Security
    val failedLoginAttempts: Int = 0,
    val lockedUntil: Long? = null,
    
    // Metadata
    val createdAt: Long = Instant.now().toEpochMilli(),
    val updatedAt: Long = Instant.now().toEpochMilli(),
    val lastLoginAt: Long? = null,
    val lastLoginIp: String? = null,
    
    // Preferences
    val preferences: UserPreferences = UserPreferences(),
    
    // Device Tokens (for push notifications)
    val deviceTokens: List<DeviceToken> = emptyList()
)

@Serializable
data class UserProfile(
    val firstName: String = "",
    val lastName: String = "",
    val displayName: String = "",
    val phone: String = "",
    val avatar: String = "",
    val bio: String = "",
    val dateOfBirth: String = "",
    val gender: Gender = Gender.NOT_SPECIFIED,
    val address: Address = Address(),
    val socialLinks: SocialLinks = SocialLinks()
)

@Serializable
data class Address(
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val postalCode: String = ""
)

@Serializable
data class SocialLinks(
    val facebook: String = "",
    val twitter: String = "",
    val instagram: String = "",
    val linkedin: String = "",
    val github: String = "",
    val website: String = ""
)

@Serializable
data class UserPreferences(
    val language: String = "en",
    val timezone: String = "UTC",
    val theme: String = "system",
    val emailNotifications: Boolean = true,
    val pushNotifications: Boolean = true,
    val smsNotifications: Boolean = false
)

@Serializable
data class DeviceToken(
    val token: String,
    val platform: Platform,
    val deviceName: String = "",
    val addedAt: Long = Instant.now().toEpochMilli()
)

@Serializable
enum class AccountStatus {
    PENDING_VERIFICATION,
    ACTIVE,
    SUSPENDED,
    DELETED
}

@Serializable
enum class UserRole {
    USER,
    MODERATOR,
    ADMIN,
    SUPER_ADMIN
}

@Serializable
enum class Gender {
    MALE,
    FEMALE,
    OTHER,
    NOT_SPECIFIED
}

@Serializable
enum class Platform {
    ANDROID,
    IOS,
    WEB
}