import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

val projectVersion = "1.0.0-SNAPSHOT"

group = "com.github.tanokun"
version = projectVersion

plugins {
    kotlin("jvm")
    alias(libs.plugins.paper)
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
    maven("https://repo.triumphteam.dev/snapshots")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)

    compileOnly(libs.protocollib)
    compileOnly(libs.commandapi)

    compileOnly(libs.invui)
    implementation(libs.adventurekt)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.bundles.mccoroutine)

    implementation(project(":Bakajinrou-API"))
    implementation(project(":Bakajinrou-Game"))

    testImplementation(libs.kotlinx.coroutines.test)
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

tasks {
    val copyJarToPlugins by registering(Copy::class) {
        dependsOn(shadowJar)
        from(shadowJar.get().archiveFile)
        into("C:/Users/owner/Desktop/1.21 paper/plugins")
        rename {
            "plugin.jar"
        }
    }

    shadowJar {
        dependsOn(subprojects.map { it.tasks.named("test") })

        archiveBaseName.set("BakaJinrou")
        archiveVersion.set(projectVersion)

        mergeServiceFiles()
        finalizedBy(copyJarToPlugins)
    }
}

paper {
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
}
