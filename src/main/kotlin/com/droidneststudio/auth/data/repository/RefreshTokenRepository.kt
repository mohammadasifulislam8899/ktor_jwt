package com.droidneststudio.auth.data.repository

import com.droidneststudio.auth.data.model.RefreshToken
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.and
import org.litote.kmongo.lt
import org.litote.kmongo.setValue
import java.time.Instant

interface RefreshTokenRepository {
    suspend fun create(refreshToken: RefreshToken): Boolean
    suspend fun findByToken(token: String): RefreshToken?
    suspend fun findByUserId(userId: String): List<RefreshToken>
    suspend fun updateLastUsed(token: String): Boolean
    suspend fun revoke(token: String, reason: String): Boolean
    suspend fun revokeAllForUser(userId: String, reason: String): Boolean
    suspend fun deleteExpired(): Long
}

class MongoRefreshTokenRepository(
    db: CoroutineDatabase
) : RefreshTokenRepository {
    
    private val tokens = db.getCollection<RefreshToken>("refresh_tokens")
    
    override suspend fun create(refreshToken: RefreshToken): Boolean {
        return tokens.insertOne(refreshToken).wasAcknowledged()
    }
    
    override suspend fun findByToken(token: String): RefreshToken? {
        return tokens.findOne(
            and(
                RefreshToken::token eq token,
                RefreshToken::isRevoked eq false
            )
        )
    }
    
    override suspend fun findByUserId(userId: String): List<RefreshToken> {
        return tokens.find(
            and(
                RefreshToken::userId eq userId,
                RefreshToken::isRevoked eq false
            )
        ).toList()
    }
    
    override suspend fun updateLastUsed(token: String): Boolean {
        return tokens.updateOne(
            RefreshToken::token eq token,
            setValue(RefreshToken::lastUsedAt, Instant.now().toEpochMilli())
        ).wasAcknowledged()
    }
    
    override suspend fun revoke(token: String, reason: String): Boolean {
        return tokens.updateOne(
            RefreshToken::token eq token,
            org.litote.kmongo.combine(
                setValue(RefreshToken::isRevoked, true),
                setValue(RefreshToken::revokedAt, Instant.now().toEpochMilli()),
                setValue(RefreshToken::revokedReason, reason)
            )
        ).wasAcknowledged()
    }
    
    override suspend fun revokeAllForUser(userId: String, reason: String): Boolean {
        return tokens.updateMany(
            and(
                RefreshToken::userId eq userId,
                RefreshToken::isRevoked eq false
            ),
            org.litote.kmongo.combine(
                setValue(RefreshToken::isRevoked, true),
                setValue(RefreshToken::revokedAt, Instant.now().toEpochMilli()),
                setValue(RefreshToken::revokedReason, reason)
            )
        ).wasAcknowledged()
    }

    override suspend fun deleteExpired(): Long {
        val now = Instant.now().toEpochMilli()
        val result = tokens.deleteMany(
            RefreshToken::expiresAt lt now
        )
        return result.deletedCount
    }

}