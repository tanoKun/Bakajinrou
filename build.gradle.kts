group = "com.github.tanokun"

val projectVersion = "1.3.0"

allprojects {
    version = projectVersion
}

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
