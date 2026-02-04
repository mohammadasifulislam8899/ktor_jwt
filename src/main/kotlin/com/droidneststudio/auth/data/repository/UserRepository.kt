package com.droidneststudio.auth.data.repository

import com.droidneststudio.auth.data.model.User
import com.droidneststudio.auth.data.model.AccountStatus
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.or
import org.litote.kmongo.and
import org.litote.kmongo.setValue
import java.time.Instant

interface UserRepository {
    suspend fun findById(id: String): User?
    suspend fun findByEmail(email: String, appId: String): User?
    suspend fun findByUsername(username: String, appId: String): User?
    suspend fun findByEmailOrUsername(emailOrUsername: String, appId: String): User?
    suspend fun create(user: User): Boolean
    suspend fun update(user: User): Boolean
    suspend fun updateLastLogin(userId: String, ipAddress: String): Boolean
    suspend fun updatePassword(userId: String, passwordHash: String, salt: String): Boolean
    suspend fun updateStatus(userId: String, status: AccountStatus): Boolean
    suspend fun verifyEmail(userId: String): Boolean
    suspend fun incrementFailedAttempts(userId: String): Boolean
    suspend fun resetFailedAttempts(userId: String): Boolean
    suspend fun lockAccount(userId: String, until: Long): Boolean
    suspend fun delete(id: String): Boolean
}

class MongoUserRepository(
    db: CoroutineDatabase
) : UserRepository {
    
    private val users = db.getCollection<User>("users")
    
    override suspend fun findById(id: String): User? {
        return try {
            users.findOneById(ObjectId(id))
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun findByEmail(email: String, appId: String): User? {
        return users.findOne(
            and(
                User::email eq email.lowercase(),
                User::appId eq appId
            )
        )
    }
    
    override suspend fun findByUsername(username: String, appId: String): User? {
        return users.findOne(
            and(
                User::username eq username.lowercase(),
                User::appId eq appId
            )
        )
    }
    
    override suspend fun findByEmailOrUsername(emailOrUsername: String, appId: String): User? {
        val lowercaseValue = emailOrUsername.lowercase()
        return users.findOne(
            and(
                User::appId eq appId,
                or(
                    User::email eq lowercaseValue,
                    User::username eq lowercaseValue
                )
            )
        )
    }
    
    override suspend fun create(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }
    
    override suspend fun update(user: User): Boolean {
        return users.replaceOne(User::id eq user.id, user).wasAcknowledged()
    }
    
    override suspend fun updateLastLogin(userId: String, ipAddress: String): Boolean {
        return try {
            users.updateOneById(
                ObjectId(userId),
                org.litote.kmongo.combine(
                    setValue(User::lastLoginAt, Instant.now().toEpochMilli()),
                    setValue(User::lastLoginIp, ipAddress),
                    setValue(User::updatedAt, Instant.now().toEpochMilli())
                )
            ).wasAcknowledged()
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun updatePassword(userId: String, passwordHash: String, salt: String): Boolean {
        return try {
            users.updateOneById(
                ObjectId(userId),
                org.litote.kmongo.combine(
                    setValue(User::passwordHash, passwordHash),
                    setValue(User::salt, salt),
                    setValue(User::updatedAt, Instant.now().toEpochMilli())
                )
            ).wasAcknowledged()
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun updateStatus(userId: String, status: AccountStatus): Boolean {
        return try {
            users.updateOneById(
                ObjectId(userId),
                org.litote.kmongo.combine(
                    setValue(User::status, status),
                    setValue(User::updatedAt, Instant.now().toEpochMilli())
                )
            ).wasAcknowledged()
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun verifyEmail(userId: String): Boolean {
        return try {
            users.updateOneById(
                ObjectId(userId),
                org.litote.kmongo.combine(
                    setValue(User::emailVerified, true),
                    setValue(User::status, AccountStatus.ACTIVE),
                    setValue(User::updatedAt, Instant.now().toEpochMilli())
                )
            ).wasAcknowledged()
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun incrementFailedAttempts(userId: String): Boolean {
        return try {
            val user = findById(userId) ?: return false
            users.updateOneById(
                ObjectId(userId),
                setValue(User::failedLoginAttempts, user.failedLoginAttempts + 1)
            ).wasAcknowledged()
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun resetFailedAttempts(userId: String): Boolean {
        return try {
            users.updateOneById(
                ObjectId(userId),
                org.litote.kmongo.combine(
                    setValue(User::failedLoginAttempts, 0),
                    setValue(User::lockedUntil, null)
                )
            ).wasAcknowledged()
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun lockAccount(userId: String, until: Long): Boolean {
        return try {
            users.updateOneById(
                ObjectId(userId),
                setValue(User::lockedUntil, until)
            ).wasAcknowledged()
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun delete(id: String): Boolean {
        return try {
            users.deleteOneById(ObjectId(id)).wasAcknowledged()
        } catch (e: Exception) {
            false
        }
    }
}