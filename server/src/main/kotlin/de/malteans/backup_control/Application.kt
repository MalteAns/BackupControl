package de.malteans.backup_control

import de.malteans.backup_control.commandExecution.service.CommandConfigLoader
import de.malteans.backup_control.commandExecution.service.CommandService
import de.malteans.backup_control.di.module
import de.malteans.backup_control.plugins.configureRouting
import de.malteans.backup_control.plugins.configureStatusPages
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory

fun main() {
    embeddedServer(Netty, port = CoreConstants.SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    assert(System.getenv(ServerConstants.API_TOKEN_ENV_VAR) != null) { "API_TOKEN environment variable must be set" }
    assert(System.getenv(ServerConstants.LOG_FILES_PATH_ENV_VAR) != null) { "LOG_FILES_PATH environment variable must be set" }

    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(CallLogging)
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
    configureStatusPages()

    install(Koin) {
        slf4jLogger()
        modules(
            module
        )
    }

    loadCommandConfig()

    install(Authentication) {
        bearer("bearer") {
            authenticate { tokenCredential ->
                if (System.getenv(ServerConstants.API_TOKEN_ENV_VAR).equals(tokenCredential.token))
                    UserIdPrincipal("api-user")
                else null
            }
        }
    }

    configureRouting()
}

private fun Application.loadCommandConfig() {
    val logger = LoggerFactory.getLogger(Application::class.java)
    val configPath = System.getenv(ServerConstants.COMMANDS_CONFIG_PATH_ENV_VAR)
        ?: ServerConstants.DEFAULT_COMMANDS_CONFIG_PATH

    CommandConfigLoader.loadFromFile(configPath)
        .onSuccess { config ->
            val commandService by inject<CommandService>()
            commandService.setPipePath(config.pipePath)
                .onSuccess {
                    logger.info("Successfully set pipe path from config")
                }
                .onFailure { error ->
                    logger.error("Failed to set pipe path from config", error)
                }
            commandService.updateCommands(config.commands)
                .onSuccess {
                    logger.info("Successfully loaded ${config.commands.size} commands from config file: $configPath")
                }
                .onFailure { error ->
                    logger.error("Failed to update commands in database", error)
                }
        }
        .onFailure { error ->
            logger.error("Failed to load command config from file: $configPath", error)
        }
}
