import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    // kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.dokka")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
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
    }

    lint {
        abortOnError = false
    }
}

kotlin {
    jvm()

    js(IR) {
        nodejs {}
        browser {}
        compilations.all {
            kotlinOptions {
                moduleKind = "umd"
                sourceMap = true
                sourceMapEmbedSources = null
            }
        }
    }

    ios()
    iosX64()
    iosArm32()
    iosArm64()
    iosSimulatorArm64()
    tvos()
    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()
    watchos()
    watchosX86()
    watchosX64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    macosX64()
    macosArm64()
    mingwX64()
    linuxX64()
    linuxArm64()
    // wasm32()

    android {
        publishAllLibraryVariants()
    }

    /*
    cocoapods {
        summary = "AboutLibraries automatically detects all dependencies of a project and collects their information including the license."
        homepage = "https://github.com/mikepenz/AboutLibraries"
        authors = "Mike Penz"
        license = "Apache 2.0"
        framework {
            baseName = "AboutLibrariesFramework"
            isStatic = false // Dynamic framework support
        }
    }
     */

    sourceSets {
        val commonMain by sourceSets.getting
        val multiplatformMain by sourceSets.creating { dependsOn(commonMain) }
        val jvmMain by sourceSets.getting { dependsOn(multiplatformMain) }
        val jsMain by sourceSets.getting { dependsOn(multiplatformMain) }
        val desktopMain by sourceSets.creating { dependsOn(multiplatformMain) }
        val linuxX64Main by sourceSets.getting { dependsOn(desktopMain) }
        val mingwX64Main by sourceSets.getting { dependsOn(desktopMain) }
        val macosX64Main by sourceSets.getting { dependsOn(desktopMain) }
        val macosArm64Main by sourceSets.getting { dependsOn(desktopMain) }
        val linuxArm64Main by sourceSets.getting { dependsOn(desktopMain) }
        val iosMain by sourceSets.getting { dependsOn(multiplatformMain) }
        val iosArm32Main by sourceSets.getting { dependsOn(iosMain) }
        val iosArm64Main by sourceSets.getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by sourceSets.getting { dependsOn(iosMain) }
        val iosX64Main by sourceSets.getting { dependsOn(iosMain) }
        val tvosArm64Main by sourceSets.getting { dependsOn(iosMain) }
        val tvosSimulatorArm64Main by sourceSets.getting { dependsOn(iosMain) }
        val tvosX64Main by sourceSets.getting { dependsOn(iosMain) }
        val watchosArm32Main by sourceSets.getting { dependsOn(iosMain) }
        val watchosArm64Main by sourceSets.getting { dependsOn(iosMain) }
        val watchosSimulatorArm64Main by sourceSets.getting { dependsOn(iosMain) }
        val watchosX86Main by sourceSets.getting { dependsOn(iosMain) }
        val watchosX64Main by sourceSets.getting { dependsOn(iosMain) }
        // val wasm32Main by sourceSets.creating { dependsOn(multiplatformMain) }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }
}

dependencies {
    // kotlinx Serialize
    "multiplatformMainImplementation"(libs.kotlinx.serialization)
}

tasks.dokkaHtml.configure {
    dokkaSourceSets {
        configureEach {
            noAndroidSdkLink.set(false)
        }
    }
}

if (project.hasProperty("pushall") || project.hasProperty("library_core_only")) {
    apply(from = "$rootDir/gradle/gradle-mvn-push.gradle")
}