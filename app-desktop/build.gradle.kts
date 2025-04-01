plugins {
    id("com.mikepenz.convention.kotlin-multiplatform")
    id("com.mikepenz.convention.compose")
    id("com.mikepenz.aboutlibraries.plugin")
}

dependencies {
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {

                implementation(compose.desktop.currentOs)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.components.resources)

                implementation(project(":aboutlibraries-core"))
                implementation(project(":aboutlibraries-compose-m2"))
                implementation(project(":aboutlibraries-compose-m3"))

                // Coroutines
                implementation(baseLibs.kotlinx.coroutines.core)

                // example for parent via a parent
                // implementation("org.apache.commons:commons-csv:1.9.0")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "m3.MainKt"
    }
}

aboutLibraries {
    registerAndroidTasks = false
    prettyPrint = true
    outputPath = "src/commonMain/composeResources/files/"
}
