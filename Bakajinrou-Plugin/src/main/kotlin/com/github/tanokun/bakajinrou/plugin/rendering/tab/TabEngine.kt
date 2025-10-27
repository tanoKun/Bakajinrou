package com.github.tanokun.bakajinrou.plugin.rendering.tab

class TabEngine {
    private val components = hashMapOf<DummyUUID, TabEntryComponent>()

    private val updateDiff = hashMapOf<DummyUUID, TabEntryComponent>()
    private val addDiff = hashMapOf<DummyUUID, TabEntryComponent>()
    private val removeDiff = arrayListOf<DummyUUID>()

    private val renderers = mutableListOf<TabRenderer>()

    fun removeComponent(dummyUuid: DummyUUID) {
        components.remove(dummyUuid) ?: return

        updateDiff.remove(dummyUuid)
        addDiff.remove(dummyUuid)
        removeDiff.add(dummyUuid)
    }

    fun addComponent(component: TabEntryComponent) {
        if (components.containsKey(component.dummyUuid)) throw IllegalStateException("Component already exists")

        components[component.dummyUuid] = component

        addDiff[component.dummyUuid] = component
        updateDiff.remove(component.dummyUuid)
        removeDiff.remove(component.dummyUuid)
    }

    fun updateComponent(component: TabEntryComponent) {
        if (addDiff.containsKey(component.dummyUuid)) throw IllegalStateException("Component is newly added, cannot update")
        if (!components.containsKey(component.dummyUuid)) throw IllegalStateException("Component does not exist")
        if (components[component.dummyUuid] == component) return

        components[component.dummyUuid] = component

        updateDiff[component.dummyUuid] = component
        removeDiff.remove(component.dummyUuid)
    }

    /**
     * 差分を各描画者に適用します。
     */
    fun applyDifferences() {
        renderers.forEach { renderer -> renderer.renderUpdate(updateDiff.values) }
        renderers.forEach { renderer -> renderer.renderInitialization(addDiff.values) }
        renderers.forEach { renderer -> renderer.renderRemoval(removeDiff.map { it.uuid }) }

        updateDiff.clear()
        addDiff.clear()
        removeDiff.clear()
    }

    fun registerRenderer(renderer: TabRenderer) {
        if (renderers.contains(renderer)) throw IllegalStateException("Renderer is already registered")
        renderers.add(renderer)

        renderer.renderInitialization(components.values)
    }

    fun unregisterRenderer(renderer: TabRenderer) {
        if (!renderers.contains(renderer)) throw IllegalStateException("Renderer is not registered")
        renderers.remove(renderer)

        renderer.renderRemoval(components.keys.map { it.uuid })
    }
}