package com.konvi.routing.integration

import com.konvi.auth.basic.BasicAuthenticator
import com.konvi.auth.jwt.JwtAuthenticator
import com.konvi.auth.jwt.JwtService
import com.konvi.di.KonviComponent
import com.konvi.lifecycle.Lifecycle
import com.konvi.routing.middleware.AuthMiddleware
import com.konvi.routing.router
import com.zaxxer.hikari.HikariDataSource
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

    private class TestComponent : KonviComponent() {
        override val authMiddleware: AuthMiddleware get() = error("unused in routing tests")
        override val basicAuthenticator: BasicAuthenticator get() = error("unused in routing tests")
        override val jwtAuthenticator: JwtAuthenticator get() = error("unused in routing tests")
        override val jwtService: JwtService get() = error("unused in routing tests")
        override val lifecycle: List<Lifecycle> get() = emptyList()
        override val hikariDataSource: HikariDataSource get() = error("unused in routing tests")
    }

    private val component = TestComponent()

    @Test
    fun `handler runs when there is no middleware`() = testApplication {
        var handlerRan = false
        val konvi = component.router {
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
        val konvi = component.router {
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
        val konvi = component.router {
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
        val konvi = component.router {
            get("/x", handler = { handlerRan = true; respond(HttpStatusCode.OK, "HANDLER") }, first, second)
        }
        application { routing { konvi.block(this) } }

        val response = client.get("/x")

        assertEquals("BLOCKED", response.bodyAsText())
        assertFalse(secondRan, "later middleware must not run")
        assertFalse(handlerRan, "handler must not run")
    }

    @Test
    fun `middleware does not leak to a sibling sub-path route`() = testApplication {
        var middlewareRan = false
        val blocker: suspend (ApplicationCall) -> Unit = {
            middlewareRan = true
            it.respond(HttpStatusCode.Forbidden, "BLOCKED")
        }
        val konvi = component.router {
            get("/users", handler = { respond(HttpStatusCode.OK, "ALL") }, blocker)
            get("/users/{id}", handler = { respond(HttpStatusCode.OK, "ONE") })
        }
        application { routing { konvi.block(this) } }

        val protected = client.get("/users")
        assertEquals(HttpStatusCode.Forbidden, protected.status, "middleware should guard GET /users")
        assertEquals("BLOCKED", protected.bodyAsText())

        middlewareRan = false
        val sibling = client.get("/users/123")
        assertFalse(middlewareRan, "middleware on /users must not run for the /users/{id} sibling")
        assertEquals(HttpStatusCode.OK, sibling.status)
        assertEquals("ONE", sibling.bodyAsText())
    }

    @Test
    fun `group middleware does not leak to a sibling route outside the group`() = testApplication {
        var middlewareRan = false
        val blocker: suspend (ApplicationCall) -> Unit = {
            middlewareRan = true
            it.respond(HttpStatusCode.Forbidden, "BLOCKED")
        }
        val konvi = component.router {
            group("/admin", blocker) {
                get("/dashboard", handler = { respond(HttpStatusCode.OK, "DASH") })
            }
            get("/admin/{id}", handler = { respond(HttpStatusCode.OK, "ONE") })
        }
        application { routing { konvi.block(this) } }

        val protected = client.get("/admin/dashboard")
        assertEquals(HttpStatusCode.Forbidden, protected.status, "group middleware should guard routes inside the group")
        assertEquals("BLOCKED", protected.bodyAsText())

        middlewareRan = false
        val sibling = client.get("/admin/123")
        assertFalse(middlewareRan, "group middleware must not run for a sibling route outside the group")
        assertEquals(HttpStatusCode.OK, sibling.status)
        assertEquals("ONE", sibling.bodyAsText())
    }
}
