package com.github.tanokun.bakajinrou.plugin

import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository


class BakaJinrouPluginLoader : PluginLoader {
    override fun classloader(pluginClasspathBuilder: PluginClasspathBuilder) {
        val resolver = MavenLibraryResolver().apply {
            addRepository(RemoteRepository.Builder("xenondevs", "default", "https://repo.xenondevs.xyz/releases/").build())
        }

        resolver.addDependency(Dependency(DefaultArtifact("xyz.xenondevs.invui:invui:pom:1.46"), null))
        pluginClasspathBuilder.addLibrary(resolver)
    }
}