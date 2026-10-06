package de.malteans.backup_control.di

import de.malteans.backup_control.backup.data.DefaultBackupRemoteSource
import de.malteans.backup_control.backup.domain.BackupRemoteSource
import de.malteans.backup_control.backup.presentation.details.BackupDetailsViewModel
import de.malteans.backup_control.backup.presentation.overview.BackupOverviewViewModel
import de.malteans.backup_control.commandExecution.data.DefaultCommandRemoteSource
import de.malteans.backup_control.commandExecution.domain.CommandRemoteSource
import de.malteans.backup_control.commandExecution.presentation.CommandExecutionViewModel
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
    single<CommandRemoteSource> { DefaultCommandRemoteSource(get(), get()) }

    viewModelOf(::BackupOverviewViewModel)
    viewModel { params ->
        BackupDetailsViewModel(
            logFileName = params.get(),
            backupRemoteSource = get()
        )
    }
    viewModelOf(::CommandExecutionViewModel)
}