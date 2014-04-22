AboutLibraries
==============
http://mikepenz.github.io/AboutLibraries

AboutLibraries is a library to offer some information of libraries.

Most modern apps feature an "Used Library"-Section and for this some information of those libs is required. As it gets annoying to copy those strings always to your app I've developed this small helper library to provide the required information.

All libraries are provided as *_strings.xml file in this library project. The project will auto-initialize those wihtout any extra effort.

If you would love to add a new library just create a {libraryName}_strings.xml file and add the specific information.

After that you can sipmly use the new information in all your projects!


Sample actionbarsherlock_strings.xml
============

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

* Mike Penz - http://penz.tundem.com - <mikepenz@gmail.com>



License
=======

    Copyright 2013 Mike Penz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
