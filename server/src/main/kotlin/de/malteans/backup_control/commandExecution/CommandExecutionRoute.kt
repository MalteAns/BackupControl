package de.malteans.backup_control.commandExecution

import de.malteans.backup_control.commandExecution.data.CommandsResponse
import de.malteans.backup_control.commandExecution.service.CommandService
import de.malteans.backup_control.core.data.ErrorCode
import de.malteans.backup_control.core.data.ErrorDto
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerCommandExecutionRoute(
    commandService: CommandService,
) {
    route("/commandExecution") {
        get {
            val commands = commandService.getCommands().getOrThrow()
            call.respond(
                HttpStatusCode.OK,
                CommandsResponse(commands)
            )
        }
        post("/{uuid}") {
            val uuid = call.parameters["uuid"]
                ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorDto(ErrorCode.MISSING_PARAMETER, "Parameter 'uuid' is required")
                )
            // TODO: check if uuid is valid
            commandService.executeCommand(uuid).getOrThrow()
            call.respond(HttpStatusCode.OK)
        }
    }
}