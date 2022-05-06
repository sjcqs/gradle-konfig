enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "konfig"
include("konfig")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}
