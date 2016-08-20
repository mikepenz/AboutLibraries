###Upgrade Notes
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
