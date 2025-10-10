import com.vanniktech.maven.publish.GradlePublishPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("com.android.lint")
    kotlin("jvm")
    id("com.gradle.plugin-publish")
    id("java-gradle-plugin")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}


group = "com.mikepenz.aboutlibraries.plugin"
version = rootProject.properties["version"]!!

gradlePlugin {
    website = "https://github.com/mikepenz/AboutLibraries"
    vcsUrl = "https://github.com/mikepenz/AboutLibraries"

    plugins {
        create("aboutlibsPlugin") {
            id = "$group"
            implementationClass = "$group.AboutLibrariesPlugin"
            description = "Resolve all dependencies used in a gradle module, with associated license and further information."
            displayName = "AboutLibraries Library Gradle Plugin"
            tags = listOf("libraries", "licenses")
        }
        create("aboutlibsAndroidPlugin") {
            id = "$group.android"
            implementationClass = "$group.AboutLibrariesPluginAndroid"
            description = "Resolve all dependencies used in a gradle module, and automatically generates the definition file as part of the Android build."
            displayName = "AboutLibraries Library Android Gradle Plugin"
            tags = listOf("libraries", "licenses", "android")
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(11)
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_1_8)
        languageVersion.set(KotlinVersion.KOTLIN_1_8)
    }
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(baseLibs.kotlin.gradlePlugin)

    implementation(localGroovy())

    // parser the pom.xml files
    implementation(libs.ivy.core)
    implementation(libs.maven.modelBuilder)

    // add better android support
    compileOnly(baseLibs.android.gradlePlugin)

    // lint rules
    lintChecks(baseLibs.android.lint.gradle)
}

mavenPublishing {
    configure(GradlePublishPlugin())
}

dokka {
    dokkaSourceSets {
    }
}