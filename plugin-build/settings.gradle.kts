include(":plugin")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        mavenLocal()
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
        create("baseLibs") {
            from("com.mikepenz:version-catalog:0.0.4")
        }
    }
}