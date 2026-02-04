package com.droidneststudio.auth.security.token

data class TokenConfig(
    val issuer: String,
    val audience: String,
    val realm: String,
    val accessTokenExpiryMs: Long,
    val refreshTokenExpiryMs: Long,
    val secret: String
)