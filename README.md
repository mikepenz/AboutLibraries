AboutLibraries
==============

AboutLibraries is a library to offer you all the information you need of your libraries!

Most modern apps feature an "Used Library"-Section and for this some information of those libs is required. As it gets annoying to copy those strings always to your app I've developed this small helper library to provide the required information.

All libraries are provided as *_strings.xml file in this library project. The project will auto-initialize those wihtout any extra effort.

It is also possible that devs include a *_strings.xml in their project (if they follow the specific definition) and you can use those information too!

If you would love to add a new library just create a {libraryName}_strings.xml file and add the specific information.
You can do a pull-request for this project, or better do the pull request for the library project.

After that you can sipmly use the new information in all your projects!


Including in your project
============
###Using Maven
AnimatedGridView Library is pushed to [Maven Central], so you just need to add the following dependency to your `build.gradle`.

```javascript
dependencies {
	compile 'com.tundem.aboutlibraries:library:1.1.0@aar'
}
```

Usage
-------------------------

The prefered method to get a Libs instance is by passing the string-field-array
```java
Libs libs = Libs.getInstance(getActivity(), R.string.class.getFields());
```

Now you can use the instance to get the information
```java
libs.getLibrary("ActionBarSherlock")
```
done.


Including in your library
============

Create a {yourlib}_strings.xml in your values folder and define the required information.
You can also find a sample in one of my other open source projects here: [LINK](https://github.com/mikepenz/AnimatedGridView/blob/master/library/src/main/res/values/info_strings.xml)

Sample *_strings.xml (ActionBarSherlock)
-------------

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


Developed By
============

* Mike Penz - http://mikepenz.com - <penz@tundem.com>


License
=======

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
