package de.malteans.backup_control.backup.routes

import de.malteans.backup_control.backup.services.BackupService
import de.malteans.backup_control.dto.BackupsResponse
import de.malteans.backup_control.mappers.toDto
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
