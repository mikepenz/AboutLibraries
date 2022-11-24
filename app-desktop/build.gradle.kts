import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("com.mikepenz.aboutlibraries.plugin")
    application
}

repositories {
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.foundation)
    @OptIn(ExperimentalComposeLibrary::class)
    implementation(compose.material3)
    implementation(project(":aboutlibraries-core"))
    implementation(project(":aboutlibraries-compose"))

    // Coroutines
    implementation(libs.kotlin.coroutines.core)
}

compose {
    kotlinCompilerPlugin.set(libs.versions.composeCompiler.get())
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}

aboutLibraries {
    registerAndroidTasks = false
    prettyPrint = true
}
