package de.malteans.backup_control.di

import de.malteans.backup_control.core.data.network.ApiConfig
import de.malteans.backup_control.core.data.network.DesktopBuildConfig
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<ApiConfig> { ApiConfig(apiToken = DesktopBuildConfig.API_TOKEN) }

        single<HttpClientEngine> { OkHttp.create() }
    }