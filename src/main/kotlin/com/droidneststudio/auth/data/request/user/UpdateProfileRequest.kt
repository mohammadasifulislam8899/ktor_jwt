package com.droidneststudio.auth.data.request.user

import com.droidneststudio.auth.data.model.*
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val displayName: String? = null,
    val phone: String? = null,
    val avatar: String? = null,
    val bio: String? = null,
    val dateOfBirth: String? = null,
    val gender: Gender? = null,
    val address: Address? = null,
    val socialLinks: SocialLinks? = null
)