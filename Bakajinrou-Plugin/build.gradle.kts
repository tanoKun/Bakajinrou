import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

val projectVersion = "1.0.0-SNAPSHOT"

group = "com.github.tanokun"
version = projectVersion

plugins {
    kotlin("jvm")
    id("com.gradleup.shadow") version "9.0.0-rc1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("io.papermc.paperweight.userdev") version "1.7.7"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

kotlin {
    jvmToolchain(22)
}

repositories {
    mavenCentral()
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.0")

    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")

    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")

    compileOnly("dev.jorel:commandapi-bukkit-core:10.1.1")

    implementation("xyz.xenondevs.invui:invui:1.44")
    implementation("xyz.xenondevs.invui:invui-kotlin:1.44")

    implementation(project(":Bakajinrou-API"))
    implementation(project(":Bakajinrou-Bukkit"))

    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.39.0")
    testImplementation("io.mockk:mockk:1.13.2")

    testImplementation("dev.jorel:commandapi-bukkit-core:10.1.1")
    testImplementation("dev.jorel:commandapi-bukkit-test-toolkit:10.1.1")
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

