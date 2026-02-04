package com.droidneststudio.auth.data.response

import com.droidneststudio.auth.data.model.App
import com.droidneststudio.auth.data.model.AppConfig
import kotlinx.serialization.Serializable

@Serializable
data class AppResponse(
    val id: String,
    val name: String,
    val description: String,
    val apiKey: String,
    val isActive: Boolean,
    val config: AppConfig,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class AppCreatedResponse(
    val id: String,
    val name: String,
    val apiKey: String,
    val apiSecret: String,  // Only shown once at creation
    val message: String = "Save the API Secret securely. It won't be shown again."
)

fun App.toResponse(): AppResponse {
    return AppResponse(
        id = this.id.toString(),
        name = this.name,
        description = this.description,
        apiKey = this.apiKey,
        isActive = this.isActive,
        config = this.config,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun App.toCreatedResponse(): AppCreatedResponse {
    return AppCreatedResponse(
        id = this.id.toString(),
        name = this.name,
        apiKey = this.apiKey,
        apiSecret = this.apiSecret
    )
}