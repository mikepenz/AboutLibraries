plugins {
    id("com.mikepenz.convention.kotlin-multiplatform")
    id("com.mikepenz.convention.compose")
    id("com.mikepenz.convention.publishing")
    alias(baseLibs.plugins.stabilityAnalyzer)
}

composeCompiler {
    stabilityConfigurationFiles.addAll(
        rootProject.layout.projectDirectory.file("stability_config.conf"),
    )
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
}

kotlin {
    android {
        namespace = "com.mikepenz.aboutlibraries.ui.compose.m3"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":aboutlibraries-core"))
                api(project(":aboutlibraries-compose"))

                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material3)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.core.ktx)
            }
        }
    }
}

configurations.configureEach {
    // We forcefully exclude AppCompat + MDC from any transitive dependencies. This is a Compose module, so there's no need for these
    // https://github.com/chrisbanes/tivi/blob/5e7586465337d326a1f1e40e0b412ecd2779bb5c/build.gradle#L72
    exclude(group = "androidx.appcompat")
    exclude(group = "com.google.android.material", module = "material")
    exclude(group = "com.google.android.material", module = "material3")
}