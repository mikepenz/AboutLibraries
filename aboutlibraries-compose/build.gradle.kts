plugins {
    kotlin("android")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

val viewModel = "2.4.0"
val composeVersion = "1.1.0-beta02"

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 30
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }

    lint {
        isAbortOnError = false
    }
}

dependencies {
    implementation(project(":aboutlibraries-core"))

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$viewModel")

    implementation("androidx.compose.runtime:runtime:$composeVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.foundation:foundation-layout:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
}

tasks.dokkaHtml.configure {
    dokkaSourceSets {
        configureEach {
            noAndroidSdkLink.set(false)
        }
    }
}

if (project.hasProperty("pushall") || project.hasProperty("library_compose_only")) {
    apply(from = "$rootDir/gradle/gradle-mvn-push.gradle")
}