package com.droidneststudio.auth.data.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val email: String,
    val username: String,
    val password: String,
    val confirmPassword: String,

    // Optional Profile Info
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",

    // Device Info (for session tracking)
    val deviceId: String = "",
    val deviceName: String = "",
    val platform: String = ""
)