package de.malteans.backup_control.backup.routes

import de.malteans.backup_control.backup.data.BackupsResponse
import de.malteans.backup_control.backup.data.toDto
import de.malteans.backup_control.backup.services.BackupService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerBackupsRoute(
    backupService: BackupService,
) {
    route("/backups") {
        get {
            backupService.updateBackups().getOrThrow()
            val backups = backupService.getBackups().getOrThrow()
            call.respond(
                HttpStatusCode.OK,
                BackupsResponse(backups.map { it.toDto() })
            )
        }
    }
}
