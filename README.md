# AboutLibraries

.. allows you to easily create an **used open source libraries** fragment/activity within your app. All the library information is automatically collected from the POM information of your dependencies and included during compile time.
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
- **about this app** section (optional)
- autodetect libraries (via the gradle dependencies)
- many included library details
- automatic created fragment/activity
- feature rich builder to simply create and start the fragment / activities
- large amount of configuration options
  - usage standalone possible too
- much much more... try the sample for a quick overview.

# Screenshots
![Image](https://raw.githubusercontent.com/mikepenz/AboutLibraries/develop/DEV/screenshots/screenshots.jpg)

# Setup

## Latest releases 🛠

- Kotlin && Gradle Plugin | [v8.9.0](https://github.com/mikepenz/AboutLibraries/tree/v8.9.0)
- Kotlin | [v7.1.0](https://github.com/mikepenz/AboutLibraries/tree/v7.1.0) - Deprecated
- Java && AndroidX | [v6.2.3](https://github.com/mikepenz/AboutLibraries/tree/v6.2.3) - Deprecated
- Java && AppCompat | [v6.1.1](https://github.com/mikepenz/AboutLibraries/tree/v6.1.1) - Deprecated

## Gradle Plugin

As a new feature of the AboutLibraries v8.x.y we offer a gradle plugin which will resolve the dependency during compilation, and only includes the libraries which are really specified as dependencies.

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
    .withFields(R.string::class.java.fields) // in some cases it may be needed to provide the R class, if it can not be automatically resolved
    .withLibraryModification("aboutlibraries", Libs.LibraryFields.LIBRARY_NAME, "_AboutLibraries") // optionally apply modifications for library information
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

## Jetpack navigation

Include the AboutLibraries destination in your graph

```xml
<include app:graph="@navigation/aboutlibs_navigation" />
```

After that you can define it as your target
```xml
<action
    android:id="@+id/action_x_to_about_libs"
    app:destination="@id/about_libraries" />
```

> To configure provide the `LibsBuilder` as the data argument

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
    configPath = "config" // the path to the directory relative to the root of the whole project containing configuration files
}
```

This directory may contain one or more of the following configurations:

```
custom_enchant_mapping.prop // allows providing custom mapping files to overwrite the information from the POM file
custom_license_mappings.prop // allows defining the licenseId which should be used for the library (if not resolvable via the POM file)
custom_license_year_mappings.prop // allows defining the license Year for this library (this information CANNOT be resolved from the POM file)
custom_name_mappings.prop // allows overwriting the name of a library if the POM specifies unexpected information
custom_author_mappings.prop // allows overwriting the authors of a library if the POM specifies unexpected information
custom_exclusion_list.prop // allows excluding libraries by their id at build time
```

See the corresponding files here for the format and content: https://github.com/mikepenz/AboutLibraries/tree/develop/aboutlibraries-definitions/src/main/res/raw

### Exclude libraries

> This is mainly meant for internal libraries, or projects. Full attribution to used projects is appreciated.

```groovy
aboutLibraries {
    // allows to specify regex patterns to exclude some libraries
    // preferred for internal libraries, ...
    // The regex applies onto the uniqueId. E.g.: `com_mikepenz__materialdrawer`
    // specify with the groovy regex syntax.
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

Alternatively all licenses can be included in the project.

> NOTE: Doing this will include extra string resources in your project.

```groovy
aboutLibraries {
    includeAllLicenses = true
}
```

## Custom Licenses

It is possible to add additional licenses. In order to do so, you have to add the content of the license as an own text file in the app's raw folder e.g. `app/src/main/res/raw/myLicense.txt`
This file will contain the full raw license text which may be too long for the strings.xml file.

```html
<h3>GNU GENERAL PUBLIC LICENSE</h3>

<p>Version 2, June 1991</p>

<p>
Copyright &copy; 1989, 1991 Free Software Foundation, Inc.<br/>
51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA<br/><br/>

Everyone is permitted to copy and distribute verbatim copies
of this license document, but changing it is not allowed.
```

Next, you need to add string identifiers that allow the plugin to recognize the license.

```xml
<!-- identifier used to reference this license -->
<string name="define_license_myLicense" translatable="false" />
<string name="license_myLicense_licenseName" translatable="false">Custom License</string>
<string name="license_myLicense_licenseWebsite" translatable="false">https://www.gnu.org/licenses/gpl-2.0.html</string>
<string name="license_myLicense_licenseShortDescription" translatable="false">
        <![CDATA[
        <p>&lt;one line to give the program\'s name and a brief idea of what it does.&gt;
        Copyright &copy; &lt;year&gt;  &lt;name of author&gt;</p>
        <p>This program is free software; you can redistribute it and/or
        modify it under the terms of the GNU General Public License
        as published by the Free Software Foundation; either version 2
        of the License, or (at your option) any later version.</p>
        <p>This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.</p>
        <p>You should have received a copy of the GNU General Public License
        along with this program; if not, write to the Free Software
        Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.</p>
        ]]>
    </string>
<!-- name of the license file under the raw folder -->
<string name="license_myLicense_licenseDescription" translatable="false">raw:myLicense</string>
```

Finally, you have to use the specified identifier `myLicense` when referencing this license in your mappings.

## Custom Libraries

In case the plugin fails to detect a library or you're using an embedded library, you can manually add an entry that will be picked up by the plugin. All you have to do is define custom string identifiers for the library.

```xml
<resources>
    <!-- identifier used to reference this library -->
    <string name="define_plu_myLibrary">year;owner</string>
    <string name="library_myLibrary_author">Author</string>
    <string name="library_myLibrary_authorWebsite">https://mikepenz.dev</string>
    <string name="library_myLibrary_libraryName">My Library</string>
    <string name="library_myLibrary_libraryDescription">Some text</string>
    <string name="library_myLibrary_libraryVersion">10.1.1</string>
    <string name="library_myLibrary_libraryWebsite">https://mikepenz.dev</string>
    <!-- you can also reference custom licenses here e.g. myLicense -->
    <string name="library_myLibrary_licenseIds">apache_2_0</string>
    <string name="library_myLibrary_isOpenSource">true</string>
    <string name="library_myLibrary_repositoryLink">https://mikepenz.dev</string>
    <!-- Custom variables section -->
    <string name="library_myLibrary_owner">Owner</string>
    <string name="library_myLibrary_year">2020</string>
</resources>
```

## Usage WITHOUT gradle plugin (deprecated)

If you do not want to use the gradle plugin, you need to add the legacy definition files, which will then be included in the built apk, and resolved via reflection during runtime.

> NOTE: This is not recommended. Please migrate to use the gradle plugin

```gradle
implementation "com.mikepenz:aboutlibraries-definitions:${latestAboutLibsRelease}"
```

## ProGuard

ProGuard / R8 rules are bundled internally with the core module.

> Please check the configuration in regards to passing in the fields `.withFields(R.string::class.java.fields)`

# Disclaimer

This library uses all compile time dependencies (and their sub dependencies) as defined in your `build.gradle` file,
this could lead to dependencies which are only used during compilation (and not actually distributed in your app) to be listed or missing in the attribution screen.
It might also fail to identify licenses if the dependencies do not define it properly in their pom.xml file.

Careful optimisation and review of all licenses is recommended to really include all required dependencies. The use of the gradle commands like `findLibraries` can help doing this.

It is also important that native sub dependencies can *not* be resolved automatically as they are not included via gradle.
Additional dependencies can be provided via this plugins API to extend and provide any additional details.

# Used by
(feel free to send me new projects)

* [TV Time](https://play.google.com/store/apps/details?id=com.tozelabs.tvshowtime)
* [Sprit Club](https://play.google.com/store/apps/details?id=at.idev.spritpreise)
* [ML Manager](https://play.google.com/store/apps/details?id=com.javiersantos.mlmanager)
* [TurnMe Panorama](https://play.google.com/store/apps/details?id=com.bezine.panosphere)
* [Navig'Tours](https://play.google.com/store/apps/details?id=com.codetroopers.transport.tours)
* [AS Sales Management](https://play.google.com/store/apps/details?id=com.armsoft.mtrade)
* [News](https://play.google.com/store/apps/details?id=com.moblino.countrynews)
* [Makota Money Manager](https://play.google.com/store/apps/details?id=be.jatra.makota)
* [Budget Manager](https://play.google.com/store/apps/details?id=com.jakubmateusiak.budgetmanager)
* [Calendula](https://play.google.com/store/apps/details?id=es.usc.citius.servando.calendula)
* [Drinking Games](https://play.google.com/store/apps/details?id=com.drinkinggames.android)
* [Recipedia](https://play.google.com/store/apps/details?id=com.md.recipedia)
* [Ordkamp FREE](https://play.google.com/store/apps/details?id=com.betapet.mobile.dk.free)
* [Ordkamp](https://play.google.com/store/apps/details?id=com.betapet.mobile.dk.full)
* [Ordspill GRATIS](https://play.google.com/store/apps/details?id=com.ap.ordspill.free)
* [Ordspill](https://play.google.com/store/apps/details?id=com.ap.ordspill.full)
* [Betapet FREE](https://play.google.com/store/apps/details?id=com.betapet.mobile.se.free)
* [Contact Lenses Time](https://play.google.com/store/apps/details?id=com.brando.lenti)
* [HTTP Shortcuts](https://github.com/Waboodoo/HTTP-Shortcuts)
* [KAU (library)](https://allanwang.github.io/KAU/about/)
* [OneMeme: Meme Maker](https://play.google.com/store/apps/details?id=com.mememaker.android&hl)
* [andOTP](https://play.google.com/store/apps/details?id=org.shadowice.flocke.andotp)
* [MyLife - Journal](https://play.google.com/store/apps/details?id=com.pi143.mylife)
* [LibreAV](https://github.com/projectmatris/antimalwareapp)
* [Honda RoadSync](https://play.google.com/store/apps/details?id=com.honda.ms.dm.sab)
* [SimpleSettings Library](https://github.com/marcauberer/simple-settings)
* [Orna Companion](https://play.google.com/store/apps/details?id=nl.bryanderidder.ornaguide)
* [School](https://github.com/daannnnn/School)
* [Secure File Manager](https://play.google.com/store/apps/details?id=com.securefilemanager.app)

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
