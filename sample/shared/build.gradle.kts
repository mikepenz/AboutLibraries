import com.mikepenz.gradle.utils.readPropertyOrElse

plugins {
    id("com.mikepenz.convention.kotlin-multiplatform")
    id("com.mikepenz.convention.compose")
    id("com.mikepenz.aboutlibraries.plugin")
}

composeCompiler {
    stabilityConfigurationFiles.addAll(
        rootProject.layout.projectDirectory.file("stability_config.conf"),
    )
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
}

kotlin {
    applyDefaultHierarchyTemplate()

    android {
        namespace = "com.mikepenz.aboutlibraries.sample.shared"
        compileSdk = baseLibs.versions.compileSdk.get().toInt()
        minSdk = project.readPropertyOrElse("com.mikepenz.android.minSdk", baseLibs.versions.minSdk.get(), null)?.toInt()
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":aboutlibraries-core"))
            implementation(project(":aboutlibraries-compose-m2"))
            implementation(project(":aboutlibraries-compose-m3"))

            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.uiUtil)
            implementation(compose.components.resources)
            implementation(compose.material)
            implementation(compose.material3)

            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
        }
    }
}

compose.resources {
    packageOfResClass = "com.mikepenz.aboutlibraries.sample.shared.resources"
}

aboutLibraries {
    export {
        outputPath = file("src/commonMain/composeResources/files/aboutlibraries.json")
    }
    library {
        duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
    }
}
