#AboutLibraries

AboutLibraries is a library to offer you all the information you need of your libraries!

Most modern apps feature an "Used Library"-Section and for this some information of those libs is required. As it gets annoying to copy those strings always to your app I've developed this small helper library to provide the required information.

######Note:
The description files should be included in the specific libraries (as far as possible) so the AboutLibraries library can take care about which libs to show. If a library contains the description file, the lib will auto-detect this file and will auto-show this library. You can fien more details in the *Contribute* section of this README.


##Include in your project
###Using Maven
The AboutLibraries Library is pushed to [Maven Central], so you just need to add the following dependency to your `build.gradle`.

```javascript
dependencies {
	compile 'com.tundem.aboutlibraries:library:1.5.0@aar'
}
```

The project is deployed to the snapshot repository so you need to add the following maven repo if you haven't already:

```javascript
maven { 
    url 'https://oss.sonatype.org/content/repositories/snapshots/' 
}
```

##Usage
You can use this library in a few different ways. You can create your own activity, including a custom style and just use the information, or you can use the built in Activity or Fragment and just pass the libs you would love to include.
###Activity / Fragment
####Fragment
```java
Bundle bundle = new Bundle();
//Pass the fields of your application to the lib so it can find all external lib information
bundle.putStringArray(Libs.BUNDLE_FIELDS, Libs.toStringArray(R.string.class.getFields()));
//Define the libs you want (only those who don't include the information, and are managed by the AboutLibraries library) (OPTIONAL if all used libraries offer the information)
bundle.putStringArray(Libs.BUNDLE_LIBS, new String[]{"AndroidIconify", "ActiveAndroid", "FButton", "Crouton", "HoloGraphLibrary", "ShowcaseView", "NineOldAndroids", "AndroidViewpagerIndicator"});

//Display the library version (OPTIONAL)
bundle.putBoolean(Libs.BUNDLE_VERSION, true);
//Display the library license (OPTIONAL
bundle.putBoolean(Libs.BUNDLE_LICENSE, true);

//Create a new Fragment (you can do this whereever you want
Fragment fragment = new LibsFragment();
//Set the arguments
fragment.setArguments(bundle);
```
####Activity
#####Code:
```java
//Create an intent with context and the Activity class
Intent i = new Intent(getApplicationContext(), LibsActivity.class);
//Pass the fields of your application to the lib so it can find all external lib information
i.putExtra(Libs.BUNDLE_FIELDS, Libs.toStringArray(R.string.class.getFields()));
//Define the libs you want (only those who don't include the information, and are managed by the AboutLibraries library) (OPTIONAL if all used libraries offer the information)
i.putExtra(Libs.BUNDLE_LIBS, new String[]{"crouton", "actionbarsherlock", "showcaseview"});

//Display the library version (OPTIONAL)
i.putExtra(Libs.BUNDLE_VERSION, true);
//Display the library license (OPTIONAL
i.putExtra(Libs.BUNDLE_LICENSE, true);

//Pass your theme (OPTIONAL)
i.putExtra(Libs.BUNDLE_THEME, android.R.style.Theme_Holo);
//Pass a custom Accentcolor (OPTIONAL)
i.putExtra(Libs.BUNDLE_ACCENTCOLOR, "#3396E5");
//Pass the information if it should use the Translucent decor (OPTIONAL) -> requires ACCENTCOLOR
i.putExtra(Libs.BUNDLE_TRANSLUCENTDECOR, true);

//start the activity
startActivity(i);
```
#####Xml:
```xml
<!-- Don't forget to define the Activity in the manifest -->
<activity
	android:name="com.tundem.aboutlibraries.ui.LibsActivity">
</activity>
```

###Custom
------------
Use the Library class and build your view on-your-own

The prefered method to get a Libs instance is by passing the string-field-array
```java
Libs libs = Libs.getInstance(getActivity(), R.string.class.getFields());
```

Now you can use the instance to get the information
```java
libs.getLibrary("ActionBarSherlock")
```
done.


##Including in your library
Create a {yourlib}_strings.xml in your values folder and define the required information.

You can also find a sample in one of my other open source projects here: [LINK](https://github.com/mikepenz/AnimatedGridView/blob/master/library/src/main/res/values/info_strings.xml)

###Sample *_strings.xml (ActionBarSherlock)
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <string name="define_ActionBarSherlock"></string>
    <string name="libray_ActionBarSherlock_author">Jake Wharton</string>
    <string name="libray_ActionBarSherlock_authorWebsite">http://jakewharton.com/</string>
    <string name="libray_ActionBarSherlock_libraryName">ActionBarSherlock</string>
    <string name="libray_ActionBarSherlock_libraryDescription">ActionBarSherlock is an standalone library designed to facilitate the use of the action bar design pattern across all versions of Android through a single API.</string>
    <string name="libray_ActionBarSherlock_libraryVersion">4.3.1</string>
    <string name="libray_ActionBarSherlock_libraryWebsite">http://actionbarsherlock.com/</string>
    <string name="libray_ActionBarSherlock_licenseVersion">Apache Version 2.0</string>
    <string name="libray_ActionBarSherlock_licenseLink">www.apache.org/licenses/LICENSE-2.0.html</string>
    <string name="libray_ActionBarSherlock_licenseContent">Copyright 2012 Jake Wharton

		Licensed under the Apache License, Version 2.0 (the "License");
		you may not use this file except in compliance with the License.
		You may obtain a copy of the License at
		
		   http://www.apache.org/licenses/LICENSE-2.0
		
		Unless required by applicable law or agreed to in writing, software
		distributed under the License is distributed on an "AS IS" BASIS,
		WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
		See the License for the specific language governing permissions and
		limitations under the License.</string>
    <string name="libray_ActionBarSherlock_isOpenSource">true</string>
    <string name="libray_ActionBarSherlock_repositoryLink">https://github.com/JakeWharton/ActionBarSherlock</string>

</resources>
```


##Contribute
You can contribute by creating an informationfile for a new library, and make a pull-request at the creators git repository. If he doesn't include the information file in his repo, or if the library isn't maintained anymore you can create a pull-request here.

Please remind that if you make a pull-request here, that you have to change the *define_* string to *define_int_*. It is also very important that the *_strings.xml* has an unique filename (it should be named *{libraryidentifier}_strings.xml*


##Contributors
Moddus



#Developed By

* Mike Penz - http://mikepenz.com - <penz@tundem.com>


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
