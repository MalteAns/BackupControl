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
    // Koin (DI)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    // Http Client (for status page images)
    implementation(libs.ktor.client.cio)

    testImplementation(libs.kotlin.testJunit)
}