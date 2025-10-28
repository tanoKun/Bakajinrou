import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

val projectVersion = "1.0.0-SNAPSHOT"

group = "com.github.tanokun"
version = projectVersion

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    alias(libs.plugins.paper)
    alias(libs.plugins.shadow)
    alias(libs.plugins.paperweight)
    alias(libs.plugins.ksp)
}

kotlin {
    jvmToolchain(22)
}

ksp {
    arg("koin.ksp.options.displayGraph", "true")
}

repositories {
    mavenCentral()
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://maven.nostal.ink/repository/maven-public/")
    maven("https://repo.triumphteam.dev/snapshots")
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)

    compileOnly(libs.protocollib)
    compileOnly(libs.commandapi)

    compileOnly(libs.invui)
    implementation(libs.adventurekt)
    implementation(libs.bundles.mccoroutine)

    implementation(kotlin("reflect"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.mordant)

    implementation(libs.bundles.koin)
    ksp(libs.koin.ksp.compiler)

    implementation(project(":Bakajinrou-API"))
    implementation(project(":Bakajinrou-Game"))

    testImplementation(libs.bundles.kotest)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.bundles.mocks)

    testImplementation(libs.bundles.commandapi)
    testImplementation(kotlin("test"))
}

configurations.testImplementation {
    exclude("io.papermc.paper", "paper-server")
}

paperweight {
    addServerDependencyTo = configurations.named(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME).map { setOf(it) }
}

tasks.withType<Test> {
    testLogging {
        events(PASSED, FAILED, SKIPPED)
        showStandardStreams = true
    }

    useJUnitPlatform()

}

tasks {
    runServer {
        minecraftVersion("1.21.4")
        downloadPlugins {
            url("https://github.com/CommandAPI/CommandAPI/releases/download/10.1.1/CommandAPI-10.1.1-Mojang-Mapped.jar")
            url("https://github.com/dmulloy2/ProtocolLib/releases/download/5.4.0/ProtocolLib.jar")
        }
    }
}

paper {
    name = "Bakajinrou"

    main = "com.github.tanokun.bakajinrou.plugin.BakaJinrou"
    loader = "com.github.tanokun.bakajinrou.plugin.BakaJinrouPluginLoader"

    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    apiVersion = "1.21"
    authors = listOf("tanoKun")
    version = projectVersion

    serverDependencies {
        register("CommandAPI")
        register("ProtocolLib")
    }

    permissions {
        register("testplugin.command.mapsetting") {
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}
