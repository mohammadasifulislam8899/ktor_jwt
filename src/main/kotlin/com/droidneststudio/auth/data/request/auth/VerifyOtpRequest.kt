package com.droidneststudio.auth.data.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpRequest(
    val email: String,
    val otp: String
)