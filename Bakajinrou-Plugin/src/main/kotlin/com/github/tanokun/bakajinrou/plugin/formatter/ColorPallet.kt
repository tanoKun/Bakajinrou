package com.github.tanokun.bakajinrou.plugin.formatter

import kotlinx.serialization.Serializable

@Serializable
data class ColorPallet(val colors: HashMap<String, Int>)