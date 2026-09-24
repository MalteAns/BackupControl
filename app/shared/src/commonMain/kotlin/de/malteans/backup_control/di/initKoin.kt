package de.malteans.backup_control.di

import de.malteans.datastore.di.dataStorePlatformModule
import de.malteans.datastore.di.dataStoreSharedModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(vararg modules: Module, config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            sharedModule, platformModule,
            dataStoreSharedModule, dataStorePlatformModule,
            *modules
        )
    }
}
