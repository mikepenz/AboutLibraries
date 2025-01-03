buildscript {
    dependencies {
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin")
    }
}

plugins {
    alias(libs.plugins.conventionPlugin)

    alias(baseLibs.plugins.androidApplication) apply false
    alias(baseLibs.plugins.androidLibrary) apply false
    alias(baseLibs.plugins.composeMultiplatform) apply false
    alias(baseLibs.plugins.composeCompiler) apply false
    alias(baseLibs.plugins.kotlinMultiplatform) apply false

    alias(baseLibs.plugins.dokka)
    alias(baseLibs.plugins.aboutlibraries) apply false
    alias(baseLibs.plugins.mavenPublish) apply false

    alias(libs.plugins.navSafeArgs) apply false
}

allprojects {
    group = ext.get("GROUP")!!
    version = ext.get("VERSION_NAME")!!
}