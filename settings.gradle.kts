pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://europe-west3-maven.pkg.dev/talsec-artifact-repository/freerasp") }
    }
}

rootProject.name = "Free-RASP-KMP"
include(":library")
