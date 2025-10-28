package com.github.tanokun.bakajinrou.plugin.rendering.tab.handler

import com.github.tanokun.bakajinrou.plugin.rendering.tab.DummyUUID
import com.github.tanokun.bakajinrou.plugin.rendering.tab.TabEngine
import com.github.tanokun.bakajinrou.plugin.rendering.tab.TabEntryComponent

class TabEngineHandlerDsl(private val engine: TabEngine) {
    fun addComponent(component: TabEntryComponent) {
        engine.addComponent(component)
    }

    fun removeComponent(dummyUuid: DummyUUID) {
        engine.removeComponent(dummyUuid)
    }

    fun updateComponent(component: TabEntryComponent) {
        engine.updateComponent(component)
    }
}