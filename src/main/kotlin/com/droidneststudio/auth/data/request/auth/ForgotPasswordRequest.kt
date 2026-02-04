package com.droidneststudio.auth.data.request.auth

import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordRequest(
    val email: String
)