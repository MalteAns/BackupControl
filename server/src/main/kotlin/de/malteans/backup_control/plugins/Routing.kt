package de.malteans.backup_control.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting(

) {
    routing {
        route("v1") {
            get("/health") {
                call.respond(HttpStatusCode.OK, mapOf("status" to "ok"))
            }
            authenticate("bearer") {

            }
        }
    }
}