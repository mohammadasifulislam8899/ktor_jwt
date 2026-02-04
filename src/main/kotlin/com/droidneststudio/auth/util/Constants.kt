package com.droidneststudio.auth.util

object Constants {
    // Headers
    const val HEADER_API_KEY = "X-API-Key"
    const val HEADER_APP_ID = "X-App-Id"
    
    // Claims
    const val CLAIM_USER_ID = "userId"
    const val CLAIM_APP_ID = "appId"
    const val CLAIM_ROLE = "role"
    const val CLAIM_EMAIL = "email"
    
    // OTP
    const val OTP_LENGTH = 6
    const val OTP_EXPIRY_MINUTES = 10
    const val OTP_MAX_ATTEMPTS = 3
    
    // Rate Limiting
    const val RATE_LIMIT_AUTH = "auth"
    const val RATE_LIMIT_API = "api"
}