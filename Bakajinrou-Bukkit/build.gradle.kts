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
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")

    implementation(project(":Bakajinrou-API"))

    testImplementation("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    testImplementation(kotlin("test"))
}

tasks.withType<Test> {
    testLogging {
        events(PASSED, FAILED, SKIPPED)
        showStandardStreams = true
    }

    useJUnitPlatform()
}