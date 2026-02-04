package com.droidneststudio.auth.plugins

import com.droidneststudio.auth.di.appModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        modules(appModule(this@configureDependencyInjection))
    }
}