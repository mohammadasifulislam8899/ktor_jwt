package com.droidneststudio.auth.routes

import com.droidneststudio.auth.data.request.app.RegisterAppRequest
import com.droidneststudio.auth.exception.*
import com.droidneststudio.auth.service.AppService
import com.droidneststudio.auth.util.EnvConfig  // ← ADD THIS IMPORT!
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.appRoutes() {
    val appService by inject<AppService>()

    route("/api/v1/apps") {

        post("/register") {
            // ✅ USE EnvConfig - NOT System.getenv()
            val masterKey = EnvConfig.masterApiKey
            val providedKey = call.request.header("X-Master-Key")

            // Debug logging
            println("=== DEBUG ===")
            println("Master Key from EnvConfig: '$masterKey'")
            println("Master Key length: ${masterKey.length}")
            println("Provided Key: '${providedKey ?: "NULL"}'")
            println("Provided Key length: ${providedKey?.length ?: 0}")
            println("Keys match: ${masterKey == providedKey}")
            println("=============")


            if (providedKey.isNullOrBlank() || providedKey != masterKey) {
                throw ForbiddenException()
            }

            val request = call.receive<RegisterAppRequest>()
            val response = appService.registerApp(request, "system")
            call.respond(HttpStatusCode.Created, response)
        }

        // Get app info
        get("/{appId}") {
            val appId = call.parameters["appId"]
                ?: throw ApiException(ErrorCode.BAD_REQUEST)
            val response = appService.getApp(appId)
            call.respond(HttpStatusCode.OK, response)
        }
    }
}