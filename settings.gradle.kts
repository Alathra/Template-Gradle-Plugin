pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0" // allow automatic download of JDKs 
}

rootProject.name = "ExamplePlugin"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")