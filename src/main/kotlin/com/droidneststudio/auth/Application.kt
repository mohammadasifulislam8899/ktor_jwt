// Application.kt
package com.droidneststudio.auth

import com.droidneststudio.auth.plugins.*
import com.droidneststudio.auth.util.EnvConfig
import com.typesafe.config.ConfigFactory
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    // Load .env file first
    val dotenv = dotenv {
        ignoreIfMissing = true
        directory = "./"
    }

    dotenv.entries().forEach { entry ->
        if (System.getenv(entry.key) == null) {
            System.setProperty(entry.key, entry.value)
        }
    }

    // Now load config (will see the system properties)
    val config = HoconApplicationConfig(ConfigFactory.load())
    val port = config.propertyOrNull("ktor.deployment.port")?.getString()?.toInt() ?: 8080
    val host = "0.0.0.0"
    // Force EnvConfig initialization FIRST
    println("Starting application...")
    println("Master API Key check: ${EnvConfig.masterApiKey.isNotEmpty()}")
    embeddedServer(
        Netty,
        port = port,
        host = host,
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureDependencyInjection()
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureRateLimiting()
    configureStatusPages()
    configureRouting()
}