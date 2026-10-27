package de.malteans.backup_control.core.data.settings

import androidx.datastore.preferences.core.stringPreferencesKey
import de.malteans.datastore.domain.PreferenceDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class DefaultServerConfigRepository(
    private val preferenceDataStore: PreferenceDataStore,
    private val apiTokenCipher: ApiTokenCipher,
) : ServerConfigRepository {
    companion object {
        val SERVER_URL_KEY = stringPreferencesKey("server_url")
        val API_TOKEN_ENCRYPTED_KEY = stringPreferencesKey("api_token_encrypted")
    }

    override fun getServerConfig(): ServerConfig? {
        val serverUrl = preferenceDataStore.get(SERVER_URL_KEY, "").trim()
        if (serverUrl.isBlank()) return null
        val encryptedToken = preferenceDataStore.get(API_TOKEN_ENCRYPTED_KEY, "")
        if (encryptedToken.isBlank()) return null
        val apiToken = try {
            apiTokenCipher.decrypt(encryptedToken)
        } catch (_: Exception) {
            return null
        }
        if (apiToken.isBlank()) return null
        return ServerConfig(
            serverUrl = serverUrl,
            apiToken = apiToken,
        )
    }

    override fun getServerConfigFlow(): Flow<ServerConfig?> {
        val serverUrlFlow = preferenceDataStore
            .getFlow(SERVER_URL_KEY, "")
            .map { it.trim() }
        val encryptedTokenFlow = preferenceDataStore
            .getFlow(API_TOKEN_ENCRYPTED_KEY, "")
        return combine(serverUrlFlow, encryptedTokenFlow) { serverUrl, encryptedToken ->
            if (serverUrl.isBlank() || encryptedToken.isBlank()) return@combine null
            val apiToken = try {
                apiTokenCipher.decrypt(encryptedToken)
            } catch (_: Exception) {
                return@combine null
            }
            if (apiToken.isBlank()) return@combine null
            ServerConfig(
                serverUrl = serverUrl,
                apiToken = apiToken,
            )
        }
    }

    override suspend fun setServerUrl(url: String): Result<Unit> {
        val trimmedUrl = url.trim()
        return if (trimmedUrl.isBlank()) {
            preferenceDataStore.remove(SERVER_URL_KEY)
        } else {
            preferenceDataStore.set(SERVER_URL_KEY, trimmedUrl)
        }
    }

    override suspend fun setApiToken(token: String): Result<Unit> {
        val trimmedToken = token.trim()
        return if (trimmedToken.isBlank()) {
            preferenceDataStore.remove(API_TOKEN_ENCRYPTED_KEY)
        } else {
            preferenceDataStore.set(API_TOKEN_ENCRYPTED_KEY, apiTokenCipher.encrypt(trimmedToken))
        }
    }

    override suspend fun clear(): Result<Unit> = runCatching {
        preferenceDataStore.remove(SERVER_URL_KEY).getOrThrow()
        preferenceDataStore.remove(API_TOKEN_ENCRYPTED_KEY).getOrThrow()
    }
}
