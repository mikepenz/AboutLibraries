import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import com.mikepenz.aboutlibraries.plugin.StrictMode

plugins {
    id("com.mikepenz.convention.kotlin")
    id("com.mikepenz.convention.android-application")
    id("com.mikepenz.convention.compose")
    id("com.mikepenz.aboutlibraries.plugin")
}

android {
    namespace = "com.mikepenz.aboutlibraries.sample"

    defaultConfig {
        setProperty("archivesBaseName", "AboutLibraries-v$versionName-c$versionCode")
    }

    buildTypes {
        create("staging") {
            signingConfig = signingConfigs.findByName("release")
            applicationIdSuffix = ".debugStaging"
            matchingFallbacks.add("debug")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes.add("META-INF/library-core_release.kotlin_module")
            excludes.add("META-INF/library_release.kotlin_module")
        }
    }
}

// It is possible to define a custom config path with custom mappings
aboutLibraries {
    // allow to enable "offline mode", will disable any network check of the plugin (including [fetchRemoteLicense] or pulling spdx license texts)
    offlineMode = false

    android {
        // - if the automatic registered android tasks are disabled, a similar thing can be achieved manually
        // - `./gradlew app:exportLibraryDefinitions -PaboutLibraries.exportPath=src/main/res/raw`
        // - the resulting file can for example be added as part of the SCM
        // registerAndroidTasks = false
    }

    collect {
        // define the path configuration files are located in. E.g. additional libraries, licenses to add to the target .json
        // relative to module root (`../` for parent folder)
        configPath = file("../config")

        // (optional) GitHub token to raise API request limit to allow fetching more licenses
        gitHubApiToken = if (hasProperty("github.pat")) property("github.pat")?.toString() else null

        // enable fetching of "remote" licenses. Uses the GitHub API
        fetchRemoteLicense = true

        // Allows to only collect dependencies of specific variants during the `collectDependencies` step.
        // filterVariants.addAll("debug")

        // Allows to enable the collection of funding information of differnet libraries
        // fetchRemoteFunding = true
    }

    export {
        // Optional configuration to output the result in an alternative location
        // outputPath = file("$buildDir/generated/aboutlibraries/aboutlibraries.json")

        // Allows excluding some fields from the generated meta data field.
        // excludeFields.addAll("developers", "funding")

        exportVariant = "release"

        // Enable pretty printing for the generated JSON file
        prettyPrint = true
    }

    license {
        // Define the strict mode, will fail if the project uses licenses not allowed
        // - This will only automatically fail for Android projects which have `registerAndroidTasks` enabled
        // For non-Android projects, execute `exportLibraryDefinitions`
        strictMode = StrictMode.FAIL
        // Allowed set of licenses, this project will be able to use without build failure
        allowedLicenses.addAll("Apache-2.0")
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
        duplicationMode = DuplicateMode.LINK
        // Configure the duplication rule, to match "duplicates" with
        duplicationRule = DuplicateRule.SIMPLE
    }
}

dependencies {
    implementation(project(":aboutlibraries-core"))
    implementation(project(":aboutlibraries"))
    implementation(project(":aboutlibraries-compose-m2"))
    implementation(project(":aboutlibraries-compose-m3"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.cardView)
    implementation(libs.androidx.recyclerView)

    implementation(libs.google.material)

    implementation(compose.desktop.currentOs)
    implementation(compose.ui)
    implementation(compose.uiTooling)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.material3)
    implementation(compose.components.resources)
    implementation(compose.materialIconsExtended)

    //used to generate the drawer on the left
    //https://github.com/mikepenz/MaterialDrawer
    implementation(libs.materialDrawer.core)

    //used to provide different itemAnimators for the RecyclerView
    //https://github.com/mikepenz/ItemAnimators
    implementation(libs.itemAnimators.core)

    // used to provide out of the box icon font support. simplifies development,
    // and provides scalable icons. the core is very very light
    // https://github.com/mikepenz/Android-Iconics
    implementation(libs.iconics.core)

    //used to display the icons in the drawer
    //https://github.com/mikepenz/Android-Iconicsx`
    implementation("com.mikepenz:material-design-iconic-typeface:2.2.0.8-kotlin@aar")

    // used only tho showcase multi flavor support
    "stagingImplementation"(libs.okhttp.core)

    // used to test matching of license names
    implementation("com.github.librepdf:openpdf:1.3.43")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
}

configurations.configureEach {
    resolutionStrategy.force(libs.fastAdapter.core)
    resolutionStrategy.force(libs.iconics.core)
}