package de.malteans.backup_control

import android.app.Application
import de.malteans.backup_control.di.androidAppModule
import de.malteans.backup_control.di.initKoin
import org.koin.android.ext.koin.androidContext

class BackupControlApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(androidAppModule) {
            androidContext(this@BackupControlApplication)
        }
    }
}
