package com.mkdn.plugins

import com.mkdn.Connection
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.routing.*
import java.util.Collections

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/chat") {
            send("New user connected")
            val newConnection = Connection(this)
            connections += newConnection
            try {
                send("You are now connected. There are ${connections.count()} users")
                for (frame in incoming) {
                    val frameText = frame as? Frame.Text ?: continue
                    val received = frameText.readText()
                    val userNameText = "[${newConnection.username}]: $received"
                    connections.forEach {
                        it.session.send(userNameText)
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                println("removing connection for user ${newConnection.username}")
                connections -= newConnection
            }
        }
    }
}
