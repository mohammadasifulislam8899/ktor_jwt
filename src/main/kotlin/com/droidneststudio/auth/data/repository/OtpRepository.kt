package com.droidneststudio.auth.data.repository

import com.droidneststudio.auth.data.model.OTP
import com.droidneststudio.auth.data.model.OTPType
import com.mongodb.client.result.DeleteResult
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.and
import org.litote.kmongo.gt
import org.litote.kmongo.lt
import org.litote.kmongo.setValue
import java.time.Instant

interface OtpRepository {
    suspend fun create(otp: OTP): Boolean
    suspend fun findValidOtp(email: String, code: String, type: OTPType, appId: String): OTP?
    suspend fun markAsUsed(id: String): Boolean
    suspend fun incrementAttempts(id: String): Boolean
    suspend fun countRecentOtps(email: String, type: OTPType, appId: String, sinceMinutes: Int): Long
    suspend fun deleteExpired(): Long
}

class MongoOtpRepository(
    db: CoroutineDatabase
) : OtpRepository {
    
    private val otps = db.getCollection<OTP>("otps")
    
    override suspend fun create(otp: OTP): Boolean {
        return otps.insertOne(otp).wasAcknowledged()
    }
    
    override suspend fun findValidOtp(email: String, code: String, type: OTPType, appId: String): OTP? {
        val now = Instant.now().toEpochMilli()
        return otps.findOne(
            and(
                OTP::email eq email.lowercase(),
                OTP::code eq code,
                OTP::type eq type,
                OTP::appId eq appId,
                OTP::isUsed eq false,
                OTP::expiresAt gt now   // ✅ properly imported
            )
        )

    }
    
    override suspend fun markAsUsed(id: String): Boolean {
        return otps.updateOneById(
            org.bson.types.ObjectId(id),
            org.litote.kmongo.combine(
                setValue(OTP::isUsed, true),
                setValue(OTP::usedAt, Instant.now().toEpochMilli())
            )
        ).wasAcknowledged()
    }
    
    override suspend fun incrementAttempts(id: String): Boolean {
        val otp = otps.findOneById(org.bson.types.ObjectId(id)) ?: return false
        return otps.updateOneById(
            org.bson.types.ObjectId(id),
            setValue(OTP::attempts, otp.attempts + 1)
        ).wasAcknowledged()
    }
    
    override suspend fun countRecentOtps(email: String, type: OTPType, appId: String, sinceMinutes: Int): Long {
        val since = Instant.now().minusSeconds(sinceMinutes * 60L).toEpochMilli()
        return otps.countDocuments(
            and(
                OTP::email eq email.lowercase(),
                OTP::type eq type,
                OTP::appId eq appId,
                OTP::createdAt gt since   // ✅ gt properly imported
            )
        )

    }

    override suspend fun deleteExpired(): Long {
        val now = Instant.now().toEpochMilli()
        val result: DeleteResult = otps.deleteMany(
            OTP::expiresAt lt now
        )
        return result.deletedCount
    }
}