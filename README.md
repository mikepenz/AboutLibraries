# AboutLibraries

.. collects all dependency details including licenses at compile time, and offers simple APIs to visualize these in the app.
*No runtime overhead.* Strong caching. Any dependency is supported.

-------

<p align="center">
    <a href="#whats-included-">What's included 🚀</a> &bull;
    <a href="#setup">Setup 🛠️</a> &bull;
    <a href="MIGRATION.md">Migration Guide 🧬</a> &bull;
    <a href="https://github.com/mikepenz/AboutLibraries/wiki">WIKI 📖</a> &bull;
    <a href="#used-by">Used by</a> &bull;
    <a href="https://play.google.com/store/apps/details?id=com.mikepenz.aboutlibraries.sample">Sample App</a>
</p>

-------

### What's included 🚀
- **used open source libraries**
	- name, description, creator, license, version, ...
- Kotlin Multiplatform support
- autodetect libraries (via the gradle dependencies)
  - configuration and build caching support
- libraries include a significant amount of meta data
- generated data can be accessed directly and included in custom UI
- Compose support

# Screenshots
![Image](https://raw.githubusercontent.com/mikepenz/AboutLibraries/develop/DEV/screenshots/screenshots.jpg)

# Setup

## Latest releases 🛠

- (NEW) Kotlin | [v10.0.0-a01](https://github.com/mikepenz/AboutLibraries/tree/v10.0.0-a01)
- Kotlin && Gradle Plugin | [v8.9.4](https://github.com/mikepenz/AboutLibraries/tree/v8.9.4)

## Gradle Plugin

AboutLibraries v10 includes a completely redone plugin, with build and configuration cache support. It includes all dependencies as found based on the gradle configuration.

```gradle
// Root build.gradle
classpath "com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:${latestAboutLibsRelease}"

// App build.gradle
apply plugin: 'com.mikepenz.aboutlibraries.plugin'
```

## Using Maven
The AboutLibraries Library is pushed to [Maven Central](http://search.maven.org/#search|ga|1|g%3A%22com.mikepenz%22).
The gradle plugin is hosted via [Gradle Plugins](https://plugins.gradle.org/plugin/com.mikepenz.aboutlibraries.plugin).

## CORE module

```gradle
implementation "com.mikepenz:aboutlibraries-core:${latestAboutLibsRelease}"
```

## UI module

```gradle
implementation "com.mikepenz:aboutlibraries:${latestAboutLibsRelease}"

//required support lib modules
implementation "androidx.appcompat:appcompat:${versions.appcompat | 1.x.y}"
implementation "androidx.cardview:cardview:${versions.cardview | 1.x.y}"
implementation "androidx.recyclerview:recyclerview:${versions.recyclerview | 1.1.y}"
implementation "com.google.android.material:material:${versions.material | 1.1.y}"
```

# Basic Usage
You can use this library in a few different ways. Create your own activity, including a custom style or just use its generated information. Or simply use the built-in Activity or Fragment and just pass the libs you would love to include.

### Activity / Fragment

> NOTE: These integrations require the `ui-module`

#### Activity

```kotlin
LibsBuilder()
    .start(this) // start the activity
```

The activity uses a toolbar, which requires the appropriate theme. See [Style the AboutLibraries](#style-the-aboutlibraries-%EF%B8%8F) for more details

#### Fragment
```kotlin
val fragment = LibsBuilder()
    .supportFragment()
```

# Gradle API

The gradle plugin will automatically run when building the application, so no action is required to build the library information showed / retrieved via the `Libs` class.
But there are additional commands which may be helpful for various situations.

## Export Library information

```kotlin
./gradlew exportLibraries // exists also per variant
```
Exports all libraries in a CSV format with the name, artifactId, and licenseId. And a separate list with all licenses used, and a potential list of unmatched libraries / licenses.

## Find

```kotlin
./gradlew findLibraries
```
Finds all included libraries with their name, and the unique AboutLibraries identifier which can be used to modify libraries and their information, or create custom mapping information if required.
See the `Config` section for more information.

# Advanced Usage

## Access generated library details

If you want to create your own integration you can access the generated library information programmatically through the core module.

```kotlin
val libraries = Libs(this).libraries
for (lib in libraries) {
    Log.e("AboutLibraries", "${lib.libraryName} from ${lib.author}")
}
```

## About this App UI
You can also use the AboutLibraries activity as an "about this app" screen.
Add the following .xml file (or just the strings - the key must be the same) to your project.

```xml
<resources>
    <string name="aboutLibraries_description_showIcon">true</string>
    <string name="aboutLibraries_description_showVersion">true</string>
    <string name="aboutLibraries_description_text">Place your description here :D</string>
</resources>
```
or use the builder and add following:
```kotlin
	.withAboutIconShown(true)
	.withAboutVersionShown(true)
	.withAboutDescription("This is a small sample which can be set in the about my app description file.<br /><b>You can style this with html markup :D</b>")
```

## Style the AboutLibraries 🖌️

Create your custom style. If you don't need a custom theme see the next section, how you can set the colors just by overwriting the original colors.
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

## Gradle Plugin Configuration

It is possible to provide custom configurations / adjustments to the automatic detection. This can be done via the gradle plugin.

```groovy
aboutLibraries {
    configPath = "config"
    // provide libraries or licenses to be integrated in the generated data
    // see the sample project for a demo usage
    // libraries are merged using the `uniqueId`, and licenses using the `hash`
}
```

See the corresponding files here for the format and content: https://github.com/mikepenz/AboutLibraries/tree/develop/config

### Exclude libraries

```groovy
aboutLibraries {
    // allows to specify regex patterns to exclude some libraries preferred for internal libraries, ...
    // The regex applies onto the uniqueId. E.g.: `com.mikepenz:materialdrawer` specify with the groovy regex syntax.
    exclusionPatterns = [~"com_.*", ~/com_mylibrary_.*/]
}
```

### Include undetected licenses

Only licenses from gradle dependencies with matching pom information are loaded. You may need to manually include some licenses if they are missing.
Required identifiers can be found in the [License.kt](https://github.com/mikepenz/AboutLibraries/blob/develop/plugin-build/plugin/src/main/kotlin/com/mikepenz/aboutlibraries/plugin/mapping/License.kt) class.

```groovy
aboutLibraries {
    // Licenses specified here will be included even if undetected.
    additionalLicenses {
        mit
        mpl_2_0
        LGPL_2_1_or_later
    }
}
```

# Disclaimer

This library uses all compile time dependencies (and their sub dependencies) as defined in your `build.gradle` file,
this could lead to dependencies which are only used during compilation (and not actually distributed in your app) to be listed or missing in the attribution screen.
It might also fail to identify licenses if the dependencies do not define it properly in their pom.xml file.

Careful optimisation and review of all licenses is recommended to really include all required dependencies. The use of the gradle commands like `findLibraries` can help doing this.

It is also important that native sub dependencies can *not* be resolved automatically as they are not included via gradle.
Additional dependencies can be provided via this plugins API to extend and provide any additional details.

# Used by
(feel free to send me new projects)

# Developed By

- Mike Penz
  - [mikepenz.dev](https://mikepenz.dev) - [blog.mikepenz.dev](https://blog.mikepenz.dev) - <mikepenz@gmail.com>
  - [paypal.me/mikepenz](http://paypal.me/mikepenz)
  - [Automatic changelog generation action](https://github.com/marketplace/actions/release-changelog-builder)

# License

    Copyright 2021 Mike Penz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
