plugins {
    kotlin("multiplatform")
    id("com.mikepenz.aboutlibraries.plugin")
}

kotlin {
    jvm()

    sourceSets {
        jvmMain {
            dependencies {
                // https://mvnrepository.com/artifact/javax.annotation/javax.annotation-api
                implementation("javax.annotation:javax.annotation-api:1.3.2")
            }
        }
    }
}

aboutLibraries {
    fetchRemoteLicense = true
}