package com.droidneststudio.auth.data.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String,
    val confirmPassword: String
)