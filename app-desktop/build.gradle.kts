plugins {
    id("com.mikepenz.convention.kotlin-multiplatform")
    id("com.mikepenz.convention.compose")
    id("com.mikepenz.aboutlibraries.plugin")
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
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

compose.desktop {
    application {
        mainClass = "m3.MainKt"
    }
}

aboutLibraries {
    android {
        registerAndroidTasks = false
    }
    export {
        variant = "jvmMain"
        prettyPrint = true
        outputFile = file("src/commonMain/composeResources/files/aboutlibraries.json")
    }
}
