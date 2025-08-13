group = "com.github.tanokun"
version = "1.0.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "2.2.0"
}

kotlin {
    jvmToolchain(22)
}

dependencies {
    implementation(project("Bakajinrou-API"))
    implementation(project("Bakajinrou-Game"))
    implementation(project("Bakajinrou-Plugin"))
}

repositories {
    mavenCentral()
}