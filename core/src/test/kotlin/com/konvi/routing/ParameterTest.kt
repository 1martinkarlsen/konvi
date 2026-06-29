package com.konvi.routing

import com.konvi.exception.configureExceptions
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class ParameterTest {

    // A real RoutingCall is only available inside a running application, so the public
    // accessors are exercised through testApplication (the same pattern MiddlewareIntegrationTest
    // uses). configureExceptions maps BadRequestException -> 400 and anything else -> 500,
    // which is what lets the error-path assertions distinguish client vs programmer errors.
    private fun ApplicationTestBuilder.startApp() {
        application {
            install(ContentNegotiation) { json() }
            configureExceptions()
            routing {
                // --- path parameters: one route per supported type ---
                get("/path/string/{v}") { call.respondText(call.pathParameter<String>("v")) }
                get("/path/int/{v}") { call.respondText(call.pathParameter<Int>("v").toString()) }
                get("/path/long/{v}") { call.respondText(call.pathParameter<Long>("v").toString()) }
                get("/path/double/{v}") { call.respondText(call.pathParameter<Double>("v").toString()) }
                get("/path/bool/{v}") { call.respondText(call.pathParameter<Boolean>("v").toString()) }
                get("/path/uuid/{v}") { call.respondText(call.pathParameter<UUID>("v").toString()) }

                // nullable path parameter
                get("/path/int-nullable/{v}") { call.respondText(call.pathParameter<Int?>("v").toString()) }

                // routes whose handler asks for a parameter the path does not declare,
                // so pathParameters["v"] is null (the "missing" branch)
                get("/path/missing-nonnull") { call.respondText(call.pathParameter<Int>("v").toString()) }
                get("/path/missing-nullable") { call.respondText(call.pathParameter<Int?>("v").toString()) }

                // --- query parameters ---
                get("/query/int") { call.respondText(call.queryParameter<Int>("v").toString()) }
                get("/query/int-nullable") { call.respondText(call.queryParameter<Int?>("v").toString()) }
            }
        }
    }

    // region path parameters – valid conversions

    @Test
    fun `path parameter parses String`() = testApplication {
        startApp()
        val res = client.get("/path/string/hello")
        assertEquals(HttpStatusCode.OK, res.status)
        assertEquals("hello", res.bodyAsText())
    }

    @Test
    fun `path parameter parses Int`() = testApplication {
        startApp()
        val res = client.get("/path/int/5")
        assertEquals(HttpStatusCode.OK, res.status)
        assertEquals("5", res.bodyAsText())
    }

    @Test
    fun `path parameter parses Long`() = testApplication {
        startApp()
        val res = client.get("/path/long/5000000000")
        assertEquals(HttpStatusCode.OK, res.status)
        assertEquals("5000000000", res.bodyAsText())
    }

    @Test
    fun `path parameter parses Double`() = testApplication {
        startApp()
        val res = client.get("/path/double/3.14")
        assertEquals(HttpStatusCode.OK, res.status)
        assertEquals("3.14", res.bodyAsText())
    }

    @Test
    fun `path parameter parses Boolean`() = testApplication {
        startApp()
        assertEquals("true", client.get("/path/bool/true").bodyAsText())
        assertEquals("false", client.get("/path/bool/false").bodyAsText())
    }

    @Test
    fun `path parameter parses UUID`() = testApplication {
        startApp()
        val id = "123e4567-e89b-12d3-a456-426614174000"
        val res = client.get("/path/uuid/$id")
        assertEquals(HttpStatusCode.OK, res.status)
        assertEquals(id, res.bodyAsText())
    }

    // endregion

    // region path parameters – invalid present value -> 400

    @Test
    fun `path parameter rejects non-numeric Int as 400`() = testApplication {
        startApp()
        assertEquals(HttpStatusCode.BadRequest, client.get("/path/int/abc").status)
    }

    @Test
    fun `path parameter rejects non-numeric Long as 400`() = testApplication {
        startApp()
        assertEquals(HttpStatusCode.BadRequest, client.get("/path/long/abc").status)
    }

    @Test
    fun `path parameter rejects non-numeric Double as 400`() = testApplication {
        startApp()
        assertEquals(HttpStatusCode.BadRequest, client.get("/path/double/abc").status)
    }

    @Test
    fun `path parameter rejects non-boolean as 400`() = testApplication {
        startApp()
        // toBooleanStrictOrNull only accepts exactly "true"/"false"
        assertEquals(HttpStatusCode.BadRequest, client.get("/path/bool/maybe").status)
    }

    @Test
    fun `path parameter rejects malformed UUID as 400`() = testApplication {
        startApp()
        assertEquals(HttpStatusCode.BadRequest, client.get("/path/uuid/not-a-uuid").status)
    }

    // endregion

    // region nullability

    @Test
    fun `nullable path parameter returns value when present`() = testApplication {
        startApp()
        val res = client.get("/path/int-nullable/7")
        assertEquals(HttpStatusCode.OK, res.status)
        assertEquals("7", res.bodyAsText())
    }

    @Test
    fun `nullable path parameter rejects present-but-invalid value as 400`() = testApplication {
        startApp()
        // '?' means "may be absent", not "may be garbage": a present invalid value still 400s
        assertEquals(HttpStatusCode.BadRequest, client.get("/path/int-nullable/abc").status)
    }

    // endregion

    // region missing parameters – path vs query differ by design

    @Test
    fun `missing non-null path parameter is a 500 programmer error`() = testApplication {
        startApp()
        // routing can't match /users/{id} without the segment, so a missing path param means a
        // typo'd name -> IllegalArgumentException -> 500, not a client error
        assertEquals(HttpStatusCode.InternalServerError, client.get("/path/missing-nonnull").status)
    }

    @Test
    fun `missing nullable path parameter returns null`() = testApplication {
        startApp()
        val res = client.get("/path/missing-nullable")
        assertEquals(HttpStatusCode.OK, res.status)
        assertEquals("null", res.bodyAsText())
    }

    @Test
    fun `missing non-null query parameter is a 400 client error`() = testApplication {
        startApp()
        assertEquals(HttpStatusCode.BadRequest, client.get("/query/int").status)
    }

    @Test
    fun `missing nullable query parameter returns null`() = testApplication {
        startApp()
        val res = client.get("/query/int-nullable")
        assertEquals(HttpStatusCode.OK, res.status)
        assertEquals("null", res.bodyAsText())
    }

    // endregion

    // region query parameters

    @Test
    fun `query parameter parses Int when present`() = testApplication {
        startApp()
        val res = client.get("/query/int?v=42")
        assertEquals(HttpStatusCode.OK, res.status)
        assertEquals("42", res.bodyAsText())
    }

    @Test
    fun `query parameter rejects present-but-invalid value as 400`() = testApplication {
        startApp()
        assertEquals(HttpStatusCode.BadRequest, client.get("/query/int?v=abc").status)
    }

    // endregion
}
