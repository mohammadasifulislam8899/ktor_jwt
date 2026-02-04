package com.droidneststudio.auth.security.hashing

interface HashingService {
    fun generateSaltedHash(value: String): SaltedHash
    fun verify(value: String, saltedHash: SaltedHash): Boolean
}

data class SaltedHash(
    val hash: String,
    val salt: String
)