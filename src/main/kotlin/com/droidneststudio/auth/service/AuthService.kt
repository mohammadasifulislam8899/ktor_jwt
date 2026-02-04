package com.droidneststudio.auth.service

import com.droidneststudio.auth.data.model.*
import com.droidneststudio.auth.data.repository.*
import com.droidneststudio.auth.data.request.auth.*
import com.droidneststudio.auth.data.response.*
import com.droidneststudio.auth.exception.*
import com.droidneststudio.auth.security.hashing.*
import com.droidneststudio.auth.security.token.*
import com.droidneststudio.auth.util.*
import io.ktor.http.*
import java.time.Instant

class AuthService(
    private val userRepository: UserRepository,
    private val appRepository: AppRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val hashingService: HashingService,
    private val tokenService: TokenService,
    private val otpService: OtpService,
    private val emailService: EmailService,
    private val tokenConfig: TokenConfig
) {
    
    suspend fun signUp(request: SignUpRequest, appId: String, ipAddress: String): ApiResponse<UserResponse> {
        // Get app config
        val app = appRepository.findById(appId)
            ?: throw ApiException(ErrorCode.APP_NOT_FOUND, HttpStatusCode.NotFound)
        
        // Validate inputs
        val email = Validation.validateEmail(request.email)
        val username = Validation.validateUsername(request.username)
        Validation.validatePassword(request.password, app.config)
        Validation.validatePasswordsMatch(request.password, request.confirmPassword)
        
        // Check if user exists
        if (userRepository.findByEmail(email, appId) != null) {
            throw ConflictException(ErrorCode.USER_ALREADY_EXISTS)
        }
        if (userRepository.findByUsername(username, appId) != null) {
            throw ConflictException(ErrorCode.USER_ALREADY_EXISTS)
        }
        
        // Hash password
        val saltedHash = hashingService.generateSaltedHash(request.password)
        
        // Create user
        val user = User(
            appId = appId,
            email = email,
            username = username,
            passwordHash = saltedHash.hash,
            salt = saltedHash.salt,
            profile = UserProfile(
                firstName = request.firstName.trim(),
                lastName = request.lastName.trim(),
                displayName = "${request.firstName} ${request.lastName}".trim().ifEmpty { username },
                phone = Validation.validatePhone(request.phone)
            ),
            status = if (app.config.emailVerificationRequired) 
                AccountStatus.PENDING_VERIFICATION 
            else 
                AccountStatus.ACTIVE,
            emailVerified = !app.config.emailVerificationRequired
        )
        
        val created = userRepository.create(user)
        if (!created) {
            throw ApiException(ErrorCode.INTERNAL_ERROR, HttpStatusCode.InternalServerError)
        }
        
        // Send verification email if required
        if (app.config.emailVerificationRequired) {
            otpService.generateAndSendOtp(
                email = email,
                type = OTPType.EMAIL_VERIFICATION,
                appId = appId,
                userId = user.id.toString(),
                ipAddress = ipAddress
            )
        } else {
            emailService.sendWelcomeEmail(email, user.profile.displayName)
        }
        
        return successResponse(user.toResponse(), "Account created successfully. Please verify your email.")
    }
    
    suspend fun signIn(request: SignInRequest, appId: String, ipAddress: String): ApiResponse<AuthResponse> {
        val app = appRepository.findById(appId)
            ?: throw ApiException(ErrorCode.APP_NOT_FOUND, HttpStatusCode.NotFound)
        
        // Find user
        val user = userRepository.findByEmailOrUsername(request.emailOrUsername, appId)
            ?: throw UnauthorizedException(ErrorCode.INVALID_CREDENTIALS)
        
        // Check account status
        when (user.status) {
            AccountStatus.PENDING_VERIFICATION -> 
                throw ApiException(ErrorCode.ACCOUNT_NOT_VERIFIED, HttpStatusCode.Forbidden)
            AccountStatus.SUSPENDED -> 
                throw ApiException(ErrorCode.ACCOUNT_SUSPENDED, HttpStatusCode.Forbidden)
            AccountStatus.DELETED -> 
                throw UnauthorizedException(ErrorCode.INVALID_CREDENTIALS)
            AccountStatus.ACTIVE -> { /* Continue */ }
        }
        
        // Check if account is locked
        user.lockedUntil?.let {
            if (Instant.now().toEpochMilli() < it) {
                val remainingMinutes = (it - Instant.now().toEpochMilli()) / 60000
                throw ApiException(
                    ErrorCode.ACCOUNT_SUSPENDED,
                    HttpStatusCode.Forbidden,
                    "Account is locked. Try again in $remainingMinutes minutes."
                )
            }
        }
        
        // Verify password
        val isValidPassword = hashingService.verify(
            request.password,
            SaltedHash(user.passwordHash, user.salt)
        )
        
        if (!isValidPassword) {
            userRepository.incrementFailedAttempts(user.id.toString())
            
            // Lock account if too many failed attempts
            val updatedUser = userRepository.findById(user.id.toString())
            if (updatedUser != null && updatedUser.failedLoginAttempts >= app.config.maxFailedLoginAttempts) {
                val lockUntil = Instant.now().plusSeconds(app.config.lockoutDurationMinutes * 60L).toEpochMilli()
                userRepository.lockAccount(user.id.toString(), lockUntil)
            }
            
            throw UnauthorizedException(ErrorCode.INVALID_CREDENTIALS)
        }
        
        // Reset failed attempts
        userRepository.resetFailedAttempts(user.id.toString())
        
        // Update last login
        userRepository.updateLastLogin(user.id.toString(), ipAddress)
        
        // Generate tokens
        val accessToken = tokenService.generateAccessToken(
            tokenConfig,
            TokenClaim(Constants.CLAIM_USER_ID, user.id.toString()),
            TokenClaim(Constants.CLAIM_APP_ID, appId),
            TokenClaim(Constants.CLAIM_ROLE, user.role.name),
            TokenClaim(Constants.CLAIM_EMAIL, user.email)
        )
        
        val refreshTokenString = tokenService.generateRefreshToken()
        val refreshToken = RefreshToken(
            token = refreshTokenString,
            userId = user.id.toString(),
            appId = appId,
            expiresAt = Instant.now().toEpochMilli() + tokenConfig.refreshTokenExpiryMs,
            deviceInfo = DeviceInfo(
                deviceId = request.deviceId,
                deviceName = request.deviceName,
                platform = request.platform
            ),
            ipAddress = ipAddress
        )
        
        refreshTokenRepository.create(refreshToken)
        
        return successResponse(
            AuthResponse(
                accessToken = accessToken,
                refreshToken = refreshTokenString,
                expiresIn = tokenConfig.accessTokenExpiryMs / 1000,
                user = user.toResponse()
            ),
            "Login successful"
        )
    }
    
    suspend fun refreshToken(request: RefreshTokenRequest, appId: String): ApiResponse<TokenRefreshResponse> {
        val storedToken = refreshTokenRepository.findByToken(request.refreshToken)
            ?: throw UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN)
        
        // Check expiry
        if (Instant.now().toEpochMilli() > storedToken.expiresAt) {
            refreshTokenRepository.revoke(request.refreshToken, "Expired")
            throw UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN)
        }
        
        // Check app
        if (storedToken.appId != appId) {
            throw UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN)
        }
        
        // Get user
        val user = userRepository.findById(storedToken.userId)
            ?: throw UnauthorizedException(ErrorCode.USER_NOT_FOUND)
        
        // Revoke old token
        refreshTokenRepository.revoke(request.refreshToken, "Refreshed")
        
        // Generate new tokens
        val accessToken = tokenService.generateAccessToken(
            tokenConfig,
            TokenClaim(Constants.CLAIM_USER_ID, user.id.toString()),
            TokenClaim(Constants.CLAIM_APP_ID, appId),
            TokenClaim(Constants.CLAIM_ROLE, user.role.name),
            TokenClaim(Constants.CLAIM_EMAIL, user.email)
        )
        
        val newRefreshTokenString = tokenService.generateRefreshToken()
        val newRefreshToken = RefreshToken(
            token = newRefreshTokenString,
            userId = user.id.toString(),
            appId = appId,
            expiresAt = Instant.now().toEpochMilli() + tokenConfig.refreshTokenExpiryMs,
            deviceInfo = storedToken.deviceInfo,
            ipAddress = storedToken.ipAddress
        )
        
        refreshTokenRepository.create(newRefreshToken)
        
        return successResponse(
            TokenRefreshResponse(
                accessToken = accessToken,
                refreshToken = newRefreshTokenString,
                expiresIn = tokenConfig.accessTokenExpiryMs / 1000
            ),
            "Token refreshed successfully"
        )
    }
    
    suspend fun verifyEmail(request: VerifyOtpRequest, appId: String): ApiResponse<Unit> {
        val user = userRepository.findByEmail(request.email, appId)
            ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
        
        otpService.verifyOtp(request.email, request.otp, OTPType.EMAIL_VERIFICATION, appId)
        
        userRepository.verifyEmail(user.id.toString())
        emailService.sendWelcomeEmail(user.email, user.profile.displayName)
        
        return successResponse(Unit, "Email verified successfully")
    }
    
    suspend fun resendVerificationOtp(email: String, appId: String, ipAddress: String): ApiResponse<Unit> {
        val user = userRepository.findByEmail(email, appId)
            ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
        
        if (user.emailVerified) {
            throw ApiException(ErrorCode.VALIDATION_ERROR, message = "Email is already verified")
        }
        
        otpService.generateAndSendOtp(
            email = email,
            type = OTPType.EMAIL_VERIFICATION,
            appId = appId,
            userId = user.id.toString(),
            ipAddress = ipAddress
        )
        
        return successResponse(Unit, "Verification code sent")
    }
    
    suspend fun forgotPassword(request: ForgotPasswordRequest, appId: String, ipAddress: String): ApiResponse<Unit> {
        val user = userRepository.findByEmail(request.email, appId)
        
        // Always return success to prevent email enumeration
        if (user != null) {
            otpService.generateAndSendOtp(
                email = request.email,
                type = OTPType.PASSWORD_RESET,
                appId = appId,
                userId = user.id.toString(),
                ipAddress = ipAddress
            )
        }
        
        return successResponse(Unit, "If the email exists, a reset code has been sent")
    }
    
    suspend fun resetPassword(request: ResetPasswordRequest, appId: String): ApiResponse<Unit> {
        val app = appRepository.findById(appId)
            ?: throw ApiException(ErrorCode.APP_NOT_FOUND, HttpStatusCode.NotFound)
        
        val user = userRepository.findByEmail(request.email, appId)
            ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
        
        // Validate new password
        Validation.validatePassword(request.newPassword, app.config)
        Validation.validatePasswordsMatch(request.newPassword, request.confirmPassword)
        
        // Verify OTP
        otpService.verifyOtp(request.email, request.otp, OTPType.PASSWORD_RESET, appId)
        
        // Update password
        val saltedHash = hashingService.generateSaltedHash(request.newPassword)
        userRepository.updatePassword(user.id.toString(), saltedHash.hash, saltedHash.salt)
        
        // Revoke all refresh tokens
        refreshTokenRepository.revokeAllForUser(user.id.toString(), "Password reset")
        
        return successResponse(Unit, "Password reset successfully")
    }
    
    suspend fun logout(refreshToken: String?, userId: String): ApiResponse<Unit> {
        if (refreshToken != null) {
            refreshTokenRepository.revoke(refreshToken, "Logout")
        }
        return successResponse(Unit, "Logged out successfully")
    }
    
    suspend fun logoutAll(userId: String): ApiResponse<Unit> {
        refreshTokenRepository.revokeAllForUser(userId, "Logout all devices")
        return successResponse(Unit, "Logged out from all devices")
    }
}