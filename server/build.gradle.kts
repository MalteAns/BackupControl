plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)

    alias(libs.plugins.kotlin.serialization)
}

group = "de.malteans.backup_control"
version = libs.versions.projectVersionName.get()
application {
    mainClass = "de.malteans.backup_control.ApplicationKt"
}

dependencies {
    api(project(":core"))

    implementation(libs.logback)
    implementation(libs.bundles.ktor.server)
    implementation(libs.ktor.serialization.kotlinx.json)
    // KotlinX
    implementation(libs.kotlinx.datetime)
    // Koin (DI)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    // Http Client (for status page images)
    implementation(libs.ktor.client.cio)
    // Exposed + SQLite (DB)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time) // DateTime support
    implementation(libs.exposed.migration.core) // Migration support
    implementation(libs.exposed.migration.jdbc) // Migration support
    implementation(libs.sqlite.jdbc) // SQLite

    testImplementation(libs.kotlin.testJunit)
}