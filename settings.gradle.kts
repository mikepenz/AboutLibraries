rootProject.name = "AboutLibraries"

// enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    versionCatalogs {
        create("baseLibs") {
            from("com.mikepenz:version-catalog:0.2.6")
        }
    }
}

include(":aboutlibraries-core")
include(":aboutlibraries")
include(":aboutlibraries-compose")
include(":aboutlibraries-compose-m2")
include(":aboutlibraries-compose-m3")

include(":app")
include(":app-desktop")
include(":app-wasm")
include(":app-test")

includeBuild("plugin-build") {
    dependencySubstitution {
        // Set up substitution so :app uses the plugin/libs from the composite build.
        substitute(module("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin")).using(project(":plugin"))
    }
}