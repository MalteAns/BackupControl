package de.malteans.backup_control.commandExecution.util

import java.io.File

fun writeToPipe(message: String, pipePath: String): Result<Unit> = runCatching {
    File(pipePath).bufferedWriter().use { writer ->
        writer.write(message)
        writer.newLine()
    }
}
