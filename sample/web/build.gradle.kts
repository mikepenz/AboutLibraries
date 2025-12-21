import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("com.mikepenz.convention.kotlin-multiplatform")
    id("com.mikepenz.convention.compose")
    id("com.mikepenz.aboutlibraries.plugin")
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "aboutlibraries"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":aboutlibraries-compose"))
            implementation(project(":sample:shared"))
            implementation(compose.foundation)
            implementation(compose.components.resources)
        }
    }
}

compose.resources {
    packageOfResClass = "com.mikepenz.aboutlibraries.sample.web.resources"
}

aboutLibraries {
    export {
        exportVariant = "wasmJs"
        outputPath = file("src/commonMain/composeResources/files/aboutlibraries.json")
    }
    library {
        duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
    }
}
