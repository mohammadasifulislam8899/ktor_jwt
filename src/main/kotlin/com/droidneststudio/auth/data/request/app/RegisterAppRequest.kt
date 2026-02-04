package com.droidneststudio.auth.data.request.app

import com.droidneststudio.auth.data.model.AppConfig
import kotlinx.serialization.Serializable

@Serializable
data class RegisterAppRequest(
    val name: String,
    val description: String = "",
    val allowedOrigins: List<String> = listOf("*"),
    val config: AppConfig = AppConfig()
)