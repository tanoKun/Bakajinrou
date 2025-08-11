package com.github.tanokun.bakajinrou.plugin.common.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.command.CommandSender
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.style.red
import plutoproject.adventurekt.text.text

abstract class Command {

    protected fun CommandSender.error(text: String, scope: CoroutineScope? = null) {
        if (scope == null)
            this.sendMessage(component { text(text) color red deco bold })

        scope?.launch {
            this@error.sendMessage(component { text(text) color red deco bold })
        }
    }

    protected fun CommandSender.info(text: String, scope: CoroutineScope? = null) {
        if (scope == null)
            this.sendMessage(component { text(text) color gray deco bold })

        scope?.launch {
            this@info.sendMessage(component { text(text) color gray deco bold })
        }
    }
}