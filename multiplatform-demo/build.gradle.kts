import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin")
    }
}

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

apply(plugin = "com.mikepenz.aboutlibraries.plugin") // has to be applied AFTER android

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        withJava()
    }
    sourceSets {

        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                implementation("com.mikepenz:aboutlibraries-core:${extra["aboutlibraries.version"] as String}")
                implementation("com.mikepenz:aboutlibraries-compose-m2:${extra["aboutlibraries.version"] as String}")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "demo"
            packageVersion = "1.0.0"
        }
    }
}

configure<AboutLibrariesExtension> {
    registerAndroidTasks = false
}

tasks.withType(KotlinCompile::class.java) {
    dependsOn("exportLibraryDefinitions")
}

