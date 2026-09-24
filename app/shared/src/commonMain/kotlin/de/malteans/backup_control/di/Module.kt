package de.malteans.backup_control.di

import de.malteans.datastore.data.DefaultPreferenceDataStore
import de.malteans.datastore.domain.PreferenceDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single<PreferenceDataStore> { DefaultPreferenceDataStore(get()) }
}