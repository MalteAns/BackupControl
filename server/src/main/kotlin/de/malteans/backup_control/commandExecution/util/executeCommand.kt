package de.malteans.backup_control.commandExecution.util

fun writeToPipe(message: String): Result<Unit> {
    return executeCommand("echo", message, ">", "/tmp/testPipe") // FIXME parameterize Pipe
}

private fun executeCommand(vararg command: String): Result<Unit> {
    println("Executing command: ${command.joinToString(" ")}")
    val fullCommand = command.joinToString(" ")
    val process = Runtime.getRuntime().exec(arrayOf("/bin/sh", "-c", fullCommand))
    val result = process.waitFor()
    return if (result == 0) Result.success(Unit)
        else Result.failure(IllegalStateException("Command execution failed with exit code $result"))
}