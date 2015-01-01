#AboutLibraries  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.mikepenz.aboutlibraries/library/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.mikepenz.aboutlibraries/library) [![Android Arsenal](http://img.shields.io/badge/Android%20Arsenal-AboutLibraries-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/102)

AboutLibraries is a library to offer you all the information you need of your libraries!

Most modern apps feature an "Used Library"-section and for this some information of those libs is required. As it gets annoying to copy those strings always to your app I've developed this small helper library to provide the required information.

#Get started
- [Include in your project](#include-in-your-project)
- [Usage](#usage)
- [Contribute](#contribute)

#More...
- [Sample (Google Play Store)](https://play.google.com/store/apps/details?id=com.mikepenz.aboutlibraries.sample)
- [Create new definition files](http://def-builder.mikepenz.com/)
- [Get detailed instructions in the wiki](https://github.com/mikepenz/AboutLibraries/wiki)
- [Compatible/included libs](https://github.com/mikepenz/AboutLibraries/wiki/Compatible-Libs)


#Screenshots
![Image](https://raw.githubusercontent.com/mikepenz/AboutLibraries/master/DEV/screenshots/screenshot1_small.png)
![Image](https://raw.githubusercontent.com/mikepenz/AboutLibraries/master/DEV/screenshots/screenshot2_small.png)


#Wiki
You can find anything you search for in the wiki. (If not open an issue)

[Bring me to the wiki](https://github.com/mikepenz/AboutLibraries/wiki)


##Include in your project
###Using Maven
The AboutLibraries Library is pushed to [Maven Central](http://search.maven.org/#search|ga|1|g%3A%22com.mikepenz.aboutlibraries%22), so you just need to add the following dependency to your `build.gradle`. It seems it is also required to add the support dependencies to the application. If it works without, you should be fine too :).

```javascript
compile('com.mikepenz.aboutlibraries:library:4.5.0@aar') {
	transitive = true
}
```

Further information and how to use it if you can't update to the newest v21 support libs can be found in the [wiki](https://github.com/mikepenz/AboutLibraries/wiki/HOWTO:-Include)


##Usage
You can use this library in a few different ways. You can create your own activity, including a custom style and just use the information, or you can use the built-in Activity or Fragment and just pass the libs you would love to include.
###Activity / Fragment
####Fragment
```java
LibsFragment fragment = new Libs.Builder()
	//Pass the fields of your application to the lib so it can find all external lib information
        .withFields(R.string.class.getFields())
        //(OPTIONAL if all used libraries offer the information, or are autoDetected)
        .withLibraries("AndroidIconify", "ActiveAndroid", "Crouton", "HoloGraphLibrary", "ShowcaseView", "NineOldAndroids")
       	//Display the library version (OPTIONAL)
        .withVersionShown(true)
        //Display the library license (OPTIONAL)
        .withLicenseShown(true)
        //get the fragment
        .fragment();
```
####Activity
#####Code:
```java
new Libs.Builder()
	//Pass the fields of your application to the lib so it can find all external lib information
        .withFields(R.string.class.getFields())
        //(OPTIONAL if all used libraries offer the information, or are autoDetected)
        .withLibraries("crouton, actionbarsherlock", "showcaseview")
        //Display the library version (OPTIONAL)
        .withVersionShown(true)
        //Display the library license (OPTIONAL)
        .withLicenseShown(true)
        //Set a title (OPTIONAL)
        .withActivityTitle
        //Pass your theme (OPTIONAL)
        .withActivityTheme(R.style.AppTheme)
        //start the activity
        .start(this);
```

##Small extra
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
```java
	.withAboutIconShown(true)
	.withAboutVersionShown(true)
	.withAboutDescription("This is a small sample which can be set in the about my app description file.<br /><b>You can style this with html markup :D</b>")

```

##Contribute
You can contribute by creating a information file for a new library, and open a pull-request at the creators Git repository. If he doesn't include the information file in his repo, or if the library isn't maintained anymore you can create a pull-request here. Find more information in the wiki [Create a definition file](https://github.com/mikepenz/AboutLibraries/wiki/HOWTODEV:-Include-into-AboutLibraries)


##Already in use in following apps
(feel free to send me new projects)

* [Numbers](https://play.google.com/store/apps/details?id=com.tundem.numbersreloaded.free)
* [MegaYatzy](https://play.google.com/store/apps/details?id=com.tundem.yatzyTJ)



#Developed By

* Mike Penz - http://mikepenz.com - <mikepenz@gmail.com>


#License

    Copyright 2014 Mike Penz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
