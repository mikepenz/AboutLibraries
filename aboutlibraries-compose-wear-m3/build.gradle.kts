plugins {
    id("com.mikepenz.convention.android-library")
    id("com.mikepenz.convention.compose")
    id("com.mikepenz.convention.publishing")
    alias(baseLibs.plugins.stabilityAnalyzer)
}

android {
    namespace = "com.mikepenz.aboutlibraries.ui.compose.wear.m3"
}

composeCompiler {
    stabilityConfigurationFiles.addAll(
        rootProject.layout.projectDirectory.file("stability_config.conf"),
    )
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
}

dependencies {
    api(project(":aboutlibraries-core"))
    api(project(":aboutlibraries-compose"))

    implementation(baseLibs.androidx.compose.runtime)
    implementation(baseLibs.androidx.compose.ui)
    implementation(baseLibs.androidx.compose.wear.foundation)
    implementation(baseLibs.androidx.compose.wear.material3)

    implementation(compose.preview)
    debugImplementation(compose.uiTooling)
    debugImplementation(baseLibs.androidx.compose.wear.ui.tooling)

    implementation(libs.androidx.core.ktx)
}

configurations.configureEach {
    // We forcefully exclude AppCompat + MDC from any transitive dependencies. This is a Compose module, so there's no need for these
    // https://github.com/chrisbanes/tivi/blob/5e7586465337d326a1f1e40e0b412ecd2779bb5c/build.gradle#L72
    exclude(group = "androidx.appcompat")
    exclude(group = "com.google.android.material", module = "material")
    exclude(group = "com.google.android.material", module = "material3")
}