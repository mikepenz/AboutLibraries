import java.io.FileInputStream
import java.util.*

plugins {
    alias(baseLibs.plugins.kotlinJvm) apply false
    alias(baseLibs.plugins.conventionPlugin)
    alias(baseLibs.plugins.dokka)
    alias(baseLibs.plugins.mavenPublish)
    id("com.gradle.plugin-publish") version "1.3.1" apply false
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