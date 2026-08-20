buildscript {
    dependencies {
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin")
    }
}

plugins {
    alias(baseLibs.plugins.conventionPlugin)
    alias(baseLibs.plugins.androidApplication) apply false
    alias(baseLibs.plugins.androidLibrary) apply false
    alias(baseLibs.plugins.androidKmpLibrary) apply false
    alias(baseLibs.plugins.composeMultiplatform) apply false
    alias(baseLibs.plugins.composeCompiler) apply false
    alias(baseLibs.plugins.composeHotreload) apply false
    alias(baseLibs.plugins.kotlinMultiplatform) apply false

    alias(baseLibs.plugins.dokka)
    alias(baseLibs.plugins.aboutLibraries) apply false
    alias(baseLibs.plugins.mavenPublish) apply false
    alias(baseLibs.plugins.binaryCompatiblityValidator) apply false
    alias(baseLibs.plugins.versionCatalogUpdate) apply false
    alias(baseLibs.plugins.stabilityAnalyzer) apply false
    alias(baseLibs.plugins.paparazzi) apply false

    alias(libs.plugins.navSafeArgs) apply false
}

// Pin patched versions of the npm packages the Kotlin/JS toolchain pulls in transitively.
// These are build-time only (webpack dev server, karma) and never ship in the published
// artifacts, but they surface as OpenSSF Scorecard `Vulnerabilities` findings against the
// committed `kotlin-js-store` lockfiles. Every pin stays within the major version already
// resolved, so no consumer sees an API change.
// Regenerate the lockfiles after editing: ./gradlew kotlinUpgradeYarnLock
plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().apply {
        resolution("braces", "3.0.3")
        resolution("cross-spawn", "7.0.5")
        resolution("diff", "5.2.2")
        resolution("engine.io", "6.6.7")
        resolution("express", "4.20.0")
        resolution("flatted", "3.4.2")
        resolution("follow-redirects", "1.16.0")
        resolution("http-proxy-middleware", "2.0.10")
        resolution("js-yaml", "4.3.1")
        resolution("launch-editor", "2.14.1")
        resolution("lodash", "4.18.0")
        resolution("micromatch", "4.0.8")
        resolution("node-forge", "1.4.0")
        resolution("on-headers", "1.1.0")
        resolution("path-to-regexp", "0.1.13")
        resolution("picomatch", "2.3.2")
        resolution("qs", "6.15.3")
        resolution("send", "0.19.0")
        resolution("serve-static", "1.16.0")
        resolution("shell-quote", "1.9.0")
        resolution("socket.io-parser", "4.2.7")
        resolution("tmp", "0.2.7")
        resolution("webpack", "5.104.1")
        resolution("websocket-driver", "0.7.5")

        resolution("ajv", "8.18.0")
        resolution("body-parser", "1.20.6")
        resolution("cookie", "0.7.2")
        resolution("serialize-javascript", "7.0.5")
        resolution("ws", "8.21.0")
    }
}