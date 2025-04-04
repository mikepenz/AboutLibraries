### Upgrade Notes

#### v12.0.0

**Dependency Upgrade**: Kotlin 2.1.20
**Breaking Changes**: The `Gradle Plugin` and `Compose UI` library have undergone significant changes, including multiple breaking API changes.
**Breaking Change**: The `Gradle Plugin` has moved to a `Property` based configuration schema with nested configs for better organization.
**Breaking Change**: The `Gradle Plugin`s configuration options which were `Array` or `List` types have been changed to `Set` types.
**Breaking Change**: The `Gradle Plugin` now only has a singular `outputPath` property, to specify where the generated file should be written to.
**Behaviour Change**: The `Gradle Plugin` will now do parts of the setup during configuration time to improve configuration cache compatibility.
**Breaking Change**: The Compose UI APIs were overhauled to improve the API and reduce the number of parameters, and make it more flexible for the future.

API changes to simplify the expansion and configuration of the compose UI.

v11.x.y

```kotlin
LibrariesContainer(
    itemContentPadding = PaddingValues(16.dp),
    itemSpacing = 2.dp
)
```

v12.x.y

```kotlin
LibrariesContainer(
    padding = LibraryDefaults.libraryPadding(contentPadding = PaddingValues(16.dp)),
    dimensions = LibraryDefaults.libraryDimensions(itemSpacing = 2.dp),
)
```

#### v10.x.y

* v10 includes a major rewrite of the `Gradle Plugin`. The old `xml` string resource format
  definitions are no longer supported.
    * All meta information is now written to a single json file
    * Old `enchant` config files are no longer supported
* The UI module dropped a major amount of configurations to lower complexity.
* TODO

#### v7.0.1

* in case your project has custom licenses, check out the new feature to allow the license
  description be included as raw file. This helps to solve the potential STRING_TOO_LARGE issue.

#### v7.x.y

* Upgraded the library to be in `kotlin` dependencies.
* Update `FastAdapter` to v4 - If you use this library too, please check out
  the [migration guide](https://github.com/mikepenz/FastAdapter/blob/develop/MIGRATION.md)
* Update `Android-Iconics` to v4 - If you use this library too, please check out
  the [migration guide](https://github.com/mikepenz/Android-Iconics/blob/develop/MIGRATION.md)

#### v6.2.x

* Upgraded the library to use `androidX` dependencies. This means your project will need to depend
  on `androidX` dependencies too. If you still use appcompat please consider using a version older
  than v6.2.x.
* Further details about migrating to androidX and a overview can be found on the official
  docs. https://developer.android.com/topic/libraries/support-library/refactor

#### v6.0.0

**IMPORTANT IF YOU USE THE FASTADAPTER OR MATERIALDRAWER**

* You have to update your FastAdapter dependency to v3.0.0 with this release
* See the MIGRATION information of the
  FastAdapter https://github.com/mikepenz/FastAdapter/blob/develop/MIGRATION.md

#### v5.9.7

**IMPORTANT IF YOU USE THE Android-Iconics**

* You have to update your Android-Iconics dependency to v2.9.0 with this release
* See the MIGRATION information of the
  Android-Iconics https://github.com/mikepenz/Android-Iconics/blob/develop/MIGRATION.md#290

#### v5.9.5

**IMPORTANT IF YOU USE THE FASTADAPTER OR MATERIALDRAWER**

* You have to update your FastAdapter dependency to v2.5.0 with this release
* See the MIGRATION information of the
  FastAdapter https://github.com/mikepenz/FastAdapter/blob/develop/MIGRATION.md

#### v5.9.0

**IMPORTANT IF YOU USE THE FASTADAPTER OR MATERIALDRAWER**

* You have to update your FastAdapter dependency to v2.1.0 with this release
* See the MIGRATION information of the
  FastAdapter https://github.com/mikepenz/FastAdapter/blob/develop/MIGRATION.md

#### v5.8.5

**IMPORTANT IF YOU USE THE FASTADAPTER**

* You have to update your FastAdapter dependency to v2.0.0 with this release
* See the MIGRATION information of the
  FastAdapter https://github.com/mikepenz/FastAdapter/blob/develop/MIGRATION.md

#### v5.8.1

**IMPORTANT IF YOU USE THE FASTADAPTER**

* This release brings a breaking interface change. Your items now have to implement
  `bindView(ViewHolder holder, List payloads)` instead of `bindView(VH holder)`.
* The additional payload can be used to implement a more performant view updating when only parts of
  the item have changed. Please also refer to the `DiffUtils` which may provide the payload.

#### v5.8.0

* **Dropping support for API < 14. New MinSdkVersion is 14**

#### v5.6.1 -> v5.6.2

* Change `void onLibTaskFinished()` to `void onLibTaskFinished(FastItemAdapter fastItemAdapter)`

#### v5.3.0 -> v5.3.1

* renamed `withAnimations()` to `withSlideInAnimation()` and change the default to `false`
* now use the `DefaultItemAnimator` to animate the displaying of the elements
* you can now define a different `ItemAnimator` via
  `LibsConfiguration.getInstance().setItemAnimator()`

#### v5.2.6 -> v5.3.0

* change `.fragment()` to `.supportFragment()` and `LibsFragment` to `LibsSupportFragment`

**INFO**

* moved logic of the fragment to the new `LibsFragmentCompat` class
* create new `LibsSupportFragment` which extends the `android.support.v4.app.Fragment`
* change the `LibsFragment` which now extends the `android.app.Fragment`
* add new method `.supportFragment()` which returns `android.support.v4.app.Fragment`
* change `.fragment()` which now returns `android.app.Fragment`
