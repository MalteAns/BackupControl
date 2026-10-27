package de.malteans.backup_control.core.data.settings

import kotlinx.coroutines.flow.Flow

class ServerNotConfiguredException : Exception("Server is not configured. Please set server url and api token in the settings.")

interface ServerConfigRepository {
    /** Returns the current server config or null if the server is not configured. */
    fun getServerConfig(): ServerConfig?

    /** Observes the current server config, null when the server is not configured. */
    fun getServerConfigFlow(): Flow<ServerConfig?>

    /** Persists the server url. A blank url removes the value. */
    suspend fun setServerUrl(url: String): Result<Unit>

    /** Persists the api token encrypted. A blank token removes the value. */
    suspend fun setApiToken(token: String): Result<Unit>

    /** Removes the server url and the api token. */
    suspend fun clear(): Result<Unit>
}
