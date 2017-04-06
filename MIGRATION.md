### Upgrade Notes
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
