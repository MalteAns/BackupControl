package de.malteans.backup_control


sealed class Endpoints(
    private val relativeUrl: String,
    private val apiVersion: String,
) {
    val url: String
        get() = "${CoreConstants.SERVER_URL}/${apiVersion}" + relativeUrl

    sealed interface Backup {
        data object GetAll : Endpoints(
            "/backups",
            "v1",
        )
    }

    sealed interface CommandExecution {
        data object GetCommands : Endpoints(
            "/commandExecution",
            "v1",
        )
        data class ExecuteCommand(val uuid: String) : Endpoints(
            "/commandExecution/$uuid",
            "v1",
        )
    }

    sealed interface Files {
        // TODO: refactor files route to be in backups route. combine getFile with getBackup
        data object GetAll : Endpoints(
            "/files",
            "v1",
        )
        data class GetFile(val fileName: String) : Endpoints(
            "/files/$fileName",
            "v1",
        )
    }
}