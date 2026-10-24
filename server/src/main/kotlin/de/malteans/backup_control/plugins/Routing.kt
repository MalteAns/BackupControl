package de.malteans.backup_control.plugins

import de.malteans.backup_control.backup.routes.registerBackupsRoute
import de.malteans.backup_control.backup.routes.registerFilesRoute
import de.malteans.backup_control.backup.services.BackupService
import de.malteans.backup_control.commandExecution.registerCommandExecutionRoute
import de.malteans.backup_control.commandExecution.service.CommandService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Application.configureRouting() {
    val backupService by inject<BackupService>()
    val commandService by inject<CommandService>()
    routing {
        route("v1") {
            get("/health") {
                call.respond(HttpStatusCode.OK, mapOf("status" to "ok"))
            }
            authenticate("bearer") {
                registerFilesRoute()
                registerBackupsRoute(backupService)

                registerCommandExecutionRoute(commandService)
            }
        }
    }
}