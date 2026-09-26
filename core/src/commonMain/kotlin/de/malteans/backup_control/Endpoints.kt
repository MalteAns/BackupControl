package de.malteans.backup_control


sealed class Endpoints(
    private val relativeUrl: String,
    private val apiVersion: String,
) {
    val url: String
        get() = "${CoreConstants.SERVER_URL}/${apiVersion}" + relativeUrl

    sealed interface Backups {
        data object GetAll : Endpoints(
            "/backups",
            "v1"
        )
    }

    sealed interface Files {
        data object GetAll : Endpoints(
            "/files",
            "v1"
        )
        data class GetFile(val fileName: String) : Endpoints(
            "/files/$fileName",
            "v1"
        )
    }
}