package com.konvi.routing.integration

import com.konvi.routing.router
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MiddlewareIntegrationTest {

    @Test
    fun `handler runs when there is no middleware`() = testApplication {
        var handlerRan = false
        val konvi = router {
            get("/x", handler = { handlerRan = true; respond(HttpStatusCode.OK, "HANDLER") })
        }
        application { routing { konvi.block(this) } }

        val response = client.get("/x")

        assertTrue(handlerRan, "handler should run with no middleware")
        assertEquals("HANDLER", response.bodyAsText())
    }

    @Test
    fun `passive middleware does not block the handler`() = testApplication {
        var handlerRan = false
        val passive: suspend (ApplicationCall) -> Unit = { /* does not respond */ }
        val konvi = router {
            get("/x", handler = { handlerRan = true; respond(HttpStatusCode.OK, "HANDLER") }, passive)
        }
        application { routing { konvi.block(this) } }

        val response = client.get("/x")

        assertTrue(handlerRan, "handler should run when middleware does not respond")
        assertEquals("HANDLER", response.bodyAsText())
    }

    @Test
    fun `middleware that responds short-circuits the handler`() = testApplication {
        var handlerRan = false
        val blocker: suspend (ApplicationCall) -> Unit = { it.respond(HttpStatusCode.Forbidden, "BLOCKED") }
        val konvi = router {
            get("/x", handler = { handlerRan = true; respond(HttpStatusCode.OK, "HANDLER") }, blocker)
        }
        application { routing { konvi.block(this) } }

        val response = client.get("/x")

        assertEquals(HttpStatusCode.Forbidden, response.status, "middleware response should win")
        assertEquals("BLOCKED", response.bodyAsText())
        assertFalse(handlerRan, "handler must not run after a middleware handled the call")
    }

    @Test
    fun `responding middleware short-circuits later middleware and handler`() = testApplication {
        var secondRan = false
        var handlerRan = false
        val first: suspend (ApplicationCall) -> Unit = { it.respond(HttpStatusCode.Forbidden, "BLOCKED") }
        val second: suspend (ApplicationCall) -> Unit = { secondRan = true }
        val konvi = router {
            get("/x", handler = { handlerRan = true; respond(HttpStatusCode.OK, "HANDLER") }, first, second)
        }
        application { routing { konvi.block(this) } }

        val response = client.get("/x")

        assertEquals("BLOCKED", response.bodyAsText())
        assertFalse(secondRan, "later middleware must not run")
        assertFalse(handlerRan, "handler must not run")
    }
}
