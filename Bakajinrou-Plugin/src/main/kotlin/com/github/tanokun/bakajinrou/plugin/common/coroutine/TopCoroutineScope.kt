package com.github.tanokun.bakajinrou.plugin.common.coroutine

import kotlinx.coroutines.CoroutineScope

class TopCoroutineScope(original: CoroutineScope): CoroutineScope by original