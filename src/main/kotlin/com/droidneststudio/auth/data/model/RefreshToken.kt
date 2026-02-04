package com.droidneststudio.auth.data.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.Instant
import java.util.*

@Serializable
data class RefreshToken(
    @BsonId
    @Contextual
    val id: ObjectId = ObjectId(),
    
    val token: String = UUID.randomUUID().toString(),
    val userId: String,
    val appId: String,
    
    // Device Info
    val deviceInfo: DeviceInfo = DeviceInfo(),
    
    // Validity
    val expiresAt: Long,
    val isRevoked: Boolean = false,
    val revokedAt: Long? = null,
    val revokedReason: String? = null,
    
    // Metadata
    val createdAt: Long = Instant.now().toEpochMilli(),
    val lastUsedAt: Long = Instant.now().toEpochMilli(),
    val ipAddress: String = ""
)

@Serializable
data class DeviceInfo(
    val deviceId: String = "",
    val deviceName: String = "",
    val platform: String = "",
    val osVersion: String = "",
    val appVersion: String = ""
)