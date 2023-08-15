import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform")
    // kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.mikepenz.aboutlibraries.core"

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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
    androidTarget {
        publishAllLibraryVariants()
    }
    // wasm()

    // tier 1
    linuxX64()
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()

    // tier 2
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()

    // tier 3
    // androidNativeArm32()
    // androidNativeArm64()
    // androidNativeX86()
    // androidNativeX64()
    mingwX64()
    watchosDeviceArm64()

    // common sets
    ios()
    tvos()
    watchos()

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
        val iosArm64Main by sourceSets.getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by sourceSets.getting { dependsOn(iosMain) }
        val iosX64Main by sourceSets.getting { dependsOn(iosMain) }
        val tvosArm64Main by sourceSets.getting { dependsOn(iosMain) }
        val tvosSimulatorArm64Main by sourceSets.getting { dependsOn(iosMain) }
        val tvosX64Main by sourceSets.getting { dependsOn(iosMain) }
        val watchosArm32Main by sourceSets.getting { dependsOn(iosMain) }
        val watchosArm64Main by sourceSets.getting { dependsOn(iosMain) }
        val watchosSimulatorArm64Main by sourceSets.getting { dependsOn(iosMain) }
        val watchosX64Main by sourceSets.getting { dependsOn(iosMain) }
        val watchosDeviceArm64Main by sourceSets.getting { dependsOn(iosMain) }
        // val wasmMain by sourceSets.getting { dependsOn(multiplatformMain) }

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
    mavenPublishing {
        publishToMavenCentral(SonatypeHost.S01)
        signAllPublications()
    }
}