import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")

    val client = HttpClient {
        install(WebSockets)
    }

    runBlocking {
        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/chats") {
            val messageOutputRoutine = launch { outputMessages() }
            val messageInputRoutine = launch { inputMessages() }

            messageInputRoutine.join()
            messageOutputRoutine.cancelAndJoin()
        }
    }
    client.close()
    println("Good bye! See you again!")
}

suspend fun DefaultWebSocketSession.outputMessages() {
    try {
        for (message in incoming) {
            val textMessage = message as? Frame.Text ?: continue
            println(textMessage.readText())
        }
    } catch(e: Exception) {
        println("Exception while receiving message: ${e.localizedMessage}")
    }
}

suspend fun DefaultWebSocketSession.inputMessages() {
    while(true) {
        val message = readlnOrNull() ?: ""
        if (message.equals("exit", true)) return
        try {
            send(message)
        } catch(e: Exception) {
            println("Error while sending: ${e.localizedMessage}")
            return
        }
    }
}