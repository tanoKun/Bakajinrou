import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

val projectVersion = "1.0.0-SNAPSHOT"

group = "com.github.tanokun"
version = projectVersion

plugins {
    kotlin("jvm")
    alias(libs.plugins.bukkit)
    alias(libs.plugins.shadow)
    alias(libs.plugins.paperweight)
}

kotlin {
    jvmToolchain(22)
}

repositories {
    mavenCentral()
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://maven.nostal.ink/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)

    compileOnly(libs.protocollib)
    compileOnly(libs.commandapi)

    implementation(libs.bundles.invui)
    implementation(libs.adventurekt)

    implementation(project(":Bakajinrou-API"))
    implementation(project(":Bakajinrou-Game"))

    testImplementation(libs.bundles.mocks)

    testImplementation(libs.bundles.commandapi)
    testImplementation(kotlin("test"))
}

configurations.testImplementation {
    exclude("io.papermc.paper", "paper-server")
}

tasks.withType<Test> {
    testLogging {
        events(PASSED, FAILED, SKIPPED)
        showStandardStreams = true
    }

    useJUnitPlatform()
}

tasks.named("shadowJar") {
    dependsOn(subprojects.map { it.tasks.named("test") })
}

tasks.shadowJar {
    archiveBaseName.set("BakaJinrou")
    archiveVersion.set(projectVersion)

    mergeServiceFiles()
}


bukkit {
    main = "com.github.tanokun.bakajinrou.plugin.BakaJinrou"

    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    apiVersion = "1.21"
    authors = listOf("tanoKun")
    version = projectVersion

    depend = arrayListOf(
        "ProtocolLib",
        "CommandAPI"
    )
}

