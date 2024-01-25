import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.mikepenz.aboutlibraries.plugin")
}

// val copyWasmResources = tasks.create("copyWasmResourcesWorkaround", Copy::class.java) {
//     from(project(":shared").file("src/commonMain/resources"))
//     into("build/processedResources/wasm/main")
// }
//
// afterEvaluate {
//     project.tasks.getByName("wasmProcessResources").finalizedBy(copyWasmResources)
// }

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

compose {
    kotlinCompilerPlugin.set(libs.versions.composeCompilerJb.get())
    // kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=${libs.versions.kotlinCore.get()}")
}