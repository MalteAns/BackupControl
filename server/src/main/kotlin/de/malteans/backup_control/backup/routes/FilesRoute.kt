package de.malteans.backup_control.backup.routes

import de.malteans.backup_control.backup.data.FilesResponse
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.registerFilesRoute() {
    route("/files") {
        get {
            val filesDir = File(System.getenv("LOG_FILES_DIR"))
            val fileNames = if (filesDir.exists() && filesDir.isDirectory) {
                filesDir.listFiles()?.map { it.name } ?: emptyList()
            } else {
                emptyList()
            }
            call.respond(FilesResponse(fileNames))
        }
    }
    staticFiles("/files", File("files"))
}