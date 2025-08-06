package com.github.tanokun.bakajinrou.plugin.localization

import kotlinx.serialization.Serializable

@Serializable
data class Dictionary(val vocabularies: Map<String, String>)