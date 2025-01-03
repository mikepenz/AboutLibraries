include(":plugin")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
        create("baseLibs") {
            from("com.mikepenz:version-catalog:0.0.1")
        }
    }
}