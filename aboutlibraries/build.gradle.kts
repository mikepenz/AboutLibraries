plugins {
    id("com.mikepenz.convention.android-library")
    id("com.mikepenz.convention.publishing")
}

android {
    namespace = "com.mikepenz.aboutlibraries"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(project(":aboutlibraries-core"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.androidx.cardView)
    implementation(libs.androidx.recyclerView)
    implementation(libs.google.material)

    // Coroutines
    implementation(baseLibs.kotlinx.coroutines.core)

    // add the constraintLayout used to create the items and headers
    implementation(libs.androidx.constraintLayout)

    // used to fill the RecyclerView with the items
    // and provides single and multi selection, expandable items
    // https://github.com/mikepenz/FastAdapter
    implementation(libs.fastAdapter.core)

    // navigation
    implementation(libs.androidx.navigation)
}