plugins {
    id("com.mikepenz.convention.android-library")
    id("com.mikepenz.convention.kotlin-multiplatform")
    id("com.mikepenz.convention.compose")
    id("com.mikepenz.convention.publishing")
}

android {
    namespace = "com.mikepenz.aboutlibraries.ui.compose.m3"

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            val outputDir = rootDir.resolve("aboutlibraries-core/compose_compiler_config.conf").path
            freeCompilerArgs.addAll("-P", "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=${outputDir}")
        }
    }
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material3)
            }
        }
        val commonTest by getting
    }
}

dependencies {
    commonMainApi(project(":aboutlibraries-core"))

    debugImplementation(compose.uiTooling)
    "androidMainImplementation"(compose.preview)
    "androidMainImplementation"(libs.androidx.core.ktx)
}

configurations.configureEach {
    // We forcefully exclude AppCompat + MDC from any transitive dependencies. This is a Compose module, so there's no need for these
    // https://github.com/chrisbanes/tivi/blob/5e7586465337d326a1f1e40e0b412ecd2779bb5c/build.gradle#L72
    exclude(group = "androidx.appcompat")
    exclude(group = "com.google.android.material", module = "material")
    exclude(group = "com.google.android.material", module = "material3")
}