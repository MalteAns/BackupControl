import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    alias(libs.plugins.kotlin.serialization)
}

private val apiToken: String = gradleLocalProperties(rootDir, rootProject.providers)
    .getProperty("API_TOKEN")
    ?: System.getenv("API_TOKEN")
    ?: throw IllegalStateException(
        "Missing API_TOKEN property in local.properties or environment variables"
    )

val generateDesktopBuildConfig = tasks.register("generateDesktopBuildConfig") {
    description = "Generates a BuildConfig file for the desktop target with the API token."

    notCompatibleWithConfigurationCache("Custom script writes file dynamically")

    val outputDir = layout.buildDirectory.dir("generated/buildConfig/desktopMain/kotlin")
    val packagePath = "de/malteans/recipes/core/data/network"
    val outputFile = outputDir.map { it.file("$packagePath/DesktopBuildConfig.kt") }

    inputs.property("apiToken", apiToken)
    outputs.file(outputFile)

    doLast {
        val safeToken = apiToken.removeSurrounding("\"")
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            package ${libs.versions.applicationId.get()}.core.data.network

            object DesktopBuildConfig {
                const val API_TOKEN = "$safeToken"
            }
            """.trimIndent()
        )
    }
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    jvm {
        compilations["main"].defaultSourceSet {
            kotlin.srcDir(generateDesktopBuildConfig.map {
                it.outputs.files.singleFile.parentFile
            })
        }
    }

    android {
        namespace = "de.malteans.backup_control.app.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)

            // Koin
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            // Ktor Client
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            api(project(":core"))
            api(project(":app:dataStore"))

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.bundles.compose)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIconsExtended)

            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.datetime)

            // Koin
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel)

            // Ktor Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}