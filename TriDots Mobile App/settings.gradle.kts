pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}


dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://www.jitpack.io")
    }
}


rootProject.name = "TriDots"
include(":app")