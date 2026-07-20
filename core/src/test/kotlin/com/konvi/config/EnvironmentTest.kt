package com.konvi.config

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EnvironmentTest {

    @AfterTest
    fun teardown() {
        System.clearProperty("konvi.environment")
    }

    @Test
    fun `resolve defaults to DEVELOPMENT when unset`() {
        assertEquals(Environment.DEVELOPMENT, Environment.resolve())
    }

    @Test
    fun `resolve reads development from system property fallback`() {
        System.setProperty("konvi.environment", "development")
        assertEquals(Environment.DEVELOPMENT, Environment.resolve())
    }

    @Test
    fun `resolve reads dev shorthand from system property fallback`() {
        System.setProperty("konvi.environment", "dev")
        assertEquals(Environment.DEVELOPMENT, Environment.resolve())
    }

    @Test
    fun `resolve reads production from system property fallback`() {
        System.setProperty("konvi.environment", "production")
        assertEquals(Environment.PRODUCTION, Environment.resolve())
    }

    @Test
    fun `resolve reads prod shorthand from system property fallback`() {
        System.setProperty("konvi.environment", "prod")
        assertEquals(Environment.PRODUCTION, Environment.resolve())
    }

    @Test
    fun `resolve treats unknown values as PRODUCTION fail-safe`() {
        System.setProperty("konvi.environment", "staging")
        assertEquals(Environment.PRODUCTION, Environment.resolve())
    }

    @Test
    fun `resolve is case-insensitive`() {
        System.setProperty("konvi.environment", "PRODUCTION")
        assertEquals(Environment.PRODUCTION, Environment.resolve())
    }
}
