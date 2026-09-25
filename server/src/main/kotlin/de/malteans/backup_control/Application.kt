package de.malteans.backup_control

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
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(Netty, port = CoreConstants.SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    assert(System.getenv(ServerConstants.API_TOKEN_ENV_VAR) != null) { "API_TOKEN environment variable must be set" }
    assert(System.getenv(ServerConstants.LOG_FILES_PATH_ENV_VAR) != null) { "FILES_DIR environment variable must be set" }

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