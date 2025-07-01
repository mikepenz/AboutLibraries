# AboutLibraries

<!-- Badges -->
[![Maven Central](https://img.shields.io/maven-central/v/com.mikepenz/aboutlibraries-core?style=for-the-badge)](https://search.maven.org/artifact/com.mikepenz/aboutlibraries-core)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/com.mikepenz.aboutlibraries.plugin?label=Gradle%20Plugin&style=for-the-badge)](https://plugins.gradle.org/plugin/com.mikepenz.aboutlibraries.plugin)
[![Apache 2.0 License](https://img.shields.io/github/license/mikepenz/AboutLibraries?style=for-the-badge)](https://github.com/mikepenz/AboutLibraries/blob/develop/LICENSE)

This library collects dependency details, including licenses at compile time, and offers simple APIs to visualize these in the app.
*No runtime overhead.* Strong caching. Supports any Gradle dependency.

-------

<p align="center">
    <a href="#whats-included-">What's Included 🚀</a> &bull;
    <a href="#setup">Setup Guide 🛠️</a> &bull;
    <a href="#gradle-api">Gradle Tasks ⚙️</a> &bull;
    <a href="MIGRATION.md">Migration Guide 🧬</a> &bull;
</p>

-------

### What's included 🚀

- Kotlin Multiplatform support (including wasm)
- Lightweight multiplatform core module
    - Access all generated information
    - Build custom UIs
- Compose UI module
- Gradle Plugin
    - Generating dependency / license metadata
    - Different exports, compliance report
    - Identify possible project funding
    - License *strict mode*
- Simple and fast integration

# Screenshots

![AboutLibraries Screenshots](https://raw.githubusercontent.com/mikepenz/AboutLibraries/develop/DEV/screenshots/Screenshots.png)

# Setup

## Latest releases 🛠

- Compose 1.8.x | Refined Compose UI Design | [v12.2.4](https://github.com/mikepenz/AboutLibraries/tree/12.2.4)
- Compose UI updates | Gradle Plugin refresh | [v12.0.1](https://github.com/mikepenz/AboutLibraries/tree/12.0.1)

## Gradle Plugin

> The Gradle plugin is hosted via the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/com.mikepenz.aboutlibraries.plugin).
> Using the `plugins` DSL is the recommended approach.

<details open><summary><b>Using the plugins DSL (Recommended)</b></summary>
<p>

```gradle
// Root build.gradle.kts
id("com.mikepenz.aboutlibraries.plugin") version "${latestAboutLibsRelease}" apply false

// App build.gradle.kts
id("com.mikepenz.aboutlibraries.plugin")
```

</p>
</details>

<details><summary><b>Using the plugins DSL (Apply to all subprojects)</b></summary>
<p>

```gradle
// Root build.gradle
id("com.mikepenz.aboutlibraries.plugin") version "${latestAboutLibsRelease}"
```

</p>
</details>

<details><summary><b>Using legacy plugin application</b></summary>
<p>

```gradle
// Root build.gradle
classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:${latestAboutLibsRelease}")

// App build.gradle
apply plugin: 'com.mikepenz.aboutlibraries.plugin'
```

</p>
</details>

<details><summary><b>Gradle Plugin Configuration Options</b></summary>
<p>

## Gradle Plugin Configuration

The plugin allows customization via the `aboutLibraries` extension in your build script.

```kts
aboutLibraries {
    // Allow to enable "offline mode", will disable any network check of the plugin (including [fetchRemoteLicense] or pulling spdx license texts)
    offlineMode = false

    android {
        // - If the automatic registered android tasks are disabled, a similar thing can be achieved manually
        // - `./gradlew app:exportLibraryDefinitions -PaboutLibraries.outputFile=src/main/res/raw/aboutlibraries.json`
        // - the resulting file can for example be added as part of the SCM
        // - the `outputFile` can also be changed by setting `outputFile` via the DSL.
        registerAndroidTasks = false
    }

    collect {
        // Define the path configuration files are located in. E.g. additional libraries, licenses to add to the target .json
        // Warning: Please do not use the parent folder of a module as path, as this can result in issues. More details: https://github.com/mikepenz/AboutLibraries/issues/936
        // The path provided is relative to the modules path (not project root)
        configPath = file("../config")

        // (optional) GitHub token to raise API request limit to allow fetching more licenses
        gitHubApiToken = if (hasProperty("github.pat")) property("github.pat")?.toString() else null

        // Enable fetching of "remote" licenses.  Uses the API of supported source hosts
        // See https://github.com/mikepenz/AboutLibraries#special-repository-support
        // A `gitHubApiToken` is required for this to work as it fetches information from GitHub's API.
        fetchRemoteLicense = false

        // Enables fetching of "remote" funding information. Uses the API of supported source hosts
        // See https://github.com/mikepenz/AboutLibraries#special-repository-support
        // A `gitHubApiToken` is required for this to work as it fetches information from GitHub's API.
        fetchRemoteFunding = false

        // Allows to only collect dependencies of specific variants during the `collectDependencies` step.
        // filterVariants.addAll("debug", "release")

        // Enable inclusion of `platform` dependencies in the library report
        includePlatform = true
    }

    export {
        // Define the output path (including fileName). Modifying this will disable the automatic meta data discovery for supported platforms.
        outputFile = file("src/commonMain/composeResources/files/aboutlibraries.json")

        // The default export variant to use for this module.
        // variant = "release"

        // Allows to exclude some fields from the generated meta data field.
        // If the class name is specified, the field is only excluded for that class; without a class name, the exclusion is global.
        excludeFields.addAll("License.name", "developers", "funding")

        // Enable pretty printing for the generated JSON file
        prettyPrint = true
    }

    exports {
        // Define export configuration per variant.
        create("jvm") {
            outputFile = file("files/jvm/aboutlibraries.json")
        }
        create("wasmJs") {
            outputFile = file("files/wasmJs/aboutlibraries.json")
        }
    }

    license {
        // Define the strict mode, will fail if the project uses licenses not allowed
        // - This will only automatically fail for Android projects which have `registerAndroidTasks` enabled
        // For non Android projects, execute `exportLibraryDefinitions`
        strictMode = com.mikepenz.aboutlibraries.plugin.StrictMode.FAIL

        // Allowed set of licenses, this project will be able to use without build failure
        allowedLicenses.addAll("Apache-2.0", "asdkl")

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
        duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.LINK
        // Configure the duplication rule, to match "duplicates" with
        duplicationRule = com.mikepenz.aboutlibraries.plugin.DuplicateRule.SIMPLE
    }
}
```

Full documentation of all available gradle plugin
configurations can be found in
the [AboutLibrariesExtension.kt](https://github.com/mikepenz/AboutLibraries/blob/develop/plugin-build/plugin/src/main/kotlin/com/mikepenz/aboutlibraries/plugin/AboutLibrariesExtension.kt)
source file.

</p>
</details>

<details><summary><b>Modify Libraries / Licenses</b></summary>
<p>

## Modify libraries / licenses

The plugin offers the ability to add or override library and license details by placing JSON files
in the `libraries` and `licenses` directories within the configured `configPath`.
See the [config directory](https://github.com/mikepenz/AboutLibraries/blob/develop/config/) for examples.

## Provide funding information

The plugin offers the ability to add or override funding details by placing a `funding.json` JSON file in the `funding` directory within the configured `configPath`.
Alternatively the `exportFunding` task can be used to generate this data automatically (ensure to provide a GitHub API Key).
The task will automatically write the file in the respective folder. Note: It will overwrite the file if it already exists.

```bash
./gradlew app:exportFunding
```

### Libraries

Provide additional or modify existing libraries via a `.json` file per library.
If the `uniqueId` overlaps, a merge will occur.

```json
{
  "uniqueId": "com.mikepenz:materialdrawer",
  "developers": [
    {
      "name": "Mike Penz",
      "organisationUrl": "https://mikepenz.dev"
    }
  ],
  "description": "(Merged) The flexible, easy to use, all in one drawer library for your Android project.",
  "name": "ABC MaterialDrawer Library",
  "website": "https://github.com/mikepenz/MaterialDrawer"
}
```

### Licenses

Provide additional or modify existing licenses via a `.json` file per license.

```json
{
  "content": "This is the Android Software Development Kit License Agreement\n<br />\n1. Introduction\n<br />\n1.1 The Android Software Development Kit (referred to in the License Agreement as the \"SDK\" and specifically including the Android system files, packaged APIs, and Google APIs add-ons) is licensed to you subject to the terms of the License Agreement. The License Agreement forms a legally binding contract between you and Google in relation to your use of the SDK.\n<br />\n1.2 \"Android\" means the Android software stack for devices, as made available under the Android Open Source Project, which is located at the following URL: http://source.android.com/, as updated from time to time.\n<br />\n1.3 A \"compatible implementation\" means any Android device that (i) complies with the Android Compatibility Definition document, which can be found at the Android compatibility website (http://source.android.com/compatibility) and which may be updated from time to time; and (ii) successfully passes the Android Compatibility Test Suite (CTS).\n<br />\n1.4 \"Google\" means Google LLC, a Delaware corporation with principal place of business at 1600 Amphitheatre Parkway, Mountain View, CA 94043, United States.\n<br />\n2. Accepting this License Agreement\n<br />\n2.1 In order to use the SDK, you must first agree to the License Agreement. You may not use the SDK if you do not accept the License Agreement.\n<br />\n2.2 By clicking to accept, you hereby agree to the terms of the License Agreement.\n<br />\n2.3 You may not use the SDK and may not accept the License Agreement if you are a person barred from receiving the SDK under the laws of the United States or other countries, including the country in which you are resident or from which you use the SDK.\n<br />\n2.4 If you are agreeing to be bound by the License Agreement on behalf of your employer or other entity, you represent and warrant that you have full legal authority to bind your employer or such entity to the License Agreement. If you do not have the requisite authority, you may not accept the License Agreement or use the SDK on behalf of your employer or other entity.\n<br />\n3. SDK License from Google\n<br />\n3.1 Subject to the terms of the License Agreement, Google grants you a limited, worldwide, royalty-free, non-assignable, non-exclusive, and non-sublicensable license to use the SDK solely to develop applications for compatible implementations of Android.\n<br />\n3.2 You may not use this SDK to develop applications for other platforms (including non-compatible implementations of Android) or to develop another SDK. You are of course free to develop applications for other platforms, including non-compatible implementations of Android, provided that this SDK is not used for that purpose.\n<br />\n3.3 You agree that Google or third parties own all legal right, title and interest in and to the SDK, including any Intellectual Property Rights that subsist in the SDK. \"Intellectual Property Rights\" means any and all rights under patent law, copyright law, trade secret law, trademark law, and any and all other proprietary rights. Google reserves all rights not expressly granted to you.\n<br />\n3.4 You may not use the SDK for any purpose not expressly permitted by the License Agreement.  Except to the extent required by applicable third party licenses, you may not copy (except for backup purposes), modify, adapt, redistribute, decompile, reverse engineer, disassemble, or create derivative works of the SDK or any part of the SDK.\n<br />\n3.5 Use, reproduction and distribution of components of the SDK licensed under an open source software license are governed solely by the terms of that open source software license and not the License Agreement.\n<br />\n3.6 You agree that the form and nature of the SDK that Google provides may change without prior notice to you and that future versions of the SDK may be incompatible with applications developed on previous versions of the SDK. You agree that Google may stop (permanently or temporarily) providing the SDK (or any features within the SDK) to you or to users generally at Google's sole discretion, without prior notice to you.\n<br />\n3.7 Nothing in the License Agreement gives you a right to use any of Google's trade names, trademarks, service marks, logos, domain names, or other distinctive brand features.\n<br />\n3.8 You agree that you will not remove, obscure, or alter any proprietary rights notices (including copyright and trademark notices) that may be affixed to or contained within the SDK.\n<br />\n4. Use of the SDK by You\n<br />\n4.1 Google agrees that it obtains no right, title or interest from you (or your licensors) under the License Agreement in or to any software applications that you develop using the SDK, including any intellectual property rights that subsist in those applications.\n<br />\n4.2 You agree to use the SDK and write applications only for purposes that are permitted by (a) the License Agreement and (b) any applicable law, regulation or generally accepted practices or guidelines in the relevant jurisdictions (including any laws regarding the export of data or software to and from the United States or other relevant countries).\n<br />\n4.3 You agree that if you use the SDK to develop applications for general public users, you will protect the privacy and legal rights of those users. If the users provide you with user names, passwords, or other login information or personal information, you must make the users aware that the information will be available to your application, and you must provide legally adequate privacy notice and protection for those users. If your application stores personal or sensitive information provided by users, it must do so securely. If the user provides your application with Google Account information, your application may only use that information to access the user's Google Account when, and for the limited purposes for which, the user has given you permission to do so.\n<br />\n4.4 You agree that you will not engage in any activity with the SDK, including the development or distribution of an application, that interferes with, disrupts, damages, or accesses in an unauthorized manner the servers, networks, or other properties or services of any third party including, but not limited to, Google or any mobile communications carrier.\n<br />\n4.5 You agree that you are solely responsible for (and that Google has no responsibility to you or to any third party for) any data, content, or resources that you create, transmit or display through Android and/or applications for Android, and for the consequences of your actions (including any loss or damage which Google may suffer) by doing so.\n<br />\n4.6 You agree that you are solely responsible for (and that Google has no responsibility to you or to any third party for) any breach of your obligations under the License Agreement, any applicable third party contract or Terms of Service, or any applicable law or regulation, and for the consequences (including any loss or damage which Google or any third party may suffer) of any such breach.\n<br />\n5. Your Developer Credentials\n<br />\n5.1 You agree that you are responsible for maintaining the confidentiality of any developer credentials that may be issued to you by Google or which you may choose yourself and that you will be solely responsible for all applications that are developed under your developer credentials.\n<br />\n6. Privacy and Information\n<br />\n6.1 In order to continually innovate and improve the SDK, Google may collect certain usage statistics from the software including but not limited to a unique identifier, associated IP address, version number of the software, and information on which tools and/or services in the SDK are being used and how they are being used. Before any of this information is collected, the SDK will notify you and seek your consent. If you withhold consent, the information will not be collected.\n<br />\n6.2 The data collected is examined in the aggregate to improve the SDK and is maintained in accordance with Google's Privacy Policy.\n<br />\n7. Third Party Applications\n<br />\n7.1 If you use the SDK to run applications developed by a third party or that access data, content or resources provided by a third party, you agree that Google is not responsible for those applications, data, content, or resources. You understand that all data, content or resources which you may access through such third party applications are the sole responsibility of the person from which they originated and that Google is not liable for any loss or damage that you may experience as a result of the use or access of any of those third party applications, data, content, or resources.\n<br />\n7.2 You should be aware the data, content, and resources presented to you through such a third party application may be protected by intellectual property rights which are owned by the providers (or by other persons or companies on their behalf). You may not modify, rent, lease, loan, sell, distribute or create derivative works based on these data, content, or resources (either in whole or in part) unless you have been specifically given permission to do so by the relevant owners.\n<br />\n7.3 You acknowledge that your use of such third party applications, data, content, or resources may be subject to separate terms between you and the relevant third party. In that case, the License Agreement does not affect your legal relationship with these third parties.\n<br />\n8. Using Android APIs\n<br />\n8.1 Google Data APIs\n<br />\n8.1.1 If you use any API to retrieve data from Google, you acknowledge that the data may be protected by intellectual property rights which are owned by Google or those parties that provide the data (or by other persons or companies on their behalf). Your use of any such API may be subject to additional Terms of Service. You may not modify, rent, lease, loan, sell, distribute or create derivative works based on this data (either in whole or in part) unless allowed by the relevant Terms of Service.\n<br />\n8.1.2 If you use any API to retrieve a user's data from Google, you acknowledge and agree that you shall retrieve data only with the user's explicit consent and only when, and for the limited purposes for which, the user has given you permission to do so. If you use the Android Recognition Service API, documented at the following URL: https://developer.android.com/reference/android/speech/RecognitionService, as updated from time to time, you acknowledge that the use of the API is subject to the Data Processing Addendum for Products where Google is a Data Processor, which is located at the following URL: https://privacy.google.com/businesses/gdprprocessorterms/, as updated from time to time. By clicking to accept, you hereby agree to the terms of the Data Processing Addendum for Products where Google is a Data Processor.\n<br />\n9. Terminating this License Agreement\n<br />\n9.1 The License Agreement will continue to apply until terminated by either you or Google as set out below.\n<br />\n9.2 If you want to terminate the License Agreement, you may do so by ceasing your use of the SDK and any relevant developer credentials.\n<br />\n9.3 Google may at any time, terminate the License Agreement with you if:<br />\n(A) you have breached any provision of the License Agreement; or<br />\n(B) Google is required to do so by law; or<br />\n(C) the partner with whom Google offered certain parts of SDK (such as APIs) to you has terminated its relationship with Google or ceased to offer certain parts of the SDK to you; or<br />\n(D) Google decides to no longer provide the SDK or certain parts of the SDK to users in the country in which you are resident or from which you use the service, or the provision of the SDK or certain SDK services to you by Google is, in Google's sole discretion, no longer commercially viable.<br />\n<br />\n9.4 When the License Agreement comes to an end, all of the legal rights, obligations and liabilities that you and Google have benefited from, been subject to (or which have accrued over time whilst the License Agreement has been in force) or which are expressed to continue indefinitely, shall be unaffected by this cessation, and the provisions of paragraph 14.7 shall continue to apply to such rights, obligations and liabilities indefinitely.\n<br />\n10. DISCLAIMER OF WARRANTIES\n<br />\n10.1 YOU EXPRESSLY UNDERSTAND AND AGREE THAT YOUR USE OF THE SDK IS AT YOUR SOLE RISK AND THAT THE SDK IS PROVIDED \"AS IS\" AND \"AS AVAILABLE\" WITHOUT WARRANTY OF ANY KIND FROM GOOGLE.\n<br />\n10.2 YOUR USE OF THE SDK AND ANY MATERIAL DOWNLOADED OR OTHERWISE OBTAINED THROUGH THE USE OF THE SDK IS AT YOUR OWN DISCRETION AND RISK AND YOU ARE SOLELY RESPONSIBLE FOR ANY DAMAGE TO YOUR COMPUTER SYSTEM OR OTHER DEVICE OR LOSS OF DATA THAT RESULTS FROM SUCH USE.\n<br />\n10.3 GOOGLE FURTHER EXPRESSLY DISCLAIMS ALL WARRANTIES AND CONDITIONS OF ANY KIND, WHETHER EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES AND CONDITIONS OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT.\n<br />\n11. LIMITATION OF LIABILITY\n<br />\n11.1 YOU EXPRESSLY UNDERSTAND AND AGREE THAT GOOGLE, ITS SUBSIDIARIES AND AFFILIATES, AND ITS LICENSORS SHALL NOT BE LIABLE TO YOU UNDER ANY THEORY OF LIABILITY FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, CONSEQUENTIAL OR EXEMPLARY DAMAGES THAT MAY BE INCURRED BY YOU, INCLUDING ANY LOSS OF DATA, WHETHER OR NOT GOOGLE OR ITS REPRESENTATIVES HAVE BEEN ADVISED OF OR SHOULD HAVE BEEN AWARE OF THE POSSIBILITY OF ANY SUCH LOSSES ARISING.\n<br />\n12. Indemnification\n<br />\n12.1 To the maximum extent permitted by law, you agree to defend, indemnify and hold harmless Google, its affiliates and their respective directors, officers, employees and agents from and against any and all claims, actions, suits or proceedings, as well as any and all losses, liabilities, damages, costs and expenses (including reasonable attorneys fees) arising out of or accruing from (a) your use of the SDK, (b) any application you develop on the SDK that infringes any copyright, trademark, trade secret, trade dress, patent or other intellectual property right of any person or defames any person or violates their rights of publicity or privacy, and (c) any non-compliance by you with the License Agreement.\n<br />\n13. Changes to the License Agreement\n<br />\n13.1 Google may make changes to the License Agreement as it distributes new versions of the SDK. When these changes are made, Google will make a new version of the License Agreement available on the website where the SDK is made available.\n<br />\n14. General Legal Terms\n<br />\n14.1 The License Agreement constitutes the whole legal agreement between you and Google and governs your use of the SDK (excluding any services which Google may provide to you under a separate written agreement), and completely replaces any prior agreements between you and Google in relation to the SDK.\n<br />\n14.2 You agree that if Google does not exercise or enforce any legal right or remedy which is contained in the License Agreement (or which Google has the benefit of under any applicable law), this will not be taken to be a formal waiver of Google's rights and that those rights or remedies will still be available to Google.\n<br />\n14.3 If any court of law, having the jurisdiction to decide on this matter, rules that any provision of the License Agreement is invalid, then that provision will be removed from the License Agreement without affecting the rest of the License Agreement. The remaining provisions of the License Agreement will continue to be valid and enforceable.\n<br />\n14.4 You acknowledge and agree that each member of the group of companies of which Google is the parent shall be third party beneficiaries to the License Agreement and that such other companies shall be entitled to directly enforce, and rely upon, any provision of the License Agreement that confers a benefit on (or rights in favor of) them. Other than this, no other person or company shall be third party beneficiaries to the License Agreement.\n<br />\n14.5 EXPORT RESTRICTIONS. THE SDK IS SUBJECT TO UNITED STATES EXPORT LAWS AND REGULATIONS. YOU MUST COMPLY WITH ALL DOMESTIC AND INTERNATIONAL EXPORT LAWS AND REGULATIONS THAT APPLY TO THE SDK. THESE LAWS INCLUDE RESTRICTIONS ON DESTINATIONS, END USERS AND END USE.\n<br />\n14.6 The rights granted in the License Agreement may not be assigned or transferred by either you or Google without the prior written approval of the other party. Neither you nor Google shall be permitted to delegate their responsibilities or obligations under the License Agreement without the prior written approval of the other party.\n<br />\n14.7 The License Agreement, and your relationship with Google under the License Agreement, shall be governed by the laws of the State of California without regard to its conflict of laws provisions. You and Google agree to submit to the exclusive jurisdiction of the courts located within the county of Santa Clara, California to resolve any legal matter arising from the License Agreement. Notwithstanding this, you agree that Google shall still be allowed to apply for injunctive remedies (or an equivalent type of urgent legal relief) in any jurisdiction.",
  "hash": "asdkl",
  "url": "https://developer.android.com/studio/terms.html",
  "name": "Android Software Development Kit License Agreement"
}
```

</p>
</details>

## Dependencies

> The AboutLibraries libraries are published to [Maven Central](https://search.maven.org/artifact/com.mikepenz/aboutlibraries-core).
> Using a version catalog (`libs.versions.toml`) is the recommended way to manage dependencies.

### `libs.versions.toml` (Recommended)

```toml
[versions]
aboutLibraries = "{latest-version}"

[libraries]
# Core module (required for accessing library data)
aboutlibraries-core = { module = "com.mikepenz:aboutlibraries-core", version.ref = "aboutLibraries" }
# Compose UI modules (choose one or both)
aboutlibraries-compose-core = { module = "com.mikepenz:aboutlibraries-compose-core", version.ref = "aboutLibraries" } # Common compose core
aboutlibraries-compose-m2 = { module = "com.mikepenz:aboutlibraries-compose", version.ref = "aboutLibraries" }      # Material 2 UI
aboutlibraries-compose-m3 = { module = "com.mikepenz:aboutlibraries-compose-m3", version.ref = "aboutLibraries" }      # Material 3 UI
# Deprecated View-based UI module
aboutlibraries-view = { module = "com.mikepenz:aboutlibraries", version.ref = "aboutLibraries" }

[plugins]
aboutLibraries = { id = "com.mikepenz.aboutlibraries.plugin", version.ref = "aboutLibraries" }
```

## Core-module

Include the core module to access the generated library information programmatically.

```gradle
// build.gradle.kts
// Recommended: Using version catalog
implementation(libs.aboutlibraries.core)

// Alternative: Direct dependency declaration
// implementation("com.mikepenz:aboutlibraries-core:${latestAboutLibsRelease}")
```

<details><summary><b>(Advanced) Usage</b></summary>
<p>

## Access generated library details

To create a individual integration, access the generated library information programmatically
through the core module.

```kotlin
val libs = Libs.Builder()
    .withJson(aboutLibsJson) // provide the metaData (alternative APIs available)
    .build()
val libraries = libs.libraries // retrieve all libraries defined in the metadata
val licenses = libs.licenses // retrieve all licenses defined in the metadata
for (lib in libraries) {
    Log.i("AboutLibraries", "${lib.name}")
}
```

</p>
</details>

## UI-module (Compose)

Include the Compose UI module(s) for easy integration into Jetpack Compose applications.

```gradle
// build.gradle.kts
// Recommended: Using version catalog (choose M2 or M3, or both)
implementation(libs.aboutlibraries.compose.m2) // Material 2
implementation(libs.aboutlibraries.compose.m3) // Material 3

// Alternative: Direct dependency declaration
// implementation("com.mikepenz:aboutlibraries-compose:${latestAboutLibsRelease}") // Material 2
// implementation("com.mikepenz:aboutlibraries-compose-m3:${latestAboutLibsRelease}") // Material 3
```

### Usage

```kotlin
// Android: Provide resource identifier for the `R.raw.aboutlibraries` file.
val libraries by rememberLibraries(R.raw.aboutlibraries)
LibrariesContainer(libraries, Modifier.fillMaxSize())

// Multiplatform: Using compose-resources API (e.g., src/commonMain/composeResources/files/aboutlibraries.json)
val libraries by rememberLibraries {
    Res.readBytes("files/aboutlibraries.json").decodeToString()
}
LibrariesContainer(libraries, Modifier.fillMaxSize())


// Multiplatform: Manually loading JSON
val libraries by rememberLibraries {
    // Replace with your specific resource loading logic
    useResource("aboutlibraries.json") { res -> res.bufferedReader().readText() }
}
LibrariesContainer(libraries, Modifier.fillMaxSize())
```

<details><summary><b>Advanced Usage</b></summary>
<p>

### Advanced Usage

Provide custom header, divider, and footer items for the `LibrariesContainer`.

```kotlin
// custom header, divider, footer
LibrariesContainer(
    libraries = libraries,
    modifier = Modifier.fillMaxSize().padding(it),
    header = {
        item {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Hello Header")
            }
        }
    },
    divider = { HorizontalDivider() },
    footer = {
        item {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Hello Footer")
            }
        }
    }
)
```

</p>
</details>


<details><summary><b>Compose Multiplatform (Desktop, Wasm, etc.)</b></summary>
<p>

The core and compose modules are Kotlin Multiplatform compatible.
The `app-desktop` and `app-wasm` modules demonstrate usage outside of Android, including manual generation and inclusion of the dependency metadata.

### Generate Dependency Information

Manually export the definitions using the Gradle task. This is typically needed for non-Android platforms or when `registerAndroidTasks` is disabled.

```bash
# Export definitions to the specified file (e.g., for desktop)
./gradlew :app-desktop:exportLibraryDefinitions -PaboutLibraries.outputFile=src/main/resources/libraries.json

# Filter exported definition by variant (e.g., for wasmJs target)
./gradlew :app-wasm:exportLibraryDefinitions -PaboutLibraries.outputFile=src/jsMain/resources/aboutlibraries.json -PaboutLibraries.exportVariant=wasmJs
```

### Run Demo app(s)

```bash
# JVM Desktop app
./gradlew :app-desktop:run

# WASM Web app
./gradlew :app-wasm:run
```

### Screenshot

![Compose Multiplatform Screenshot](https://raw.githubusercontent.com/mikepenz/AboutLibraries/develop/DEV/screenshots/compose-jb.png)

</p>
</details>

## (Legacy) UI-module (View-based)

> [!WARNING]
> **Deprecated:** The legacy View-based UI module (`com.mikepenz:aboutlibraries`) is deprecated and will receive limited support.
> Please migrate to the [Compose UI module](#ui-module-compose).
> For embedding Compose in Views, consider using [ComposeView](https://developer.android.com/develop/ui/compose/migrate/interoperability-apis/compose-in-views).
> While newer Gradle plugin versions *might* be compatible with older UI modules regarding the data format, migration is strongly recommended.

```gradle
// build.gradle.kts
// Recommended: Using version catalog
implementation(libs.aboutlibraries.view)

// Alternative: Direct dependency declaration
// implementation("com.mikepenz:aboutlibraries:${latestAboutLibsRelease}")
```

<details><summary><b>Legacy Usage</b></summary>
<p>

### Usage

Use this library in a few different ways. Create a custom activity, including a custom style or just
use its generated information. Or simply use the built-in Activity or Fragment and just pass the
libs to include.

> **Note**: The new version requires the new Material3 theme as base.

#### Activity

```kotlin
LibsBuilder()
    .start(this) // start the activity
```

The activity uses a toolbar, which requires the appropriate theme.
See [Style the AboutLibraries](#style-the-aboutlibraries-%EF%B8%8F) for more details

#### Fragment

```kotlin
val fragment = LibsBuilder()
    .supportFragment()
```

#### About this App UI

The `AboutLibraries` library also offers the ability to create an `About this app` screen.
Add the following .xml file (or just the strings - the key must be the same) to the project.

```xml

<resources>
    <string name="aboutLibraries_description_showIcon">true</string>
    <string name="aboutLibraries_description_showVersion">true</string>
    <string name="aboutLibraries_description_text">Place the description here :D</string>
</resources>
```

or use the builder and add following:

```kotlin
.withAboutIconShown(true)
    .withAboutVersionShown(true)
    .withAboutDescription("This is a small sample which can be set in the about my app description file.<br /><b>Style this with html markup :D</b>")
```

#### Style the AboutLibraries 🖌️

Create a custom style for the AboutLibraries UI.

```xml
// define a custom style
<style name="CustomAboutLibrariesStyle" parent="">
    <!-- AboutLibraries specific values -->
    <item name="aboutLibrariesCardBackground">?cardBackgroundColor</item>
    <item name="aboutLibrariesDescriptionTitle">?android:textColorPrimary</item>
    <item name="aboutLibrariesDescriptionText">?android:textColorSecondary</item>
    <item name="aboutLibrariesDescriptionDivider">@color/opensource_divider</item>
    <item name="aboutLibrariesOpenSourceTitle">?android:textColorPrimary</item>
    <item name="aboutLibrariesOpenSourceText">?android:textColorSecondary</item>
    <item name="aboutLibrariesSpecialButtonText">?android:textColorPrimary</item>
    <item name="aboutLibrariesOpenSourceDivider">@color/opensource_divider</item>
</style>

        // define the custom styles for the theme

<style name="SampleApp" parent="Theme.MaterialComponents.Light.NoActionBar">
...
<item name="aboutLibrariesStyle">@style/CustomAboutLibrariesStyle</item>
...
</style>
```

</p>
</details>

## Enterprise / Manual JSON Handling

For environments requiring full control over the included `aboutlibraries.json` (e.g., manual verification, CI generation), you can disable automatic task registration and handle
generation manually.

```kts
// build.gradle.kts
aboutLibraries {
    android {
        // Disable automatic task registration for Android builds
        registerAndroidTasks = false
    }
    export {
        // Define the output path for manual generation
        // Adjust the path based on your project structure (e.g., composeResources, Android res/raw)
        outputFile = file("src/commonMain/composeResources/files/aboutlibraries.json")
        // Optionally specify the variant for export
        // variant = "release"
    }
}
```

Generate the file manually using the Gradle task:

```bash
# Generate using the configured location and variant
./gradlew :app:exportLibraryDefinitions

# Generate providing a custom path and variant override
./gradlew :app:exportLibraryDefinitions -PaboutLibraries.outputFile=src/main/res/raw/libraries.json -PaboutLibraries.exportVariant=release
```

Commit the generated `aboutlibraries.json` to your SCM. The library will then need to load this file manually. See details in [Usage](#usage).

## Gradle API / Tasks

By default, the gradle plugin is automatically executed for Android projects, generating the library
metadata where it's automatically discovered by the `ui` modules.
For other environments or for more advanced usages the plugin offers additional APIs.

```bash
# Manually generate the dependency metaData in the provided location. Allows to commit it in SCM
# Exports the metaData in `src/main/resources/` relative to the module root
./gradlew app-desktop:exportLibraryDefinitions -PaboutLibraries.outputFile=src/main/resources/libraries.json
# Export only for a specific variant: `release`
./gradlew app-desktop:exportLibraryDefinitions -PaboutLibraries.outputFile=src/main/resources/libraries.json -PaboutLibraries.exportVariant=release

# Export dependencies to CLI in CSV format
./gradlew app:exportLibraries
./gradlew app:exportLibraries${Variant}

# Outputs all dependencies with name, version and their identifier
./gradlew app:findLibraries

# Exports all dependencies in a format helpful for compliance reports.
# By default writes `export.csv` and `export.txt` and `dependencies` folder in the root of the project.
./gradlew app:exportComplianceLibraries${Variant} -PaboutLibraries.exportPath=complianceReport/

# List all funding options for included projects (as identified via the e.g.: GitHub API)
./gradlew app:fundLibraries
```

# Special repository support

| Host                          | License | Funding |
|-------------------------------|---------|---------|
| [GitHub](https://github.com/) | x       | x       |

# Disclaimer

This library uses all compile time dependencies (and their sub dependencies) as defined in the
`build.gradle` file.
This could lead to dependencies which are only used during compilation (and not actually distributed
in the app) to be listed or missing in the attribution screen.
It might also fail to identify licenses if the dependencies do not define it properly in their
pom.xml file.

Careful optimisation and review of all licenses is recommended to really include all required
dependencies. The use of the gradle commands like `findLibraries` can help doing this.

It is also important that native sub dependencies can *not* be resolved automatically as they are
not included via gradle.
Additional dependencies can be provided via the plugins API to extend and provide any additional
details.

# Credits

Starting from v12.1.0 some parts of the plugin were refactored to use different APIs to retrieve dependencies.
Some of these refactored components were heavily inspired by the amazing https://github.com/cashapp/licensee/tree/1.13.0 project.

# Developed By

- Mike Penz
    - [mikepenz.dev](https://mikepenz.dev) - [blog.mikepenz.dev](https://blog.mikepenz.dev) - <mikepenz@gmail.com>
    - [paypal.me/mikepenz](http://paypal.me/mikepenz)
    - [Automatic changelog generation action](https://github.com/marketplace/actions/release-changelog-builder)

# License

    Copyright 2024 Mike Penz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
