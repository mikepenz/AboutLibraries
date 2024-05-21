import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    id("com.mikepenz.aboutlibraries.plugin")
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "aboutlibraries"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
            }
        }

        val wasmJsMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)

                implementation(project(":aboutlibraries-core"))
                implementation(project(":aboutlibraries-compose-m2"))
                implementation(project(":aboutlibraries-compose-m3"))
            }
        }
    }
}

compose.experimental {
    web.application {}
}