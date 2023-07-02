package com.mkdn.routes

import com.mkdn.models.orderStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.listOrders() {
    get("/orders") {
        if (orderStorage.isNotEmpty()) {
            call.respond(orderStorage)
        } else {
            call.respondText("No orders found", status = HttpStatusCode.OK)
        }
    }
}

fun Route.getOrder() {
    get("/orders/{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )

        val order = orderStorage
            .find { it.orderNumber == id } ?: return@get call.respondText(
                "Order with id $id not found",
                status = HttpStatusCode.NotFound
            )

        call.respond(order)
    }
}

fun Route.getOrderTotal() {
    get("/orders/{id?}/total") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )

        val order = orderStorage
            .find { it.orderNumber == id } ?: return@get call.respondText(
            "Order with id $id not found",
            status = HttpStatusCode.NotFound
        )

        val total = order.items.sumOf { it.price * it.quantity }
        call.respond(total)
    }
}