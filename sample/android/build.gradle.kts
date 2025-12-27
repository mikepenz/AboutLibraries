import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import com.mikepenz.aboutlibraries.plugin.StrictMode

plugins {
    id("com.mikepenz.convention.android-application")
    id("com.mikepenz.convention.compose")
    id("com.mikepenz.aboutlibraries.plugin")
    id("com.mikepenz.aboutlibraries.plugin.android")
    id("com.mikepenz.convention.composable-preview-scanner.paparazzi-plugin")
}

android {
    namespace = "com.mikepenz.aboutlibraries.sample"

    defaultConfig {
        applicationId = "com.mikepenz.aboutlibraries"
        base.archivesName = "AboutLibraries-v$versionName-c$versionCode"
    }
}

dependencies {
    implementation(project(":aboutlibraries-compose"))
    implementation(project(":aboutlibraries-compose-m2"))
    implementation(project(":aboutlibraries-compose-m3"))
    implementation(project(":sample:shared"))
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.material3)
    implementation(compose.components.resources)
    implementation(libs.androidx.activity.compose)
    debugImplementation(compose.uiTooling)
}

compose.resources {
    packageOfResClass = "com.mikepenz.aboutlibraries.sample.resources"
}

composablePreviewPaparazzi {
    enable = true
    packages = listOf("com.mikepenz.aboutlibraries.screenshot")
    includePrivatePreviews = false
    testClassName = "PaparazziTests"
    testPackageName = "com.mikepenz.aboutlibraries.screenshot.generated.tests"
}

aboutLibraries {
    collect {
        // define the path configuration files are located in. E.g. additional libraries, licenses to add to the target .json
        // relative to module root (`../` for parent folder)
        configPath = file("../../config")

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

    // export {
    //     prettyPrint = true
    //     outputFile = file("src/main/res/raw/aboutlibraries.json")
    // }

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
            "BSD-3-Clause" to listOf("org.hamcrest:hamcrest-core"),
            "EPL-1.0" to listOf("junit:junit"),
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