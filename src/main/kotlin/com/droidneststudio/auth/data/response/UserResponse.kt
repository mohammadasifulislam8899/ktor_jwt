package com.droidneststudio.auth.data.response

import com.droidneststudio.auth.data.model.AccountStatus
import com.droidneststudio.auth.data.model.Address
import com.droidneststudio.auth.data.model.Gender
import com.droidneststudio.auth.data.model.SocialLinks
import com.droidneststudio.auth.data.model.User
import com.droidneststudio.auth.data.model.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val username: String,
    val profile: UserProfileResponse,
    val status: AccountStatus,
    val role: UserRole,
    val emailVerified: Boolean,
    val phoneVerified: Boolean,
    val createdAt: Long,
    val lastLoginAt: Long?
)

@Serializable
data class UserProfileResponse(
    val firstName: String,
    val lastName: String,
    val displayName: String,
    val phone: String,
    val avatar: String,
    val bio: String,
    val dateOfBirth: String,
    val gender: Gender,
    val address: Address,
    val socialLinks: SocialLinks
)

// Extension function to convert User to UserResponse
fun User.toResponse(): UserResponse {
    return UserResponse(
        id = this.id.toString(),
        email = this.email,
        username = this.username,
        profile = UserProfileResponse(
            firstName = this.profile.firstName,
            lastName = this.profile.lastName,
            displayName = this.profile.displayName,
            phone = this.profile.phone,
            avatar = this.profile.avatar,
            bio = this.profile.bio,
            dateOfBirth = this.profile.dateOfBirth,
            gender = this.profile.gender,
            address = this.profile.address,
            socialLinks = this.profile.socialLinks
        ),
        status = this.status,
        role = this.role,
        emailVerified = this.emailVerified,
        phoneVerified = this.phoneVerified,
        createdAt = this.createdAt,
        lastLoginAt = this.lastLoginAt
    )
}