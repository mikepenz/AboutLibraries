@file:OptIn(ExperimentalWasmDsl::class)

import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.mikepenz.aboutlibraries.ui.compose.m3"

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            if (project.findProperty("composeCompilerReports") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.layout.buildDirectory.asFile.get().absolutePath}/compose_compiler"
                )
            }
            if (project.findProperty("composeCompilerMetrics") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.layout.buildDirectory.asFile.get().absolutePath}/compose_compiler"
                )
            }
        }

        val outputDir = rootDir.resolve("aboutlibraries-core/compose_compiler_config.conf").path
        compilerOptions.freeCompilerArgs.addAll(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=${outputDir}"
        )
    }

    buildFeatures {
        compose = true
    }

    lint {
        abortOnError = false
    }
}

kotlin {
    applyDefaultHierarchyTemplate()

    jvm()

    androidTarget {
        publishLibraryVariants("release")
    }

    js(IR) {
        browser()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    wasmJs {
        nodejs()
    }

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

tasks.dokkaHtml.configure {
    dokkaSourceSets {
        configureEach {
            noAndroidSdkLink.set(false)
        }
    }
}



if (project.hasProperty("pushall") || project.hasProperty("library_compose_m3_only")) {
    mavenPublishing {
        publishToMavenCentral(SonatypeHost.S01)
        signAllPublications()
    }
}