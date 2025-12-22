import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("com.mikepenz.convention.kotlin-multiplatform")
    id("com.mikepenz.convention.compose")
    id("com.mikepenz.aboutlibraries.plugin")
    alias(baseLibs.plugins.composeHotreload)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":aboutlibraries-compose"))
            implementation(project(":sample:shared"))
            implementation(compose.components.resources)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.mikepenz.aboutlibraries.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.mikepenz.aboutlibraries"
            packageVersion = "1.0.0"
        }
    }
}

compose.resources {
    packageOfResClass = "com.mikepenz.aboutlibraries.sample.desktop.resources"
}

aboutLibraries {
    export {
        exportVariant = "jvmMain"
        outputPath = file("src/commonMain/composeResources/files/aboutlibraries.json")
    }
    library {
        duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
    }
}
