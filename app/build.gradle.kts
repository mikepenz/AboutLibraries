import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import com.mikepenz.aboutlibraries.plugin.StrictMode
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    id("com.mikepenz.convention.kotlin-multiplatform")
    id("com.mikepenz.convention.android-application")
    id("com.mikepenz.convention.compose")
    id("com.mikepenz.aboutlibraries.plugin")
    id("com.mikepenz.aboutlibraries.plugin.android")
}

android {
    namespace = "com.mikepenz.aboutlibraries.sample"

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

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
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
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            // implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

dependencies {
    implementation(project(":aboutlibraries"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.cardView)
    implementation(libs.androidx.recyclerView)

    implementation(libs.google.material)

    // used to generate the drawer on the left
    // https://github.com/mikepenz/MaterialDrawer
    implementation(libs.materialDrawer.core)

    // used to provide different itemAnimators for the RecyclerView
    // https://github.com/mikepenz/ItemAnimators
    implementation(libs.itemAnimators.core)

    // used to provide out of the box icon font support. simplifies development,
    // and provides scalable icons. the core is very very light
    // https://github.com/mikepenz/Android-Iconics
    implementation(libs.iconics.core)

    // used to display the icons in the drawer
    // https://github.com/mikepenz/Android-Iconicsx`
    implementation("com.mikepenz:material-design-iconic-typeface:2.2.0.8-kotlin@aar")

    // used only tho showcase multi flavor support
    "stagingImplementation"(libs.okhttp.core)

    // used to test matching of license names
//    implementation("com.github.librepdf:openpdf:1.3.43")
//    implementation("com.google.android.play:app-update-ktx:2.1.0")
}

configurations.configureEach {
    resolutionStrategy.force(libs.fastAdapter.core)
    resolutionStrategy.force(libs.iconics.core)
}

compose.desktop {
    application {
        mainClass = "com.mikepenz.aboutlibraries.sample.m3.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.mikepenz.aboutlibraries.sample"
            packageVersion = "1.0.0"
        }
    }
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

    export {
        prettyPrint = true
        outputFile = file("src/androidMain/composeResources/files/aboutlibraries.json")
    }

    exports {
        create("desktop") {
            prettyPrint = true
            outputFile = file("src/desktopMain/composeResources/files/aboutlibraries.json")
        }

        create("wasmJs") {
            prettyPrint = true
            outputFile = file("src/wasmJsMain/composeResources/files/aboutlibraries.json")
        }
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
