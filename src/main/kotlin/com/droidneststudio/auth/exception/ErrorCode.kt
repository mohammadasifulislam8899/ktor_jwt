package com.droidneststudio.auth.exception

enum class ErrorCode(val code: String, val message: String) {
    // Authentication Errors
    INVALID_CREDENTIALS("AUTH001", "Invalid username or password"),
    USER_NOT_FOUND("AUTH002", "User not found"),
    USER_ALREADY_EXISTS("AUTH003", "User already exists"),
    INVALID_TOKEN("AUTH004", "Invalid or expired token"),
    INVALID_REFRESH_TOKEN("AUTH005", "Invalid or expired refresh token"),
    ACCOUNT_NOT_VERIFIED("AUTH006", "Please verify your email first"),
    ACCOUNT_SUSPENDED("AUTH007", "Your account has been suspended"),
    ACCOUNT_DELETED("AUTH008", "This account has been deleted"),
    
    // Validation Errors
    VALIDATION_ERROR("VAL001", "Validation failed"),
    INVALID_EMAIL("VAL002", "Invalid email format"),
    WEAK_PASSWORD("VAL003", "Password must be at least 8 characters"),
    INVALID_PHONE("VAL004", "Invalid phone number format"),
    
    // OTP Errors
    INVALID_OTP("OTP001", "Invalid or expired OTP"),
    OTP_EXPIRED("OTP002", "OTP has expired"),
    TOO_MANY_OTP_REQUESTS("OTP003", "Too many OTP requests. Please try later"),
    
    // App Errors
    INVALID_APP_KEY("APP001", "Invalid API key"),
    APP_NOT_FOUND("APP002", "App not found"),
    APP_INACTIVE("APP003", "App is inactive"),
    
    // General Errors
    INTERNAL_ERROR("GEN001", "Internal server error"),
    BAD_REQUEST("GEN002", "Bad request"),
    NOT_FOUND("GEN003", "Resource not found"),
    FORBIDDEN("GEN004", "Access denied"),
    RATE_LIMITED("GEN005", "Too many requests. Please slow down"),
    
    // User Errors
    INVALID_CURRENT_PASSWORD("USR001", "Current password is incorrect"),
    PASSWORDS_DO_NOT_MATCH("USR002", "New passwords do not match")
}