### Upgrade Notes

#### v7.0.1
* in case your project has custom licenses, check out the new feature to allow the license description be included as raw file. This helps to solve the potential STRING_TOO_LARGE issue.

#### v7.x.y
* Upgraded the library to be in `kotlin` dependencies.
* Update `FastAdapter` to v4 - If you use this library too, please check out the [migration guide](https://github.com/mikepenz/FastAdapter/blob/develop/MIGRATION.md)
* Update `Android-Iconics` to v4 - If you use this library too, please check out the [migration guide](https://github.com/mikepenz/Android-Iconics/blob/develop/MIGRATION.md)

#### v6.2.x
* Upgraded the library to use `androidX` dependencies. This means your project will need to depend on `androidX` dependencies too. If you still use appcompat please consider using a version older than v6.2.x. 
* Further details about migrating to androidX and a overview can be found on the official docs. https://developer.android.com/topic/libraries/support-library/refactor

#### v6.0.0
**IMPORTANT IF YOU USE THE FASTADAPTER OR MATERIALDRAWER**
* You have to update your FastAdapter dependency to v3.0.0 with this release
* See the MIGRATION information of the FastAdapter https://github.com/mikepenz/FastAdapter/blob/develop/MIGRATION.md

#### v5.9.7
**IMPORTANT IF YOU USE THE Android-Iconics**
* You have to update your Android-Iconics dependency to v2.9.0 with this release
* See the MIGRATION information of the Android-Iconics https://github.com/mikepenz/Android-Iconics/blob/develop/MIGRATION.md#290

#### v5.9.5
**IMPORTANT IF YOU USE THE FASTADAPTER OR MATERIALDRAWER**
* You have to update your FastAdapter dependency to v2.5.0 with this release
* See the MIGRATION information of the FastAdapter https://github.com/mikepenz/FastAdapter/blob/develop/MIGRATION.md

#### v5.9.0
**IMPORTANT IF YOU USE THE FASTADAPTER OR MATERIALDRAWER**
* You have to update your FastAdapter dependency to v2.1.0 with this release
* See the MIGRATION information of the FastAdapter https://github.com/mikepenz/FastAdapter/blob/develop/MIGRATION.md

#### v5.8.5 
**IMPORTANT IF YOU USE THE FASTADAPTER**
* You have to update your FastAdapter dependency to v2.0.0 with this release
* See the MIGRATION information of the FastAdapter https://github.com/mikepenz/FastAdapter/blob/develop/MIGRATION.md

#### v5.8.1
**IMPORTANT IF YOU USE THE FASTADAPTER**
* This release brings a breaking interface change. Your items now have to implement `bindView(ViewHolder holder, List payloads)` instead of `bindView(VH holder)`. 
 * The additional payload can be used to implement a more performant view updating when only parts of the item have changed. Please also refer to the `DiffUtils` which may provide the payload.

#### v5.8.0
* **Dropping support for API < 14. New MinSdkVersion is 14**

#### v5.6.1 -> v5.6.2
* Change `void onLibTaskFinished()` to `void onLibTaskFinished(FastItemAdapter fastItemAdapter)`

#### v5.3.0 -> v5.3.1
* renamed `withAnimations()` to `withSlideInAnimation()` and change the default to `false`
* now use the `DefaultItemAnimator` to animate the displaying of the elements 
* you can now define a different `ItemAnimator` via `LibsConfiguration.getInstance().setItemAnimator()`

#### v5.2.6 -> v5.3.0
* change `.fragment()` to `.supportFragment()` and `LibsFragment` to `LibsSupportFragment`

**INFO**
* moved logic of the fragment to the new `LibsFragmentCompat` class
 * create new `LibsSupportFragment` which extends the `android.support.v4.app.Fragment`
 * change the `LibsFragment` which now extends the `android.app.Fragment`
* add new method `.supportFragment()` which returns `android.support.v4.app.Fragment`
* change `.fragment()` which now returns `android.app.Fragment`
