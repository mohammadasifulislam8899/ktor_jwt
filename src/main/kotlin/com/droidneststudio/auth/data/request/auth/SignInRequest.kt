package com.droidneststudio.auth.data.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    val emailOrUsername: String,
    val password: String,
    
    // Device Info
    val deviceId: String = "",
    val deviceName: String = "",
    val platform: String = ""
)