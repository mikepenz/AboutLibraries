buildscript {
    dependencies {
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin")
    }
}

plugins {
    alias(baseLibs.plugins.conventionPlugin)
    alias(baseLibs.plugins.androidApplication) apply false
    alias(baseLibs.plugins.androidLibrary) apply false
    alias(baseLibs.plugins.androidKmpLibrary) apply false
    alias(baseLibs.plugins.composeMultiplatform) apply false
    alias(baseLibs.plugins.composeCompiler) apply false
    alias(baseLibs.plugins.composeHotreload) apply false
    alias(baseLibs.plugins.kotlinMultiplatform) apply false

    alias(baseLibs.plugins.dokka)
    alias(baseLibs.plugins.aboutLibraries) apply false
    alias(baseLibs.plugins.mavenPublish) apply false
    alias(baseLibs.plugins.binaryCompatiblityValidator) apply false
    alias(baseLibs.plugins.versionCatalogUpdate) apply false
    alias(baseLibs.plugins.stabilityAnalyzer) apply false
    alias(baseLibs.plugins.paparazzi) apply false

    alias(libs.plugins.navSafeArgs) apply false
}