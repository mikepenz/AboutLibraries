plugins {
    kotlin("multiplatform")
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
}
apply(plugin = "com.mikepenz.aboutlibraries.plugin") // has to be applied AFTER android

kotlin {
    androidTarget()
    jvm()
}

android {
    namespace = "app.app"
    compileSdk = 34

    defaultConfig {
        versionCode = 1
        versionName = "1.0.0"
        minSdk = 29
        targetSdk = 34
    }
}