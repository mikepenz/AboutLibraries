import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
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
    wasm {
        moduleName = "aboutlibraries"
        browser {
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).copy(
                    static = (devServer?.static ?: mutableListOf()).apply {
                        add(project.rootDir.path)
                        add(project.rootDir.path + "/shared/")
                        add(project.rootDir.path + "/nonAndroidMain/")
                        add(project.rootDir.path + "/webApp/")
                    },
                )
            }
        }
        binaries.executable()
    }

    sourceSets {
        val wasmJsMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
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