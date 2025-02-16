plugins {
    id("com.mikepenz.convention.kotlin")
    id("com.mikepenz.convention.android-application")
    id("com.mikepenz.convention.compose")
    id("com.mikepenz.aboutlibraries.plugin")
}

android {
    namespace = "com.mikepenz.aboutlibraries.sample.test"
    
    flavorDimensions.add("default")

    productFlavors {
        create("basic") {
            dimension = "default"
        }
        create("freenet") {
            dimension = "default"
            versionNameSuffix = "_freenet"
        }
    }

}

dependencies {
    implementation(testLibs.core.ktx)
    implementation(testLibs.appcompat)
    implementation(testLibs.material)
    implementation(testLibs.core.splashscreen)

    implementation(testLibs.cardview)
    implementation(testLibs.swiperefreshlayout)

    implementation(platform(testLibs.compose.bom))
    implementation(testLibs.activity.compose)
    implementation(testLibs.compose.material.ripple)
    implementation(testLibs.compose.animation)
    implementation(testLibs.compose.ui.tooling)
    implementation(testLibs.compose.ui.util)
    implementation(testLibs.compose.foundation)
    implementation(testLibs.compose.material3)
    implementation(testLibs.compose.material.icons)
    implementation(testLibs.navigation.compose)
    lintChecks(testLibs.compose.lint.checks)

    implementation(testLibs.accompanist.permissions)

    testImplementation(testLibs.bundles.test)

    // preference.
    implementation(testLibs.preference.ktx)

    // db
    implementation(testLibs.bundles.sqlite)

    // work.
    implementation(testLibs.work.runtime)

    // lifecycle.
    implementation(testLibs.bundles.lifecycle)
    implementation(testLibs.recyclerview)


    // HTTP
    implementation(testLibs.bundles.retrofit)
    implementation(testLibs.bundles.okhttp)
    implementation(testLibs.kotlinx.serialization.json)
    // Not used in free sources
    "basicImplementation"(testLibs.kotlinx.serialization.xml.core)
    "basicImplementation"(testLibs.kotlinx.serialization.xml)

    // data store
    // implementation(libs.datastore)

    // jwt - Only used by MF at the moment
    "basicImplementation"(testLibs.jjwt.api)
    "basicRuntimeOnly"(testLibs.jjwt.impl)
    "basicRuntimeOnly"(testLibs.jjwt.orgjson) {
        exclude("org.json", "json") // provided by Android natively
    }

    // rx java.
    implementation(testLibs.rxjava)
    implementation(testLibs.rxandroid)
    implementation(testLibs.kotlinx.coroutines.rx3)

    // ui.
    implementation(testLibs.vico.compose.m3)
    implementation(testLibs.adaptiveiconview)
    implementation(testLibs.activity)
    implementation(testLibs.expandabletextcompose)

    // utils.
    implementation(testLibs.suncalc)
    implementation(testLibs.aboutLibraries)

    // Allows reflection of the relative time class to pass Locale as parameter
    implementation(testLibs.restrictionBypass)

    // debugImplementation because LeakCanary should only run in debug builds.
    // debugImplementation(testLibs.leakcanary)
}

aboutLibraries {
    prettyPrint = true

    // Define the path configuration files are located in. E.g. additional libraries, licenses to add to the
    // target .json
    // Warning: Please do not use the parent folder of a module as path, as this can result in issues.
    // More details: https://github.com/mikepenz/AboutLibraries/issues/936
    configPath = "config"

    // Remove the "generated" timestamp to allow for reproducible builds
    excludeFields = arrayOf("generated")
}