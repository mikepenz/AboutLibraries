rootProject.name = "AboutLibraries"

// enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
// enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    versionCatalogs {
        create("baseLibs") {
            from("com.mikepenz:version-catalog:0.12.0")
        }
    }
}

include(":aboutlibraries-core")
include(":aboutlibraries")
include(":aboutlibraries-compose")
include(":aboutlibraries-compose-m2")
include(":aboutlibraries-compose-m3")
include(":aboutlibraries-compose-wear-m3")

include(":sample:shared")
include(":sample:android")
include(":sample:desktop")
include(":sample:web")

include(":app")
include(":app-test")

includeBuild("plugin-build") {
    dependencySubstitution {
        // Set up substitution so :app uses the plugin/libs from the composite build.
        substitute(module("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin")).using(project(":plugin"))
    }
}
