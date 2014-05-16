AboutLibraries
==============
http://mikepenz.github.io/AboutLibraries

AboutLibraries is a library to offer you all the information you need of your libraries!

Most modern apps feature an "Used Library"-Section and for this some information of those libs is required. As it gets annoying to copy those strings always to your app I've developed this small helper library to provide the required information.

All libraries are provided as *_strings.xml file in this library project. The project will auto-initialize those wihtout any extra effort.

It is also possible that devs include a *_strings.xml in their project (if they follow the specific definition) and you can use those information too!

If you would love to add a new library just create a {libraryName}_strings.xml file and add the specific information.
You can do a pull-request for this project, or better do the pull request for the library project.

After that you can sipmly use the new information in all your projects!


Sample *_strings.xml (ActionBarSherlock)
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

- To use this information get an Instance of the Libs class, and call
```xml
Libs.getInstance(context).getLibrary("ActionBarSherlock")
```
- with "ActionBarSherlock" -> define_*ActionBarSherlock*

- If you use libs which include the *_strings.xml definition in their project you have just add an extra line. You have to first initialize the Libs instance with the strings of your project, because otherwhise it wouldn't find the info file.
```xml
Libs.getInstance(getActivity(), R.string.class.getFields());
```
- done.

Developed By
============

* Mike Penz - http://mikepenz.com - <mikepenz@gmail.com>


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
