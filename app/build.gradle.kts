import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import com.mikepenz.aboutlibraries.plugin.StrictMode

plugins {
    id("com.mikepenz.convention.android-application")
    id("com.mikepenz.convention.compose")
    id("com.mikepenz.aboutlibraries.plugin")
    id("com.mikepenz.aboutlibraries.plugin.android")
    alias(baseLibs.plugins.stabilityAnalyzer)
}

android {
    namespace = "com.mikepenz.aboutlibraries.sample.legacy"

    defaultConfig {
        base.archivesName = "AboutLibraries-v$versionName-c$versionCode"
    }

    buildTypes {
        create("staging") {
            signingConfig = signingConfigs.findByName("release")
            applicationIdSuffix = ".debugStaging"
            matchingFallbacks.add("debug")
        }
    }

    packaging {
        resources {
            excludes.add("META-INF/library-core_release.kotlin_module")
            excludes.add("META-INF/library_release.kotlin_module")
        }
    }
}

composeCompiler {
    stabilityConfigurationFiles.addAll(
        rootProject.layout.projectDirectory.file("stability_config.conf"),
    )
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
}

dependencies {
    // implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.material3)
    implementation(compose.components.resources)
    implementation(compose.components.uiToolingPreview)

    implementation(project(":aboutlibraries-core"))
    implementation(project(":aboutlibraries-compose-m2"))
    implementation(project(":aboutlibraries-compose-m3"))

    // Coroutines
    implementation(baseLibs.kotlinx.coroutines.core)

    // example for parent via a parent
    // implementation("org.apache.commons:commons-csv:1.9.0")

    implementation(libs.androidx.activity.compose)
    implementation(libs.google.material)

    // used only tho showcase multi flavor support
    "stagingImplementation"(libs.okhttp.core)

    // https://mvnrepository.com/artifact/androidx.compose.material/material-icons-core
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // used to test matching of license names
//    implementation("com.github.librepdf:openpdf:1.3.43")
//    implementation("com.google.android.play:app-update-ktx:2.1.0")
}


aboutLibraries {
    collect {
        // define the path configuration files are located in. E.g. additional libraries, licenses to add to the target .json
        // relative to module root (`../` for parent folder)
        configPath = file("../config")

        // (optional) GitHub token to raise API request limit to allow fetching more licenses
        gitHubApiToken = if (hasProperty("github.pat")) property("github.pat")?.toString() else null

        // Set to offline mode, skipping remote requests to fetch licenses or funding information
        // offlineMode = true

        // enable fetching of "remote" licenses. Uses the GitHub API
        fetchRemoteLicense = false

        // Allows to only collect dependencies of specific variants during the `collectDependencies` step.
        // filterVariants.addAll("debug")

        // Allows to enable the collection of funding information of differnet libraries
        // fetchRemoteFunding = true
    }

    license {
        // Define the strict mode, will fail if the project uses licenses not allowed
        // - This will only automatically fail for Android projects which have `registerAndroidTasks` enabled
        // For non-Android projects, execute `exportLibraryDefinitions`
        strictMode = StrictMode.FAIL
        // Allowed set of licenses, this project will be able to use without build failure
        allowedLicenses.addAll("Apache-2.0", "MIT")
        // Allowed set of licenses for specific dependencies, this project will be able to use without build failure
        allowedLicensesMap = mapOf(
            "asdkl" to listOf("androidx.jetpack.library"),
            "NOASSERTION" to listOf("org.jetbrains.kotlinx"),
        )

        // Full license text for license IDs mentioned here will be included, even if no detected dependency uses them.
        // additionalLicenses.addAll("mit", "mpl_2_0")
    }

    library {
        // Enable the duplication mode, allows to merge, or link dependencies which relate
        duplicationMode = DuplicateMode.MERGE
        // Configure the duplication rule, to match "duplicates" with
        duplicationRule = DuplicateRule.EXACT
    }
}
