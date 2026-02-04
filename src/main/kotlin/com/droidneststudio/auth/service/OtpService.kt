package com.droidneststudio.auth.service

import com.droidneststudio.auth.data.model.OTP
import com.droidneststudio.auth.data.model.OTPType
import com.droidneststudio.auth.data.repository.OtpRepository
import com.droidneststudio.auth.exception.ApiException
import com.droidneststudio.auth.exception.ErrorCode
import com.droidneststudio.auth.util.Constants
import io.ktor.http.*
import java.security.SecureRandom
import java.time.Instant

class OtpService(
    private val otpRepository: OtpRepository,
    private val emailService: EmailService
) {
    private val secureRandom = SecureRandom()
    
    suspend fun generateAndSendOtp(
        email: String,
        type: OTPType,
        appId: String,
        userId: String? = null,
        ipAddress: String = ""
    ): Boolean {
        // Check rate limit
        val recentCount = otpRepository.countRecentOtps(email, type, appId, 60)
        if (recentCount >= 5) {
            throw ApiException(ErrorCode.TOO_MANY_OTP_REQUESTS, HttpStatusCode.TooManyRequests)
        }
        
        val code = generateOtpCode()
        val otp = OTP(
            code = code,
            email = email.lowercase(),
            userId = userId,
            appId = appId,
            type = type,
            expiresAt = Instant.now().plusSeconds(Constants.OTP_EXPIRY_MINUTES * 60L).toEpochMilli(),
            ipAddress = ipAddress
        )
        
        val saved = otpRepository.create(otp)
        if (!saved) return false
        
        // Send email
        val subject = when (type) {
            OTPType.EMAIL_VERIFICATION -> "Verify your email"
            OTPType.PASSWORD_RESET -> "Reset your password"
            OTPType.LOGIN_VERIFICATION -> "Login verification"
            else -> "Your verification code"
        }
        
        return emailService.sendOtpEmail(email, code, subject)
    }
    
    suspend fun verifyOtp(
        email: String,
        code: String,
        type: OTPType,
        appId: String
    ): Boolean {
        val otp = otpRepository.findValidOtp(email, code, type, appId)
            ?: throw ApiException(ErrorCode.INVALID_OTP)
        
        if (otp.attempts >= otp.maxAttempts) {
            throw ApiException(ErrorCode.INVALID_OTP)
        }
        
        otpRepository.incrementAttempts(otp.id.toString())
        
        if (otp.code != code) {
            throw ApiException(ErrorCode.INVALID_OTP)
        }
        
        otpRepository.markAsUsed(otp.id.toString())
        return true
    }
    
    private fun generateOtpCode(): String {
        val code = StringBuilder()
        repeat(Constants.OTP_LENGTH) {
            code.append(secureRandom.nextInt(10))
        }
        return code.toString()
    }
}