package com.droidneststudio.auth.plugins

import com.droidneststudio.auth.routes.appRoutes
import com.droidneststudio.auth.routes.authRoutes
import com.droidneststudio.auth.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        healthRoutes()
        authRoutes()
        userRoutes()
        appRoutes()
    }
}