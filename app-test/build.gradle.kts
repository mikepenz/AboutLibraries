@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.mikepenz.aboutlibraries.plugin")
}

kotlin {
    jvm()
    wasmJs {
        nodejs()
    }

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
    collect {
        fetchRemoteLicense = true
    }
    export {
        prettyPrint = true
        outputFile = file("files/aboutlibraries.json")
    }
    exports {
        create("jvm") {
            outputFile = file("files/jvm/aboutlibraries.json")
        }
        create("wasmJs") {
            outputFile = file("files/wasmJs/aboutlibraries.json")
        }
    }
}