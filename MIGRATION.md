###Upgrade Notes

#### v5.2.6 -> 5.3.0
* change `.fragment()` to `.supportFragment()` and `LibsFragment` to `LibsSupportFragment`

**INFO**
* moved logic of the fragment to the new `LibsFragmentCompat` class
 * create new `LibsSupportFragment` which extends the `android.support.v4.app.Fragment`
 * change the `LibsFragment` which now extends the `android.app.Fragment`
* add new method `.supportFragment()` which returns `android.support.v4.app.Fragment`
* change `.fragment()` which now returns `android.app.Fragment`
