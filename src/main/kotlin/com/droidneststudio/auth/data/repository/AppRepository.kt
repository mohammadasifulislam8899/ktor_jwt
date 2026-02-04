package com.droidneststudio.auth.data.repository

import com.droidneststudio.auth.data.model.App
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import java.time.Instant

interface AppRepository {
    suspend fun findById(id: String): App?
    suspend fun findByApiKey(apiKey: String): App?
    suspend fun findByName(name: String): App?
    suspend fun create(app: App): Boolean
    suspend fun update(app: App): Boolean
    suspend fun updateStatus(id: String, isActive: Boolean): Boolean
    suspend fun regenerateApiSecret(id: String): String?
    suspend fun delete(id: String): Boolean
    suspend fun findAll(): List<App>
}

class MongoAppRepository(
    db: CoroutineDatabase
) : AppRepository {
    
    private val apps = db.getCollection<App>("apps")
    
    override suspend fun findById(id: String): App? {
        return try {
            apps.findOneById(ObjectId(id))
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun findByApiKey(apiKey: String): App? {
        return apps.findOne(App::apiKey eq apiKey)
    }
    
    override suspend fun findByName(name: String): App? {
        return apps.findOne(App::name eq name)
    }
    
    override suspend fun create(app: App): Boolean {
        return apps.insertOne(app).wasAcknowledged()
    }
    
    override suspend fun update(app: App): Boolean {
        return apps.replaceOne(App::id eq app.id, app).wasAcknowledged()
    }
    
    override suspend fun updateStatus(id: String, isActive: Boolean): Boolean {
        return try {
            apps.updateOneById(
                ObjectId(id),
                org.litote.kmongo.combine(
                    setValue(App::isActive, isActive),
                    setValue(App::updatedAt, Instant.now().toEpochMilli())
                )
            ).wasAcknowledged()
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun regenerateApiSecret(id: String): String? {
        return try {
            val newSecret = App.generateApiSecret()
            val result = apps.updateOneById(
                ObjectId(id),
                org.litote.kmongo.combine(
                    setValue(App::apiSecret, newSecret),
                    setValue(App::updatedAt, Instant.now().toEpochMilli())
                )
            ).wasAcknowledged()
            if (result) newSecret else null
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun delete(id: String): Boolean {
        return try {
            apps.deleteOneById(ObjectId(id)).wasAcknowledged()
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun findAll(): List<App> {
        return apps.find().toList()
    }
}