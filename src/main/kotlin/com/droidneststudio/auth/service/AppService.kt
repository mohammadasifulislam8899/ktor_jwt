package com.droidneststudio.auth.service

import com.droidneststudio.auth.data.model.App
import com.droidneststudio.auth.data.repository.AppRepository
import com.droidneststudio.auth.data.request.app.RegisterAppRequest
import com.droidneststudio.auth.data.response.*
import com.droidneststudio.auth.exception.*

class AppService(
    private val appRepository: AppRepository
) {
    
    suspend fun registerApp(request: RegisterAppRequest, ownerId: String): ApiResponse<AppCreatedResponse> {
        // Check if app name exists
        if (appRepository.findByName(request.name) != null) {
            throw ConflictException(ErrorCode.USER_ALREADY_EXISTS)
        }
        
        val app = App(
            name = request.name,
            description = request.description,
            ownerId = ownerId,
            allowedOrigins = request.allowedOrigins,
            config = request.config
        )
        
        val created = appRepository.create(app)
        if (!created) {
            throw ApiException(ErrorCode.INTERNAL_ERROR)
        }
        
        return successResponse(app.toCreatedResponse(), "App registered successfully")
    }
    
    suspend fun getApp(appId: String): ApiResponse<AppResponse> {
        val app = appRepository.findById(appId)
            ?: throw NotFoundException(ErrorCode.APP_NOT_FOUND)
        
        return successResponse(app.toResponse())
    }
    
    suspend fun validateApiKey(apiKey: String): App? {
        val app = appRepository.findByApiKey(apiKey) ?: return null
        if (!app.isActive) return null
        return app
    }
    
    suspend fun regenerateApiSecret(appId: String, ownerId: String): ApiResponse<Map<String, String>> {
        val app = appRepository.findById(appId)
            ?: throw NotFoundException(ErrorCode.APP_NOT_FOUND)
        
        if (app.ownerId != ownerId) {
            throw ForbiddenException()
        }
        
        val newSecret = appRepository.regenerateApiSecret(appId)
            ?: throw ApiException(ErrorCode.INTERNAL_ERROR)
        
        return successResponse(
            mapOf("apiSecret" to newSecret),
            "API Secret regenerated. Save it securely."
        )
    }
}