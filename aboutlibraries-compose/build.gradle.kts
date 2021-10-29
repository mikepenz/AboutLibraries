import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 24
        targetSdk = 30
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.0-beta01"
    }
}

kotlin {
    jvm()

    android {
        publishLibraryVariants("release")
    }
}

dependencies {
    commonMainImplementation(project(":aboutlibraries-core"))
    commonMainCompileOnly(compose.runtime)
    commonMainCompileOnly(compose.ui)
    commonMainCompileOnly(compose.foundation)
    commonMainCompileOnly(compose.material)

    val compose_version = "1.1.0-beta01"
    "androidMainImplementation"("androidx.compose.runtime:runtime:$compose_version")
    "androidMainImplementation"("androidx.compose.ui:ui:$compose_version")
    "androidMainImplementation"("androidx.compose.foundation:foundation-layout:$compose_version")
    "androidMainImplementation"("androidx.compose.material:material:$compose_version")
    "androidMainImplementation"("androidx.compose.material:material-icons-extended:$compose_version")
    "androidMainImplementation"("androidx.compose.foundation:foundation:$compose_version")
    "androidMainImplementation"("androidx.compose.animation:animation:$compose_version")
    "androidMainImplementation"("androidx.compose.ui:ui-tooling:$compose_version")
    "androidMainImplementation"("androidx.compose.runtime:runtime-livedata:$compose_version")
}

tasks.dokkaHtml.configure {
    dokkaSourceSets {
        configureEach {
            noAndroidSdkLink.set(false)
        }
    }
}

tasks.create<Jar>("javadocJar") {
    dependsOn("dokkaJavadoc")
    classifier = "javadoc"
    from("$buildDir/javadoc")
}

//mavenPublish {
//    releaseSigningEnabled = true
//    androidVariantToPublish = "release"
//}

//publishing {
//    repositories {
//        maven {
//            name = "installLocally"
//            setUrl("${rootProject.buildDir}/localMaven")
//        }
//    }
//}