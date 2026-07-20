package com.konvi.config

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfigLoaderTest {

    @AfterTest
    fun teardown() {
        System.clearProperty("konvi.port")
        System.clearProperty("konvi.database.url")
        System.clearProperty("konvi.auth.jwt.secret")
        System.clearProperty("konvi.cors.allowedOrigins")
    }

    @Test
    fun `loadConfig uses Kotlin defaults for keys absent from application yaml`() {
        val config = loadConfig()
        assertEquals(8080, config.port)
        assertEquals("change-me", config.auth.jwt.secret)
    }

    @Test
    fun `loadConfig reads a scalar value from application yaml`() {
        val config = loadConfig()
        assertEquals("yaml-issuer", config.auth.jwt.issuer)
    }

    @Test
    fun `loadConfig reads a list value from application yaml`() {
        // Guards the list() path: a YAML list must resolve via getList(), not getString()
        val config = loadConfig()
        assertEquals(
            listOf("https://one.example.com", "https://two.example.com"),
            config.cors.allowedOrigins
        )
    }

    @Test
    fun `loadConfig prefers system property over default`() {
        System.setProperty("konvi.port", "9090")
        System.setProperty("konvi.auth.jwt.secret", "from-system-property")

        val config = loadConfig()

        assertEquals(9090, config.port)
        assertEquals("from-system-property", config.auth.jwt.secret)
    }

    @Test
    fun `loadConfig prefers a system property override over an application yaml list`() {
        System.setProperty("konvi.cors.allowedOrigins", "https://a.example.com, https://b.example.com")

        val config = loadConfig()

        assertEquals(listOf("https://a.example.com", "https://b.example.com"), config.cors.allowedOrigins)
    }
}
