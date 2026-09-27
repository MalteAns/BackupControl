package de.malteans.backup_control.di

import de.malteans.backup_control.BuildConfig
import de.malteans.backup_control.core.data.network.ApiConfig
import org.koin.core.module.Module
import org.koin.dsl.module

val androidAppModule: Module
    get() = module {
        single<ApiConfig> { ApiConfig(BuildConfig.API_TOKEN) }
    }