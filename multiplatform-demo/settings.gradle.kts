pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform").version(extra["kotlin.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
    }
}

rootProject.name = "multiplatform-demo"

includeBuild("../plugin-build") {
    dependencySubstitution {
        // Set up substitution so :app uses the plugin/libs from the composite build.
        substitute(module("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin")).using(project(":plugin"))
    }
}