package com.mkdn.plugins

import com.mkdn.routes.customerRouting
import com.mkdn.routes.getOrder
import com.mkdn.routes.getOrderTotal
import com.mkdn.routes.listOrders
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        customerRouting()
        listOrders()
        getOrder()
        getOrderTotal()
    }
}
