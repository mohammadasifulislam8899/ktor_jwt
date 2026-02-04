package com.droidneststudio.auth.security.hashing

import at.favre.lib.crypto.bcrypt.BCrypt

class BCryptHashingService : HashingService {
    
    companion object {
        private const val COST = 12 // Higher = more secure but slower
    }
    
    override fun generateSaltedHash(value: String): SaltedHash {
        val hash = BCrypt.withDefaults().hashToString(COST, value.toCharArray())
        // BCrypt includes the salt in the hash, so we don't need to store it separately
        return SaltedHash(hash = hash, salt = "")
    }
    
    override fun verify(value: String, saltedHash: SaltedHash): Boolean {
        return BCrypt.verifyer().verify(value.toCharArray(), saltedHash.hash).verified
    }
}