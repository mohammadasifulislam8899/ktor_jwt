package com.droidneststudio.auth.exception

import io.ktor.http.*

open class ApiException(
    val errorCode: ErrorCode,
    val statusCode: HttpStatusCode = HttpStatusCode.BadRequest,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null
) : Exception(message, cause)

class UnauthorizedException(
    errorCode: ErrorCode = ErrorCode.INVALID_CREDENTIALS
) : ApiException(errorCode, HttpStatusCode.Unauthorized)

class NotFoundException(
    errorCode: ErrorCode = ErrorCode.NOT_FOUND
) : ApiException(errorCode, HttpStatusCode.NotFound)

class ConflictException(
    errorCode: ErrorCode = ErrorCode.USER_ALREADY_EXISTS
) : ApiException(errorCode, HttpStatusCode.Conflict)

class ForbiddenException(
    errorCode: ErrorCode = ErrorCode.FORBIDDEN
) : ApiException(errorCode, HttpStatusCode.Forbidden)

class ValidationException(
    message: String,
    errorCode: ErrorCode = ErrorCode.VALIDATION_ERROR
) : ApiException(errorCode, HttpStatusCode.BadRequest, message)

class RateLimitException(
    errorCode: ErrorCode = ErrorCode.RATE_LIMITED
) : ApiException(errorCode, HttpStatusCode.TooManyRequests)