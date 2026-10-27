package de.malteans.backup_control.di

import de.malteans.backup_control.core.data.settings.ApiTokenCipher
import de.malteans.backup_control.core.data.settings.DesktopApiTokenCipher
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<ApiTokenCipher> { DesktopApiTokenCipher() }

        single<HttpClientEngine> { OkHttp.create() }
    }
