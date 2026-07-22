package com.github.tanokun.bakajinrou.plugin.presentation.tab.handler

import com.github.tanokun.bakajinrou.plugin.presentation.tab.DummyUUID
import com.github.tanokun.bakajinrou.plugin.presentation.tab.TabEngine
import com.github.tanokun.bakajinrou.plugin.presentation.tab.TabEntryComponent

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