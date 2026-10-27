// build.gradle.kts (Project-level)
plugins {
    id("com.android.application") version "8.6.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.5.0" apply false
}