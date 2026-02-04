package com.droidneststudio.auth.di

import com.droidneststudio.auth.data.repository.*
import com.droidneststudio.auth.security.hashing.*
import com.droidneststudio.auth.security.token.*
import com.droidneststudio.auth.service.*
import com.droidneststudio.auth.util.EnvConfig
import io.ktor.server.application.*
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun appModule(application: Application) = module {

    // Database
    single<CoroutineDatabase> {
        val connectionString = EnvConfig.mongoUri
        val dbName = EnvConfig.mongoDatabase

        application.log.info("Connecting to MongoDB: $dbName")

        KMongo.createClient(connectionString)
            .coroutine
            .getDatabase(dbName)
    }

    // Repositories
    single<UserRepository> { MongoUserRepository(get()) }
    single<AppRepository> { MongoAppRepository(get()) }
    single<RefreshTokenRepository> { MongoRefreshTokenRepository(get()) }
    single<OtpRepository> { MongoOtpRepository(get()) }

    // Security
    single<HashingService> { BCryptHashingService() }
    single<TokenService> { JwtTokenService() }

    // Token Config
    single {
        val accessExpiry = application.environment.config
            .propertyOrNull("jwt.accessTokenExpiry")?.getString()?.toLongOrNull() ?: 900000L
        val refreshExpiry = application.environment.config
            .propertyOrNull("jwt.refreshTokenExpiry")?.getString()?.toLongOrNull() ?: 2592000000L

        TokenConfig(
            issuer = EnvConfig.jwtIssuer,
            audience = EnvConfig.jwtAudience,
            realm = EnvConfig.jwtRealm,
            accessTokenExpiryMs = accessExpiry,
            refreshTokenExpiryMs = refreshExpiry,
            secret = EnvConfig.jwtSecret
        )
    }

    // Email Service
    single {
        EmailService(
            host = EnvConfig.smtpHost,
            port = EnvConfig.smtpPort,
            username = EnvConfig.smtpUsername,
            password = EnvConfig.smtpPassword,
            fromEmail = EnvConfig.smtpFromEmail,
            fromName = EnvConfig.smtpFromName
        )
    }

    // Services
    single { OtpService(get(), get()) }
    single { AuthService(get(), get(), get(), get(), get(), get(), get(), get()) }
    single { UserService(get(), get(), get(), get()) }
    single { AppService(get()) }
}