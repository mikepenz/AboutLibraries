import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
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
    implementation(compose.material)
    implementation(compose.material3)
    implementation(project(":aboutlibraries-core"))
    implementation(project(":aboutlibraries-compose-m2"))
    implementation(project(":aboutlibraries-compose-m3"))

    // Coroutines
    implementation(libs.kotlin.coroutines.core)

    // example for parent via a parent
    // implementation("org.apache.commons:commons-csv:1.9.0")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "11"
    targetCompatibility = "11"
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
