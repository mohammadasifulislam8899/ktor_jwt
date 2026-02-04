package com.droidneststudio.auth.util

import com.droidneststudio.auth.data.model.AppConfig
import com.droidneststudio.auth.exception.ErrorCode
import com.droidneststudio.auth.exception.ValidationException


object Validation {
    
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val USERNAME_REGEX = Regex("^[a-zA-Z0-9_]{3,30}$")
    private val PHONE_REGEX = Regex("^\\+?[1-9]\\d{6,14}$")
    
    fun validateEmail(email: String): String {
        val trimmed = email.trim().lowercase()
        if (!EMAIL_REGEX.matches(trimmed)) {
            throw ValidationException("Invalid email format", ErrorCode.INVALID_EMAIL)
        }
        return trimmed
    }
    
    fun validateUsername(username: String): String {
        val trimmed = username.trim().lowercase()
        if (!USERNAME_REGEX.matches(trimmed)) {
            throw ValidationException(
                "Username must be 3-30 characters and contain only letters, numbers, and underscores",
                ErrorCode.VALIDATION_ERROR
            )
        }
        return trimmed
    }
    
    fun validatePassword(password: String, config: AppConfig = AppConfig()): String {
        val errors = mutableListOf<String>()
        
        if (password.length < config.minPasswordLength) {
            errors.add("Password must be at least ${config.minPasswordLength} characters")
        }
        if (config.requireUppercase && !password.any { it.isUpperCase() }) {
            errors.add("Password must contain at least one uppercase letter")
        }
        if (config.requireLowercase && !password.any { it.isLowerCase() }) {
            errors.add("Password must contain at least one lowercase letter")
        }
        if (config.requireNumbers && !password.any { it.isDigit() }) {
            errors.add("Password must contain at least one number")
        }
        if (config.requireSpecialChars && !password.any { !it.isLetterOrDigit() }) {
            errors.add("Password must contain at least one special character")
        }
        
        if (errors.isNotEmpty()) {
            throw ValidationException(errors.joinToString(". "), ErrorCode.WEAK_PASSWORD)
        }
        
        return password
    }
    
    fun validatePhone(phone: String): String {
        if (phone.isBlank()) return ""
        
        val cleaned = phone.replace(Regex("[\\s-()]"), "")
        if (!PHONE_REGEX.matches(cleaned)) {
            throw ValidationException("Invalid phone number format", ErrorCode.INVALID_PHONE)
        }
        return cleaned
    }
    
    fun validatePasswordsMatch(password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            throw ValidationException("Passwords do not match", ErrorCode.PASSWORDS_DO_NOT_MATCH)
        }
    }
}