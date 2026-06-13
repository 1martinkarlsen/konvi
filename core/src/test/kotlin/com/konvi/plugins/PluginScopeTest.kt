package com.konvi.plugins

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.Plugin
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.CallSetup
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PluginScopeTest {

    // Mirrors the delegating scope built inside Konvi.start, so these tests exercise
    // the same PluginScope contract the framework relies on.
    private fun Application.pluginScope(): PluginScope {
        val app = this
        return object : PluginScope {
            override fun <B : Any, F : Any> install(plugin: Plugin<Application, B, F>, configure: B.() -> Unit) {
                app.install(plugin, configure)
            }
        }
    }

    private class GreeterConfig {
        var greeting: String = ""
    }

    private fun greeterPlugin(record: MutableList<String>) =
        createApplicationPlugin(name = "Greeter", createConfiguration = ::GreeterConfig) {
            record += pluginConfig.greeting
        }

    @Test
    fun `install runs the config block`() = testApplication {
        val recorded = mutableListOf<String>()
        application {
            pluginScope().install(greeterPlugin(recorded)) { greeting = "hello" }
            routing { get("/x") { call.respond(HttpStatusCode.OK, "OK") } }
        }

        client.get("/x") // force the application block to run

        assertEquals(listOf("hello"), recorded, "config block should run with greeting='hello'")
    }

    @Test
    fun `installed plugin participates in the request pipeline`() = testApplication {
        var sawCall = false
        val tracer = createApplicationPlugin(name = "Tracer") {
            on(CallSetup) { sawCall = true }
        }
        application {
            pluginScope().install(tracer)
            routing { get("/x") { call.respond(HttpStatusCode.OK, "OK") } }
        }

        client.get("/x")

        assertTrue(sawCall, "the installed plugin should run for incoming calls")
    }

    @Test
    fun `routes still serve when no plugin is installed`() = testApplication {
        application {
            routing { get("/x") { call.respond(HttpStatusCode.OK, "OK") } }
        }

        val response = client.get("/x")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("OK", response.bodyAsText())
    }

    @Test
    fun `installing the same plugin twice fails fast`() = testApplication {
        val recorded = mutableListOf<String>()
        val plugin = greeterPlugin(recorded)
        application {
            val scope = pluginScope()
            scope.install(plugin) { greeting = "a" }
            assertFailsWith<Exception> {
                scope.install(plugin) { greeting = "b" }
            }
        }

        client.get("/") // force the application block to run
    }
}
