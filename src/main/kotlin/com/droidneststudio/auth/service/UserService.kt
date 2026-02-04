package com.droidneststudio.auth.service

import com.droidneststudio.auth.data.model.*
import com.droidneststudio.auth.data.repository.*
import com.droidneststudio.auth.data.request.user.*
import com.droidneststudio.auth.data.response.*
import com.droidneststudio.auth.exception.*
import com.droidneststudio.auth.security.hashing.*
import com.droidneststudio.auth.util.Validation
import java.time.Instant

class UserService(
    private val userRepository: UserRepository,
    private val appRepository: AppRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val hashingService: HashingService
) {
    
    suspend fun getProfile(userId: String): ApiResponse<UserResponse> {
        val user = userRepository.findById(userId)
            ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
        
        return successResponse(user.toResponse())
    }
    
    suspend fun updateProfile(userId: String, request: UpdateProfileRequest): ApiResponse<UserResponse> {
        val user = userRepository.findById(userId)
            ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
        
        val updatedProfile = user.profile.copy(
            firstName = request.firstName ?: user.profile.firstName,
            lastName = request.lastName ?: user.profile.lastName,
            displayName = request.displayName ?: user.profile.displayName,
            phone = request.phone?.let { Validation.validatePhone(it) } ?: user.profile.phone,
            avatar = request.avatar ?: user.profile.avatar,
            bio = request.bio ?: user.profile.bio,
            dateOfBirth = request.dateOfBirth ?: user.profile.dateOfBirth,
            gender = request.gender ?: user.profile.gender,
            address = request.address ?: user.profile.address,
            socialLinks = request.socialLinks ?: user.profile.socialLinks
        )
        
        val updatedUser = user.copy(
            profile = updatedProfile,
            updatedAt = Instant.now().toEpochMilli()
        )
        
        val success = userRepository.update(updatedUser)
        if (!success) {
            throw ApiException(ErrorCode.INTERNAL_ERROR)
        }
        
        return successResponse(updatedUser.toResponse(), "Profile updated successfully")
    }
    
    suspend fun changePassword(userId: String, request: ChangePasswordRequest, appId: String): ApiResponse<Unit> {
        val app = appRepository.findById(appId)
            ?: throw ApiException(ErrorCode.APP_NOT_FOUND)
        
        val user = userRepository.findById(userId)
            ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
        
        // Verify current password
        val isValidPassword = hashingService.verify(
            request.currentPassword,
            SaltedHash(user.passwordHash, user.salt)
        )
        
        if (!isValidPassword) {
            throw ApiException(ErrorCode.INVALID_CURRENT_PASSWORD)
        }
        
        // Validate new password
        Validation.validatePassword(request.newPassword, app.config)
        Validation.validatePasswordsMatch(request.newPassword, request.confirmPassword)
        
        // Update password
        val saltedHash = hashingService.generateSaltedHash(request.newPassword)
        val success = userRepository.updatePassword(userId, saltedHash.hash, saltedHash.salt)
        
        if (!success) {
            throw ApiException(ErrorCode.INTERNAL_ERROR)
        }
        
        // Revoke all refresh tokens except current
        refreshTokenRepository.revokeAllForUser(userId, "Password changed")
        
        return successResponse(Unit, "Password changed successfully")
    }
    
    suspend fun deleteAccount(userId: String, password: String): ApiResponse<Unit> {
        val user = userRepository.findById(userId)
            ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
        
        // Verify password
        val isValidPassword = hashingService.verify(
            password,
            SaltedHash(user.passwordHash, user.salt)
        )
        
        if (!isValidPassword) {
            throw ApiException(ErrorCode.INVALID_CREDENTIALS)
        }
        
        // Soft delete - just mark as deleted
        userRepository.updateStatus(userId, AccountStatus.DELETED)
        
        // Revoke all tokens
        refreshTokenRepository.revokeAllForUser(userId, "Account deleted")
        
        return successResponse(Unit, "Account deleted successfully")
    }
    
    suspend fun getActiveSessions(userId: String): ApiResponse<List<SessionResponse>> {
        val tokens = refreshTokenRepository.findByUserId(userId)
        
        val sessions = tokens.map { token ->
            SessionResponse(
                id = token.id.toString(),
                deviceName = token.deviceInfo.deviceName,
                platform = token.deviceInfo.platform,
                ipAddress = token.ipAddress,
                lastUsedAt = token.lastUsedAt,
                createdAt = token.createdAt
            )
        }
        
        return successResponse(sessions)
    }
    
    suspend fun revokeSession(userId: String, sessionId: String): ApiResponse<Unit> {
        val tokens = refreshTokenRepository.findByUserId(userId)
        val token = tokens.find { it.id.toString() == sessionId }
            ?: throw NotFoundException(ErrorCode.NOT_FOUND)
        
        refreshTokenRepository.revoke(token.token, "Manually revoked")
        
        return successResponse(Unit, "Session revoked")
    }
}

@kotlinx.serialization.Serializable
data class SessionResponse(
    val id: String,
    val deviceName: String,
    val platform: String,
    val ipAddress: String,
    val lastUsedAt: Long,
    val createdAt: Long
)