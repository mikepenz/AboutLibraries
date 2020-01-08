# AboutLibraries [![Status](https://travis-ci.org/mikepenz/AboutLibraries.svg?branch=master)](https://travis-ci.org/mikepenz/AboutLibraries) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.mikepenz/aboutlibraries/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.mikepenz/aboutlibraries) [![Android Arsenal](http://img.shields.io/badge/Android%20Arsenal-AboutLibraries-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/102)

[![Join the chat at https://gitter.im/mikepenz/AboutLibraries](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/mikepenz/AboutLibraries?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

The **AboutLibraries** library allows you to easily create an **used open source libraries** fragment/activity within your app. As an extra feature you can also add an **about this app** section.
All the library information is automatically collected from the POM information of the depencencies and included during compile time. No runtime overhead. Strong caching, so build times stay short. Due to usage of maven POM information practically any dependency is supported.

Here's a quick overview of functions it include:
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

# Motivation

Most modern apps feature a "Used Library"-section, which requires information about those respective libs. As it gets annoying to always copy those strings to your app, **AboutLibraries** is there for the rescue.

# Migration
- [MIGRATION GUIDE](https://github.com/mikepenz/AboutLibraries/blob/develop/MIGRATION.md)

# Get started
- [Include in your project](#include-in-your-project)
- [Usage](#usage)
- [Contribute](#contribute)

# More...
- [Sample (Google Play Store)](https://play.google.com/store/apps/details?id=com.mikepenz.aboutlibraries.sample)
- [Get detailed instructions in the wiki](https://github.com/mikepenz/AboutLibraries/wiki)

# Screenshots
![Image](https://raw.githubusercontent.com/mikepenz/AboutLibraries/master/DEV/screenshots/screenshot1_small.png)
![Image](https://raw.githubusercontent.com/mikepenz/AboutLibraries/master/DEV/screenshots/screenshot2_small.png)

# Include in your project

## Latest releases

- Kotlin && Gradle Plugin | [v8.0.0-b01](https://github.com/mikepenz/FastAdapter/tree/v8.0.0-b01)
- Kotlin | [v7.1.0](https://github.com/mikepenz/FastAdapter/tree/v7.1.0)
- Java && AndroidX | [v6.2.3](https://github.com/mikepenz/FastAdapter/tree/v6.2.3)
- Java && AppCompat | [v6.1.1](https://github.com/mikepenz/FastAdapter/tree/v6.1.1)

## Gradle Plugin

As a new feature of the AboutLibraries v8.x.y we offer a gradle plugin which will resolve the dependency during compilation, and only includes the libraries which are really specified as dependencies.

```javascript
// Root build.gradle
classpath "com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:${latestAboutLibsRelease}"

// App build.gradle
apply plugin: 'com.mikepenz.aboutlibraries.plugin'
```

## Using Maven
The AboutLibraries Library is pushed to [Maven Central](http://search.maven.org/#search|ga|1|g%3A%22com.mikepenz%22).

## CORE module

```javascript
implementation "com.mikepenz:aboutlibraries-core:${latestAboutLibsRelease}"
```

## UI module

```javascript
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

```javascript
implementation "com.mikepenz:aboutlibraries-definitions:${latestAboutLibsRelease}"
```

# Usage
You can use this library in a few different ways. You can create your own activity, including a custom style and just use the information, or you can use the built-in Activity or Fragment and just pass the libs you would love to include.

### Upgrade Notes
> If you upgrade from < 8.x.y follow the [MIGRATION GUIDE](https://github.com/mikepenz/AboutLibraries/blob/develop/MIGRATION.md)

### Activity / Fragment
#### Fragment
```kotlin
val fragment = LibsBuilder()
                .withLibraryModification("aboutlibraries", Libs.LibraryFields.LIBRARY_NAME, "_AboutLibraries")
                .supportFragment()
```
#### Activity
##### Code:
```kotlin
LibsBuilder()
        //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
        //start the activity
    .start(this)
```

## Small extra
For those who read the whole README here's one more thing.
You can also use the AboutLibraries activity as an "about this app" screen. You ask how?
Yeah pretty simple just add the following .xml file (or just the strings - the key must be the same) to your project.

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


## Contribute
You can contribute by creating a information file for a new library, and open a pull-request at the creators Git repository. If he doesn't include the information file in his repo, or if the library isn't maintained anymore you can create a pull-request here. Find more information in the wiki [Create a definition file](https://github.com/mikepenz/AboutLibraries/wiki/HOWTODEV:-Include-into-AboutLibraries)


## Already in use in following apps
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

# Wiki
You can find anything you search for in the wiki. (If not open an issue)

[Bring me to the wiki](https://github.com/mikepenz/AboutLibraries/wiki)

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
