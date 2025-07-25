pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "Bakajinrou"

include("Bakajinrou-API")
include("Bakajinrou-Game")
include("Bakajinrou-Plugin")
