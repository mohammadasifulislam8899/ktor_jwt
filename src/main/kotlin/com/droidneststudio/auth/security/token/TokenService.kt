package com.droidneststudio.auth.security.token

interface TokenService {
    fun generateAccessToken(config: TokenConfig, vararg claims: TokenClaim): String
    fun generateRefreshToken(): String
}