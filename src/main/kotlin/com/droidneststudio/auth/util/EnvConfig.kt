package com.droidneststudio.auth.util

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import java.io.File

object EnvConfig {

    private val dotenv: Dotenv

    init {
        // Debug: Print current working directory
        println("========== ENV CONFIG DEBUG ==========")
        println("Current working directory: ${System.getProperty("user.dir")}")

        // Check if .env file exists
        val envFile = File(".env")
        println(".env file exists: ${envFile.exists()}")
        println(".env absolute path: ${envFile.absolutePath}")

        if (envFile.exists()) {
            println(".env file content preview:")
            envFile.readLines().take(5).forEach { println("  $it") }
        }
        println("=======================================")

        dotenv = dotenv {
            ignoreIfMissing = true
            ignoreIfMalformed = true
            directory = "./"
        }

        // Debug: Print loaded values
        println("========== LOADED VALUES ==========")
        println("MASTER_API_KEY loaded: ${dotenv["MASTER_API_KEY"]?.take(20) ?: "NULL"}...")
        println("MONGODB_URI loaded: ${dotenv["MONGODB_URI"]?.take(30) ?: "NULL"}...")
        println("===================================")
    }

    // MongoDB
    val mongoUri: String get() = get("MONGODB_URI", "mongodb://localhost:27017")
    val mongoDatabase: String get() = get("MONGODB_DATABASE", "auth_db")

    // JWT
    val jwtSecret: String get() = get("JWT_SECRET", "default-secret-change-in-production")
    val jwtIssuer: String get() = get("JWT_ISSUER", "http://0.0.0.0:8080")
    val jwtAudience: String get() = get("JWT_AUDIENCE", "user")
    val jwtRealm: String get() = get("JWT_REALM", "ktor User app")

    // Email
    val smtpHost: String get() = get("SMTP_HOST", "smtp.gmail.com")
    val smtpPort: Int get() = get("SMTP_PORT", "587").toInt()
    val smtpUsername: String get() = get("SMTP_USERNAME", "")
    val smtpPassword: String get() = get("SMTP_PASSWORD", "ouer ahhl yggw mbyz")
    val smtpFromEmail: String get() = get("SMTP_FROM_EMAIL", "mohammadasifulislam8899@gmail.com")
    val smtpFromName: String get() = get("SMTP_FROM_NAME", "DroidNest Studio")

    // Admin
    val masterApiKey: String get() = get("MASTER_API_KEY", "")

    // App
    val appEnvironment: String get() = get("APP_ENVIRONMENT", "development")
    val isDevelopment: Boolean get() = appEnvironment == "development"
    val isProduction: Boolean get() = appEnvironment == "production"

    private fun get(key: String, default: String = ""): String {
        val value = System.getenv(key) ?: dotenv[key] ?: default
        return value
    }
}