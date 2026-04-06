# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

AboutLibraries is a **Kotlin Multiplatform (KMP)** library that automatically collects dependency and license information from Gradle projects at build time (via a Gradle plugin) and provides Compose/View-based UI to display it at runtime.

The Gradle plugin lives in a **separate Gradle build** under `plugin-build/`. The main project and `plugin-build/` are independent Gradle projects — commands for one do not apply to the other.

## Common Commands

### Main Project
```bash
./gradlew build                                          # Build all modules
./gradlew lintDebug                                      # Lint Android modules
./gradlew apiCheck                                       # Binary compatibility validation
./gradlew :sample:android:assembleDebug                  # Build Android sample
./gradlew :sample:android:validateDebugScreenshotTest    # Run Paparazzi screenshot tests
./gradlew :sample:android:updateDebugScreenshotTest      # Update screenshot baselines
./gradlew :sample:desktop:run                            # Run desktop sample
./gradlew :sample:web:wasmJsBrowserDevelopmentRun        # Run WASM sample
```

### Gradle Plugin (run from `plugin-build/`)
```bash
cd plugin-build
./gradlew build                        # Build the plugin
./gradlew test                         # Run all plugin tests
./gradlew test --tests FunctionalTest  # Run a single test class
./gradlew plugin:lint                  # Lint the plugin
```

### Core Module Tests
```bash
./gradlew :aboutlibraries-core:jvmTest  # Run JVM tests for core module
```

### Generating Library Definitions (for sample/app)
```bash
./gradlew :app:exportLibraryDefinitions
./gradlew :sample:android:exportLibraryDefinitionsDebug
./gradlew :sample:shared:exportLibraryDefinitions
```

## Architecture

### Module Structure

```
aboutlibraries-core/          # KMP data models + JSON parsing (all platforms)
aboutlibraries-compose/       # Base Compose UI (no Material dependency)
aboutlibraries-compose-m2/    # Material 2 Compose UI
aboutlibraries-compose-m3/    # Material 3 Compose UI
aboutlibraries-compose-wear-m3/ # Wear OS Material 3 (Android-only)
aboutlibraries/               # DEPRECATED: View-based UI (FastAdapter/RecyclerView)
app/                          # Full showcase Android app
sample/shared/                # KMP shared sample code
sample/android/               # Android sample (has Paparazzi screenshot tests)
sample/desktop/               # JVM desktop Compose sample
sample/web/                   # WebAssembly Compose sample
plugin-build/plugin/          # Gradle plugin (separate Gradle project)
config/                       # Sample library/license JSON config overrides
```

### How It Works

1. **Build time**: The Gradle plugin (`com.mikepenz.aboutlibraries.plugin` or `.android` variant) scans project dependencies, reads POM files, and generates a JSON file (`aboutlibraries.json`) that is bundled into the app's assets/resources.

2. **Runtime**: `aboutlibraries-core` reads and parses that JSON to produce `Libs` data objects. The Compose modules provide `LibrariesContainer` and related composables to display the data.

### KMP Targets

- **Android** (API 24+)
- **JVM** (desktop)
- **Native** (iOS, Linux, macOS)
- **JS** (browser)
- **WebAssembly** (experimental)

### Plugin Architecture

The plugin provides two Gradle plugins:
- `com.mikepenz.aboutlibraries.plugin` — Manual task-based approach
- `com.mikepenz.aboutlibraries.plugin.android` — Auto-hooks into Android build variants

Plugin tests use **JUnit 5 + Gradle TestKit** (`GradleRunner`). The plugin is built with Java 11 toolchain.

### Compose UI Pattern

All Compose modules follow the same pattern: `aboutlibraries-compose` provides the core composables with no Material dependency, and the `-m2`/`-m3` modules add Material-styled wrappers. Downstream users pick the variant matching their Material version.

## Key Technologies

- **Kotlin 2.2**, **AGP 9.0+**, **Compose 1.10.x**
- **Kotlin Serialization** — JSON parsing in core module
- **Paparazzi** — Screenshot testing in `sample/android`
- **Gradle TestKit** — Functional plugin tests
- **Binary Compatibility Validator** — `apiCheck` enforces public API stability

## Version & Publishing

- Published to **Maven Central** as `com.mikepenz:aboutlibraries-*`
- Gradle plugin published to **Gradle Plugin Portal**
- Versions managed via external version catalog (`com.mikepenz:version-catalog`)
- Convention plugins from the same catalog handle shared build configuration (`com.mikepenz.convention.*`)

## Commit Conventions

All commits must follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/):

```
<type>(<scope>): <short description>

[optional body]

[optional footer(s)]
```

Common types: `feat`, `fix`, `chore`, `refactor`, `docs`, `test`, `ci`, `perf`, `build`.

### Dependency Update Commits

Dependency update commits must be **verbose** — every changed dependency must be listed explicitly with its old and new version. Vague messages like `chore: bump dependencies` are not acceptable.

Format:
```
chore(deps): update dependencies

- <dependency-name> <old-version> -> <new-version>

via <catalog-artifact> <old-version> -> <new-version>:
- <dependency-name> <old-version> -> <new-version>
```

## Dependency Management

This project uses two sources for dependency versions:

1. **`gradle/libs.versions.toml`** — project-local versions (coil, ktor, androidx, etc.)
2. **`com.mikepenz:version-catalog`** in `settings.gradle.kts` — shared catalog from https://github.com/mikepenz/convention

When the version catalog is bumped in `settings.gradle.kts`, always check https://github.com/mikepenz/convention to identify every transitive dependency version change between the old and new catalog versions. Include them all in the commit message under a `via com.mikepenz:version-catalog <old> -> <new>:` section.
