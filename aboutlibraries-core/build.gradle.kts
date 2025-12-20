import com.mikepenz.gradle.utils.readPropertyOrElse

plugins {
    id("com.mikepenz.convention.kotlin-multiplatform")
    alias(baseLibs.plugins.androidKmpLibrary)
    id("com.mikepenz.convention.publishing")
    kotlin("plugin.serialization") version baseLibs.versions.kotlin.get()
}

kotlin {
    android {
        namespace = "com.mikepenz.aboutlibraries.core"
        compileSdk = baseLibs.versions.compileSdk.get().toInt()
        minSdk = project.readPropertyOrElse("com.mikepenz.android.minSdk", baseLibs.versions.minSdk.get(), null)?.toInt()
        lint {
            abortOnError = false
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization)
            }
        }
        val multiplatformMain by creating {
            dependsOn(commonMain)
        }
        val jvmMain by getting {
            dependsOn(multiplatformMain)
        }
        val nativeMain by getting {
            dependsOn(multiplatformMain)
        }
        val jsMain by getting {
            dependsOn(multiplatformMain)
        }
        val androidMain by getting
        val wasmJsMain by getting {
            dependsOn(multiplatformMain)
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }
}