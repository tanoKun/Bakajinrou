import org.gradle.api.tasks.testing.logging.TestLogEvent.*

group = "com.github.tanokun"

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
    testImplementation(libs.bundles.kotest)
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
