# AboutLibraries [![Status](https://travis-ci.org/mikepenz/AboutLibraries.svg?branch=master)](https://travis-ci.org/mikepenz/AboutLibraries) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.mikepenz/aboutlibraries/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.mikepenz/aboutlibraries)

.. allows you to easily create an **used open source libraries** fragment/activity within your app. All the library information is automatically collected from the POM information of your depencencies and included during compile time.
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
- autodetect libraries (via the gradle depencies)
- many included library details
- automatic created fragment/activity
- feature rich builder to simply create and start the fragment / activities
- large amount of configuration options
  - usage standalone possible too
- much much more... try the sample for a quick overview.

# Screenshots
![Image](https://raw.githubusercontent.com/mikepenz/AboutLibraries/master/DEV/screenshots/screenshot1_small.png)
![Image](https://raw.githubusercontent.com/mikepenz/AboutLibraries/master/DEV/screenshots/screenshot2_small.png)

# Setup

## Latest releases 🛠

- Kotlin && Gradle Plugin | [v8.0.0-rc02](https://github.com/mikepenz/FastAdapter/tree/v8.0.0-rc02)
- Kotlin | [v7.1.0](https://github.com/mikepenz/FastAdapter/tree/v7.1.0)
- Java && AndroidX | [v6.2.3](https://github.com/mikepenz/FastAdapter/tree/v6.2.3)
- Java && AppCompat | [v6.1.1](https://github.com/mikepenz/FastAdapter/tree/v6.1.1)

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

## WITHOUT gradle plugin (not recommended)

If you do not want to use the gradle plugin, you need to add the legacy definition files, which will then be included in the built apk, and resolved via reflection during runtime.
> NOTE: This is not recommended. Please migrate to use the gradle plugin

```gradle
implementation "com.mikepenz:aboutlibraries-definitions:${latestAboutLibsRelease}"
```

# Basic Usage
You can use this library in a few different ways. Create your own activity, including a custom style or just use its generated information. Or simply use the built-in Activity or Fragment and just pass the libs you would love to include.

### Activity / Fragment

#### Activity

```kotlin
LibsBuilder()
    .start(this) // start the activity
```

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
./gradlew exportLibraries
```
Exports all libraries in a CSV format with the name, artifactId, and licenseId. And a seperate list with all licenses used, and a potential list of unmatched libraries / licenses.

## Find

```kotlin
./gradlew findLibraries
```
Finds all included libraries with their name, and the unique AboutLibraries identifier which can be used to modify libraries and their information, or create custom mapping information if required.
See the `Config` section for more information.

# Advanced Usage

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
    <item name="aboutLibrariesWindowBackground">?android:colorBackground</item>
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
    configPath = "config" // the path to the directory containing configuration files
}
```

This directory may contain one or more of the following configurations:

```
custom_enchant_mapping.prop // allows providing custom mapping files to overwrite the information from the POM file
custom_license_mappings.prop // allows defining the licenseId which should be used for the library (if not resolveable via the POM file)
custom_license_year_mappings.prop // allows defining the license Year for this library (this information CANNOT be resolved from the POM file)
custom_name_mappings.prop // allows overwriting the name of a library if the POM specifies unexpected information
```

See the corresponding files here for the format and content: https://github.com/mikepenz/AboutLibraries/tree/develop/library-definitions/src/main/res/raw

## ProGuard
Exclude `R` from ProGuard to enable the **libraries auto detection**
```proguard
-keep class .R
-keep class **.R$* {
    <fields>;
}
```

In case you want to minimize your resources as much as possible use the following rules (Thanks to @rubengees and @AllanWang as discussed here: https://github.com/mikepenz/AboutLibraries/issues/331)
```proguard
-keepclasseswithmembers class **.R$* {
    public static final int define_*;
}
```
These rules **will** require you to add the libraries manually. (see more in the above linked issue)

# Used by
(feel free to send me new projects)

* [wallsplash](https://play.google.com/store/apps/details?id=com.mikepenz.unsplash)
* [Numbers](https://play.google.com/store/apps/details?id=com.tundem.numbersreloaded.free)
* [MegaYatzy](https://play.google.com/store/apps/details?id=com.tundem.yatzyTJ)
* [Sir Spellalot](https://play.google.com/store/apps/details?id=com.sirspellalot.app.android)
* [TV Time](https://play.google.com/store/apps/details?id=com.tozelabs.tvshowtime)
* [Strength](https://play.google.com/store/apps/details?id=com.e13engineering.strength)
* [Sprit Club](https://play.google.com/store/apps/details?id=at.idev.spritpreise)
* [Hold'Em Poker Manager](https://play.google.com/store/apps/details?id=pt.massena.holdemtracker.free)
* [PixCell8](https://play.google.com/store/apps/details?id=com.pixcell8.prod)
* [ML Manager](https://play.google.com/store/apps/details?id=com.javiersantos.mlmanager)
* [TurnMe Panorama](https://play.google.com/store/apps/details?id=com.bezine.panosphere)
* [Navig'Tours](https://play.google.com/store/apps/details?id=com.codetroopers.transport.tours)
* [AS Sales Management](https://play.google.com/store/apps/details?id=com.armsoft.mtrade)
* [News](https://play.google.com/store/apps/details?id=com.moblino.countrynews)
* [Makota Money Manager](https://play.google.com/store/apps/details?id=be.jatra.makota)
* [Companion for Band](https://play.google.com/store/apps/details?id=com.pimp.companionforband)
* [Ask Janee - Answer Questions](https://play.google.com/store/apps/details?id=com.askjanee.app.android)
* [Temadagar](https://play.google.com/store/apps/details?id=com.alvarlagerlof.temadagarapp)
* [KPS Connect](https://play.google.com/store/apps/details?id=me.msfjarvis.kpsconnect)
* [Budget Manager](https://play.google.com/store/apps/details?id=com.jakubmateusiak.budgetmanager)
* [Calendula](https://play.google.com/store/apps/details?id=es.usc.citius.servando.calendula)
* [Drinking Games](https://play.google.com/store/apps/details?id=com.drinkinggames.android)
* [Recipedia](https://play.google.com/store/apps/details?id=com.md.recipedia)
* [Ordkamp FREE](https://play.google.com/store/apps/details?id=com.betapet.mobile.dk.free)
* [Ordkamp](https://play.google.com/store/apps/details?id=com.betapet.mobile.dk.full)
* [Ordspill GRATIS](https://play.google.com/store/apps/details?id=com.ap.ordspill.free)
* [Ordspill](https://play.google.com/store/apps/details?id=com.ap.ordspill.full)
* [Betapet FREE](https://play.google.com/store/apps/details?id=com.betapet.mobile.se.free)
* [Betapet](https://play.google.com/store/apps/details?id=com.betapet.mobile.se.full)
* [Contact Lenses Time](https://play.google.com/store/apps/details?id=com.brando.lenti)
* [HTTP Shortcuts](https://github.com/Waboodoo/HTTP-Shortcuts)
* [KAU (library)](https://allanwang.github.io/KAU/about/)
* [Frost for Facebook](https://play.google.com/store/apps/details?id=com.pitchedapps.frost)
* [OneMeme: Meme Maker](https://play.google.com/store/apps/details?id=com.mememaker.android&hl)
* [andOTP](https://play.google.com/store/apps/details?id=org.shadowice.flocke.andotp)

# Developed By

* Mike Penz 
 * [mikepenz.com](http://mikepenz.com) - <mikepenz@gmail.com>
 * [paypal.me/mikepenz](http://paypal.me/mikepenz)

# License

    Copyright 2020 Mike Penz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
