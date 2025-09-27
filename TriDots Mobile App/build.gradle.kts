// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    // DECLARE the Google Services plugin here at the project level
    id("com.google.gms.google-services") version "4.4.1" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Add the classpath for the Google Services plugin
        classpath("com.google.gms:google-services:4.4.1")
    }
}

// If you are managing dependencies with a libs.versions.toml (version catalog),
// you might have firebase-bom defined there.
// If not, you might explicitly define it in the app-level build.gradle.
// However, the standard practice is to use the BOM.