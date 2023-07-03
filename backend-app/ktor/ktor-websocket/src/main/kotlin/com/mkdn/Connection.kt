package com.mkdn

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger()
    }
    val username = "user${lastId.incrementAndGet()}"
}