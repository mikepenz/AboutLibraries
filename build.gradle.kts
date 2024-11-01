import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

buildscript {
    apply(from = "configurations.gradle")

    dependencies {
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin")
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.navSafeArgs) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish) apply false
}

allprojects {
    group = ext.get("GROUP")!!
    version = ext.get("VERSION_NAME")!!

    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven { setUrl("https://androidx.dev/storage/compose-compiler/repository") }
        maven { setUrl("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven { setUrl("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental") }
    }
}

tasks.withType<DokkaMultiModuleTask>().configureEach {
    dependsOn(gradle.includedBuild("plugin-build").task(":plugin:dokkaHtmlPartial"))
    addSubprojectChildTasks(":plugin-build:build:dokkaHtmlPartial")
}