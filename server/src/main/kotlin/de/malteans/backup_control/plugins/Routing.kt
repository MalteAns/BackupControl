package de.malteans.backup_control.plugins

import de.malteans.backup_control.routes.registerBackupsRoute
import de.malteans.backup_control.routes.registerFilesRoute
import de.malteans.backup_control.services.BackupService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.configureRouting() {
    val backupService by inject<BackupService>()
    routing {
        route("v1") {
            get("/health") {
                call.respond(HttpStatusCode.OK, mapOf("status" to "ok"))
            }
            authenticate("bearer") {
                registerFilesRoute()
                registerBackupsRoute(backupService)
            }
        }
    }
}