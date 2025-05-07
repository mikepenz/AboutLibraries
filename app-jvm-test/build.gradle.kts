plugins {
    kotlin("jvm")
    id("com.mikepenz.aboutlibraries.plugin")
}

group = "com.mikepenz.jvm"
version = "1.0-SNAPSHOT"

dependencies {
    // https://mvnrepository.com/artifact/javax.annotation/javax.annotation-api
    implementation("javax.annotation:javax.annotation-api:1.3.2")
}

aboutLibraries {
    collect {
        fetchRemoteLicense = true
    }
    export {
        prettyPrint = true
        outputFile = file("files/aboutlibraries.json")
    }
}