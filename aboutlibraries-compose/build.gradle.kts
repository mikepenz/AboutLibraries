import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.mikepenz.aboutlibraries.ui.compose"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"

        kotlinOptions {
            if (project.findProperty("composeCompilerReports") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}/compose_compiler"
                )
            }
            if (project.findProperty("composeCompilerMetrics") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.buildDir.absolutePath}/compose_compiler"
                )
            }
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    lint {
        abortOnError = false
    }
}


compose {
    kotlinCompilerPlugin.set(libs.versions.composeCompilerJb.get())
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=${libs.versions.kotlinCore.get()}")
}

kotlin {
    jvm()

    androidTarget {
        publishLibraryVariants("release")
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()

    sourceSets {
        val commonMain by getting
        val commonTest by getting

        val nonAndroidMain by creating {
            dependsOn(commonMain)
        }
        val nonAndroidTest by creating {
            dependsOn(commonTest)
        }

        listOf(
            "jvm",
            "iosX64",
            "iosArm64",
            "iosSimulatorArm64",
            "macosX64",
            "macosArm64"
        ).forEach {
            getByName(it + "Main").dependsOn(nonAndroidMain)
            getByName(it + "Test").dependsOn(nonAndroidTest)
        }
    }
}

dependencies {
    commonMainApi(project(":aboutlibraries-core"))

    commonMainImplementation(compose.runtime)
    commonMainImplementation(compose.ui)
    commonMainImplementation(compose.foundation)
    commonMainImplementation(compose.material)
    commonMainImplementation(libs.kotlinx.collections)

    debugImplementation(compose.uiTooling)
    "androidMainImplementation"(compose.preview)

    "androidMainImplementation"(libs.androidx.core.ktx)
}

configurations.configureEach {
    // We forcefully exclude AppCompat + MDC from any transitive dependencies. This is a Compose module, so there's no need for these
    // https://github.com/chrisbanes/tivi/blob/5e7586465337d326a1f1e40e0b412ecd2779bb5c/build.gradle#L72
    exclude(group = "androidx.appcompat")
    exclude(group = "com.google.android.material", module = "material")
}

tasks.dokkaHtml.configure {
    dokkaSourceSets {
        configureEach {
            noAndroidSdkLink.set(false)
        }
    }
}



if (project.hasProperty("pushall") || project.hasProperty("library_compose_only")) {
    mavenPublishing {
        publishToMavenCentral(SonatypeHost.S01)
        signAllPublications()
    }
}