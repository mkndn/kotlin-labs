package com.mkdn

import com.mkdn.models.orderStorage
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class OrderRouteTests {
    @Test
    fun `Api returns all Orders`() = testApplication {
        val response = client.request("/orders")
        val jsonString = """
            [{"orderNumber":"2020-04-06-01","items":[{"name":"Ham Sandwich","quantity":2,"price":5.5},{"name":"Water","quantity":1,"price":1.5},{"name":"Beer","quantity":3,"price":2.3},{"name":"Cheesecake","quantity":1,"price":3.75}]},{"orderNumber":"2020-04-03-01","items":[{"name":"Cheeseburger","quantity":1,"price":8.5},{"name":"Water","quantity":2,"price":1.5},{"name":"Coke","quantity":2,"price":1.76},{"name":"Ice Cream","quantity":1,"price":2.35}]}]
        """.trimIndent()
        assertEquals(jsonString, response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `Api returns a single order`() = testApplication {
        val response = client.request("/orders/2020-04-06-01")
        val order = orderStorage.find { it.orderNumber == "2020-04-06-01" }
        assertNotNull(order)
        val jsonString = """
            {"orderNumber":"2020-04-06-01","items":[{"name":"Ham Sandwich","quantity":2,"price":5.5},{"name":"Water","quantity":1,"price":1.5},{"name":"Beer","quantity":3,"price":2.3},{"name":"Cheesecake","quantity":1,"price":3.75}]}
        """.trimIndent()
        assertEquals(jsonString, response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `Api returns not found when using invalid order number`() = testApplication {
        val response = client.request("/orders/3030-100-060-101")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}