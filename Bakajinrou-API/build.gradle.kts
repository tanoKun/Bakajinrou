import org.gradle.api.tasks.testing.logging.TestLogEvent.*

group = "com.github.tanokun"
version = "1.0.0-SNAPSHOT"

plugins {
    kotlin("jvm")
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