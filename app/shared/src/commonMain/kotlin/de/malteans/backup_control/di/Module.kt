package de.malteans.backup_control.di

import de.malteans.backup_control.backups.data.DefaultBackupRemoteSource
import de.malteans.backup_control.backups.domain.BackupRemoteSource
import de.malteans.backup_control.backups.presentation.details.BackupDetailsViewModel
import de.malteans.backup_control.backups.presentation.overview.BackupOverviewViewModel
import de.malteans.backup_control.core.data.network.HttpClientFactory
import de.malteans.datastore.data.DefaultPreferenceDataStore
import de.malteans.datastore.domain.PreferenceDataStore
import io.ktor.client.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single<PreferenceDataStore> { DefaultPreferenceDataStore(get()) }

    single<HttpClient> { HttpClientFactory.create(get()) }
    single<BackupRemoteSource> { DefaultBackupRemoteSource(get(), get()) }

    viewModelOf(::BackupOverviewViewModel)
    viewModel { params ->
        BackupDetailsViewModel(
            logFileName = params.get(),
            backupRemoteSource = get()
        )
    }
}