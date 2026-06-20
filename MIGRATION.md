### Upgrade Notes

#### v15.0.0

- **Breaking Change**: The legacy View-based UI module (`com.mikepenz:aboutlibraries`) has been removed.
    - Migrate to the Compose UI modules: `com.mikepenz:aboutlibraries-compose`, `com.mikepenz:aboutlibraries-compose-m2` (M2) or `com.mikepenz:aboutlibraries-compose-m3` (M3).
    - Remove `LibsBuilder`, `LibsActivity`, `LibsFragment`, and `LibsConfiguration` usages from your code.
    - See the [README](README.md) for the Compose UI setup and usage.
- **Breaking Change**: Upgrade to Compose 1.11.x which drops support for `macosX64` and `iosX64` targets. If you were using these targets, you will need to migrate to the new
  `macosArm64` and `iosArm64` targets respectively.
- **Breaking Change**: The Android Gradle Plugin (AGP) version 8.13.0 or greater is now required when applying the `.android` plugin.
- **Breaking Change (Gradle Plugin)**: Removed previously deprecated configuration API.
    - `AndroidConfig` and the `android { }` block on `AboutLibrariesExtension` were removed (`registerAndroidTasks` was already a no-op). Apply the
      `com.mikepenz.aboutlibraries.plugin.android` plugin to auto-hook the Android build instead.
    - `outputFileName` on `ExportConfig` was removed — use `outputFile` (full path) instead.
    - The `aboutLibraries.outputPath` Gradle property was removed — use `aboutLibraries.outputFile` instead.
- **Breaking Change (Compose UI)**: The Compose UI received a major overhaul (variant system, accent theming, search). The public `LibrariesContainer` API changed;
  see [Compose UI API migration](#compose-ui-api-migration-v14x--v15) below.
    - `onLibraryClick` now returns `Boolean` instead of `Unit`, and the new `onActionClick` callback (replacing `onFundingClick`) returns `Boolean`. Return `true` to consume the
      event and suppress the default handling (URI open / license dialog / sheet).
    - The `show*` boolean parameters (`showAuthor`, `showDescription`, `showVersion`, `showLicenseBadges`, `showFundingBadges`) are replaced by a single `badges: LibraryBadges`.
    - The styling parameters (`typography`, `padding`, `dimensions`, `textStyles`, `shapes`) and `libraryModifier`/`onFundingClick` were removed from the M2/M3
      `LibrariesContainer`. Styling now flows through the variant token system (see below).
    - The deprecated `rememberLibraries` overloads and the deprecated `LibrariesContainer` wrapper files (deprecated in v12.2.0) were removed.

##### Compose UI API migration (v14.x → v15)

The styling knobs that used to be individual `LibrariesContainer` parameters are now grouped into the variant token system. Where each one moved:

| Removed v14 parameter                                                                        | v15 replacement                                                                                                                                                                |
|----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `showAuthor` / `showDescription` / `showVersion` / `showLicenseBadges` / `showFundingBadges` | `badges = LibraryBadges(author, description, version, license, funding)`                                                                                                       |
| `typography: Typography`                                                                     | Derived from `MaterialTheme.typography` inside the style; override via `textStyles` on `LibraryDefaults.librariesStyle(...)`                                                   |
| `padding: LibraryPadding`                                                                    | `LibrariesStyle.padding` (`VariantPadding`) via `LibraryDefaults.defaultVariantPadding(...)`                                                                                   |
| `dimensions: LibraryDimensions`                                                              | `LibrariesStyle.dimensions` (`VariantDimensions`) via `LibraryDefaults.defaultVariantDimensions(...)`                                                                          |
| `textStyles: LibraryTextStyles`                                                              | `LibrariesStyle.textStyles` (`VariantTextStyles`) via `LibraryDefaults.m3VariantTextStyles(...)` / `defaultVariantTextStyles(...)`                                             |
| `shapes: LibraryShapes`                                                                      | `LibrariesStyle.shapes` (`VariantShapes`) via `LibraryDefaults.defaultVariantShapes(...)`                                                                                      |
| `colors: LibraryColors`                                                                      | Kept (chips + license dialog). Header / row / action chrome colors moved to `variantColors: VariantColors` via `LibraryDefaults.m3VariantColors(...)` / `m2VariantColors(...)` |
| `libraryModifier`, `onFundingClick`, per-slot lambdas (`name`/`version`/`author`/…)          | Replaced by `variant` / `actionMode` / `detailMode` enums and the `libraryRow` slot                                                                                            |

> Note: `LibraryColors` and the legacy `LibraryDefaults.libraryColors()` / `libraryPadding()` / `libraryDimensions()` / `libraryTextStyles()` / `libraryShapes()` factories still
> exist — only the `LibrariesContainer` parameters changed.

**Before (v14.x):**

```kotlin
LibrariesContainer(
    libraries = libs,
    showAuthor = true,
    showVersion = true,
    showLicenseBadges = true,
    showFundingBadges = false,
    colors = LibraryDefaults.libraryColors(),
    padding = LibraryDefaults.libraryPadding(),
    dimensions = LibraryDefaults.libraryDimensions(),
    textStyles = LibraryDefaults.libraryTextStyles(),
    shapes = LibraryDefaults.libraryShapes(),
    onLibraryClick = { library -> /* handle */ },
)
```

**After (v15):**

```kotlin
LibrariesContainer(
    libraries = libs,
    badges = LibraryBadges(
        author = true,
        version = true,
        license = true,
        funding = false,
        description = false,
    ),
    colors = LibraryDefaults.libraryColors(),          // chips + license dialog (unchanged)
    variantColors = LibraryDefaults.m3VariantColors(), // header / row / action chrome
    variant = LibrariesVariant.Traditional,            // or .Refined
    actionMode = LibraryActionMode.Chips,              // or .Icons / .Links
    detailMode = LibraryDetailMode.Inline,             // or .None / .Sheet
    onLibraryClick = { library -> false },             // return true to consume the click
    onActionClick = { library, kind -> false },        // return true to suppress default open
)
```

**Customizing padding / dimensions / text styles / shapes:** the M2/M3 `LibrariesContainer` builds its `LibrariesStyle` internally and only exposes `variantColors`. For full
control over the remaining tokens, use the Material-agnostic `Libraries` composable from `aboutlibraries-compose` and pass a `LibrariesStyle`:

```kotlin
import com.mikepenz.aboutlibraries.ui.compose.variant.Libraries

Libraries(
    libraries = libs,
    style = LibraryDefaults.m3LibrariesStyle(compact = false),   // shortcut, or build granularly:
    // style = LibraryDefaults.librariesStyle(
    //     colors = LibraryDefaults.m3VariantColors(),
    //     padding = LibraryDefaults.defaultVariantPadding(headerPadding = PaddingValues(20.dp)),
    //     dimensions = LibraryDefaults.defaultVariantDimensions(headerIconSize = 28.dp),
    //     textStyles = LibraryDefaults.m3VariantTextStyles(),
    //     shapes = LibraryDefaults.defaultVariantShapes(),
    // ),
)
```

#### v14.0.0

- **Breaking Change**: The `core` plugin no longer depends on the `kotlinx.immutable` collections library.
    - Collections are marked as stable via the stability config file instead: https://github.com/mikepenz/AboutLibraries/pull/1267
- **Breaking Change**: The already deprecated `generateLibraryDefinitions*` tasks are now removed
- **Breaking Change**: The plugin will now only work for projects that use AGP 7 or newer, with the new variants API via `AndroidComponentsExtension` available
- **Breaking Change**: Due to Paparazzi requiring Java 21 - This project is now also compiled with Java 21

#### v13.2.0

- **Breaking Change**: Some underlying APIs start to require API 23 instead of 21.
    - `org.jetbrains.compose.components.resources`
- **Dependency Upgrade**: Kotlin 2.3.0
- **Dependency Upgrade**: Compose 1.10.x

#### v13.1.0

- **Behaviour Change**: The `Gradle Plugin` now by default enables MERGING duplicates with EXACT matches. In prior releases, duplicates would be kept.

```kotlin
// To enable the prior behavior, you can simply configure this in your build script
aboutLibraries {
    library {
        duplicationMode = DuplicateMode.KEEP
        duplicationRule = DuplicateRule.SIMPLE
    }
}

```

#### v13.0.0

- **Breaking Change**: Deprecated APIs from v12.x.y were removed
- **Breaking Change**: The `Gradle Plugin` was split into 2. The main plugin that is registering all the manual tasks, and an Android specific one automatically registering the
  Android auto
  generation task.
    - For most projects the manual tasks are recommended. Only if you require or want to use the generation as part of the android build, use the `.android` plugin.
    - The main plugin (`com.mikepenz.aboutlibraries.plugin`) provides tasks like `exportLibraryDefinitions` that need to be manually executed
    - The Android plugin (`com.mikepenz.aboutlibraries.plugin.android`) automatically hooks into the Android build process

```kotlin
// To use the Android auto registering plugin - add the following to your module:
id("com.mikepenz.aboutlibraries.plugin.android")
```

- **Breaking Change**: The `AndroidConfig` class and its `registerAndroidTasks` property were removed, replaced by the Android-specific plugin
- **Breaking Change**: Reworked the `LibraryColors` interface to be more descriptive and more flexible
    - Renamed `backgroundColor` to `libraryBackgroundColor`
    - Renamed `contentColor` to `libraryContentColor`
    - Added new background, content color variants for the dialog

#### v12.2.0

- **Breaking Change**: Renamed `nameTextStyles` in `libraryTextStyles`/`LibraryTextStyles` to `nameTextStyle` (to align with other styles).
- **Breaking Change**: Introduced new `libraryPadding()` default function to replace the existing one. This moves to use a new class to hold chip paddings.
- **Breaking Change**: Wrappers around the `LibrariesContainer` were deprecated in favor of `remember*` functions. To be removed in release `13.x.y`.
- **Breaking Change**: The `LicenseDialogBody` now also accepts the `Modifier` to include padding in text instead of scrollable container
- **Deprecate**: Deprecate series of APIs to cleanup and simplify API surface, and simplify maintenance.
- **Note**: Updated README to suggest new non deprecated API for using `LibrariesContainer` on Android.

#### v12.0.0

- **Dependency Upgrade**: Kotlin 2.1.20
- **Breaking Change**: The `Gradle Plugin` and `Compose UI` library have undergone significant changes, including multiple breaking API changes.
- **Breaking Change**: The `Gradle Plugin` has moved to a `Property` based configuration schema with nested configs for better organization.
- **Breaking Change**: The `Gradle Plugin's` configuration options which were `Array` or `List` types have been changed to `Set` types.
- **Breaking Change**: The `Gradle Plugin` now only has a singular `outputPath` property, to specify where the generated file should be written to.
- **Behaviour Change**: The `Gradle Plugin` will now do parts of the setup during configuration time to improve configuration cache compatibility.
- **Breaking Change**: The Compose UI APIs were overhauled to improve the API and reduce the number of parameters, and make it more flexible for the future.

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
