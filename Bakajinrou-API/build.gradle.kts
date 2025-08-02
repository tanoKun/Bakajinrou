import org.gradle.api.tasks.testing.logging.TestLogEvent.*

group = "com.github.tanokun"
version = "1.0.0-SNAPSHOT"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.0"
}

kotlin {
    jvmToolchain(22)
}

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.bundles.junit)
    testImplementation(libs.mockk)
    testImplementation(kotlin("test"))
}

tasks.withType<Test> {
    testLogging {
        events(PASSED, FAILED, SKIPPED)
        showStandardStreams = true
    }

    useJUnitPlatform()
}