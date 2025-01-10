import java.io.FileInputStream
import java.util.*

plugins {
    // force Kotlin 1.9.24 to support Gradle builds < 8.12.
    // Gradle only introduces Kotlin 2 support
    alias(baseLibs.plugins.kotlinJvm) version "1.9.24" apply false
    alias(libs.plugins.conventionPlugin)
    alias(baseLibs.plugins.dokka)
    alias(baseLibs.plugins.mavenPublish)
    id("com.gradle.plugin-publish") version "1.3.0" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        classpath(baseLibs.kotlin.gradlePlugin.get())
        classpath(baseLibs.android.lint.gradlePlugin.get())
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    val props = Properties()
    val propFile = project.layout.files("../gradle.properties").singleFile
    if (propFile.exists()) {
        props.load(FileInputStream(propFile))
        setProperty("version", props["VERSION_NAME"])
    } else {
        throw IllegalStateException("The parent project's gradle.properties file is missing")
    }
}