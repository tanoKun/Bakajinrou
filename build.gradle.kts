group = "com.github.tanokun"
version = "1.0.0-SNAPSHOT"

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
}

kotlin {
    jvmToolchain(22)
}

repositories {
    mavenCentral()
}