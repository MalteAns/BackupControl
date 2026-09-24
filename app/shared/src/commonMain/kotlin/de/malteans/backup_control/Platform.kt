package de.malteans.backup_control

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform