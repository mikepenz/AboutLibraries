# Deprecated APIs

## (Legacy) UI-module (View-based)

> [!WARNING]
> **Deprecated:** The legacy View-based UI module (`com.mikepenz:aboutlibraries`) is deprecated and will receive limited support.
> Please migrate to the [Compose UI module](#ui-module-compose).
> For embedding Compose in Views, consider using [ComposeView](https://developer.android.com/develop/ui/compose/migrate/interoperability-apis/compose-in-views).
> While newer Gradle plugin versions *might* be compatible with older UI modules regarding the data format, migration is strongly recommended.

```gradle
// build.gradle.kts
// Recommended: Using version catalog
implementation(libs.aboutlibraries.view)

// Alternative: Direct dependency declaration
// implementation("com.mikepenz:aboutlibraries:${latestAboutLibsRelease}")
```

### Usage

Use this library in a few different ways. Create a custom activity, including a custom style or just
use its generated information. Or simply use the built-in Activity or Fragment and just pass the
libs to include.

> **Note**: The new version requires the new Material3 theme as base.

#### Activity

```kotlin
LibsBuilder()
    .start(this) // start the activity
```

The activity uses a toolbar, which requires the appropriate theme.
See [Style the AboutLibraries](#style-the-aboutlibraries-%EF%B8%8F) for more details

#### Fragment

```kotlin
val fragment = LibsBuilder()
    .supportFragment()
```

#### About this App UI

The `AboutLibraries` library also offers the ability to create an `About this app` screen.
Add the following .xml file (or just the strings - the key must be the same) to the project.

```xml

<resources>
    <string name="aboutLibraries_description_showIcon">true</string>
    <string name="aboutLibraries_description_showVersion">true</string>
    <string name="aboutLibraries_description_text">Place the description here :D</string>
</resources>
```

or use the builder and add following:

```kotlin
.withAboutIconShown(true)
    .withAboutVersionShown(true)
    .withAboutDescription("This is a small sample which can be set in the about my app description file.<br /><b>Style this with html markup :D</b>")
```

#### Style the AboutLibraries 🖌️

Create a custom style for the AboutLibraries UI.

```xml
// define a custom style
<style name="CustomAboutLibrariesStyle" parent="">
    <!-- AboutLibraries specific values -->
    <item name="aboutLibrariesCardBackground">?cardBackgroundColor</item>
    <item name="aboutLibrariesDescriptionTitle">?android:textColorPrimary</item>
    <item name="aboutLibrariesDescriptionText">?android:textColorSecondary</item>
    <item name="aboutLibrariesDescriptionDivider">@color/opensource_divider</item>
    <item name="aboutLibrariesOpenSourceTitle">?android:textColorPrimary</item>
    <item name="aboutLibrariesOpenSourceText">?android:textColorSecondary</item>
    <item name="aboutLibrariesSpecialButtonText">?android:textColorPrimary</item>
    <item name="aboutLibrariesOpenSourceDivider">@color/opensource_divider</item>
</style>

        // define the custom styles for the theme

<style name="SampleApp" parent="Theme.MaterialComponents.Light.NoActionBar">
...
<item name="aboutLibrariesStyle">@style/CustomAboutLibrariesStyle</item>
...
</style>
```
